package Earth;

abstract class AnyCell{
    int xPos;//позиция в мире
    int yPos;//позиция в мире
    World myWorld;//ссылка на мир
    abstract public boolean isEmptyCell();
    abstract public int getColor();

    public int getNewXPosOnStep(int eyeDirection, int xPos){
        return switch (eyeDirection){
            case 1:
            case 2:
            case 3:
                yield xPos+1;
            case 5:
            case 6:
            case 7:
                yield xPos-1;
            default:
                yield xPos;
        };
    }

    public int getNewYPosOnStep(int eyeDirection, int yPos){
        return switch (eyeDirection){
            case 7:
            case 0:
            case 1:
                yield yPos+1;
            case 3:
            case 4:
            case 5:
                yield yPos-1;
            default:
                yield yPos;
        };
    }
}

class Cell extends AnyCell {
    private float energy;//энергия клетки (1-100)
    private final int color;//цвет (0- пустая клетка)
    private float fightLevel=0;//уровень мастерства драки (0-100)
    private int eyeDirection=0;//напрвление взгляда (0 - вверх)
    private int attacked=0;//(0..10) флаг атаки. Опускается на 1 за каждый следующий шаг

/*дальше всё что касается мозга*/
    private int hidenLayersCnt;//кол-во скрытых уровней
    private int hidenLayersPow;//кол-во нейронов в каждом уровне
    private final int INPUT_SIGNAL_COUNT = 6;//кол-во входных нейронов
    /*
    1- уровень энергии
    2- уровень мастерства драки
    3- уровень атаки
    4- бот перед ним или нет
    5- если бот, то одного цвета или нет
    6- кол-во собратьев рядом
    */
    private final int OUTPUT_SIGNAL_COUNT = 4;//кол-во выходных нейронов
    /*
    1- есть
    2- идти
    3- напасть
    4- делиться
    */
    public void live(){//функция жизни, вызывается раз за ход

        reduceEnergy(myWorld.FOOD_LEVEL_PER_STEP);
        reduceAttacked();
    }

    private void setRndBrain(){//инициализация мозга
        hidenLayersCnt = (int) Math.round((Math.random() * 7) + 3);
        hidenLayersPow = (int) Math.round((Math.random() * 7) + 3);
        int hidenLayersPowTMP = Math.max(hidenLayersPow,INPUT_SIGNAL_COUNT);

    }

    private void reduceAttacked(){
        attacked = (attacked>0)?attacked--:0;
    }

    private void step(){//шагаем по направлению взгляда
        int newX = getNewXPosOnStep(eyeDirection,xPos);
        int newY = getNewYPosOnStep(eyeDirection,yPos);

        if (newX <0)newX=0;
        if (newX >=myWorld.WEIGHT)newX=myWorld.WEIGHT-1;
        if (newY <0)newY=0;
        if (newY >=myWorld.HEIGHT)newY=myWorld.HEIGHT-1;

        if (myWorld.isEmptyCell(newX,newY)){
            EmptyCell tmpCell = (EmptyCell) myWorld.cellArray[newY][newX];
            myWorld.cellArray[newY][newX] = this;
            myWorld.cellArray[yPos][xPos] = tmpCell;
            tmpCell.xPos = this.xPos;
            tmpCell.yPos = this.yPos;
            this.xPos = newX;
            this.yPos = newY;
        }
    }

    private void eat(){//едим
        if(!myWorld.isCellInWater(this)){ addEnergy(myWorld.getFoodLevel());};
    }

    private void atack(){
        int opponentX = getNewXPosOnStep(eyeDirection,xPos);
        int opponentY = getNewYPosOnStep(eyeDirection,yPos);

        if ((opponentX >= 0)&(opponentX < myWorld.WEIGHT)&(opponentY >= 0)&(opponentY <= myWorld.HEIGHT)){
            if(! myWorld.isEmptyCell(opponentX,opponentY)){
                fight((Cell)myWorld.cellArray[opponentY][opponentX]);
            }
        }
    }

    private void fight(Cell opponentCell){//деремся с соперником
        Cell winner;
        Cell looser;
        opponentCell.setAttacked();
        if ((this.energy * this.fightLevel) > (opponentCell.energy*opponentCell.fightLevel)){//определяем победителя
            winner = this;
            looser = opponentCell;
        }else{
            winner = opponentCell;
            looser = this;
        };

        float deltaEnergy = looser.energy * winner.fightLevel / 100;//передача энергии
        winner.fightLevel += 1;
        looser.reduceEnergy(deltaEnergy);
        winner.addEnergy(deltaEnergy);
    }

    public Cell(World world, int xPos, int yPos, float energy, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.energy = energy;
        this.color = color;
        myWorld = world;
        myWorld.cellCnt +=1;
        setRndBrain();
    }

    private void die(){
        myWorld.cellArray[yPos][xPos] = new EmptyCell(xPos,yPos);
        myWorld.cellCnt -=1;
    }

    private float getEnergy() {
        return energy;
    }

    private void addEnergy(float energy) {
        this.energy += energy;
        this.energy = (this.energy>100)?100:this.energy;
    }

    private float reduceEnergy(float energy){
        if (this.energy > energy){
            this.energy -= energy;
            return energy;
        }else {
            die();
            return this.energy;
        }
    }

    @Override
    public boolean isEmptyCell() {
        return false;
    }

    @Override
    public int getColor() {
        return color;
    }

    public float getFightLevel() {
        return fightLevel;
    }

    public int getAttacked() {
        return attacked;
    }

    public void setAttacked() {
        this.attacked = 10;
    }
}

class EmptyCell extends AnyCell{
    public EmptyCell(int xPos, int yPos){
        this.xPos = xPos;
        this.yPos = yPos;
    }
    @Override
    public boolean isEmptyCell() {
        return true;
    }

    @Override
    public int getColor() {
        return 0;
    }
}