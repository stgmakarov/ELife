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
    private final int color;//цвет
    private int eyeDirection=0;//напрвление взгляда (0 - вверх)
    private float fightLevel=0;//уровень мастерства драки (0-100)

    private void step(){
        int newX;
        int newY;
        newX = getNewXPosOnStep(eyeDirection,xPos);
        newY = getNewYPosOnStep(eyeDirection,yPos);

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

    public void fight(Cell opponentCell){
        Cell winner;
        Cell looser;
        if ((this.energy * this.fightLevel) > (opponentCell.energy*opponentCell.fightLevel)){
            winner = this;
            looser = opponentCell;
        }else{
            winner = opponentCell;
            looser = this;
        };

        float deltaEnergy = looser.energy * winner.fightLevel / 100;
        winner.fightLevel += 1;
        looser.reduceEnergy(deltaEnergy);
        winner.addEnergy(deltaEnergy);
    }

    Cell(World world, int xPos, int yPos, float energy, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.energy = energy;
        this.color = color;
        myWorld = world;
        myWorld.cellCnt +=1;
    }

    private void die(){
        myWorld.cellArray[yPos][xPos] = new EmptyCell(xPos,yPos);
        myWorld.cellCnt -=1;
    }

    public float getEnergy() {
        return energy;
    }

    public void addEnergy(float energy) {
        this.energy += energy;
        this.energy = (this.energy>100)?100:this.energy;
    }

    public float reduceEnergy(float energy){
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