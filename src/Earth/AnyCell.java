package Earth;

import java.awt.*;

public abstract class AnyCell{
    public int xPos;//позиция в мире
    public int yPos;//позиция в мире
    public boolean cellInWater;
    World myWorld;//ссылка на мир
    abstract public boolean isEmptyCell();
    abstract public int getColor();
    abstract public Color getRealColor();

    public void setCellInWater(int waterLevel){
        if(this.yPos < waterLevel)
        {
            cellInWater = true;
        }
        else cellInWater = false;
    }

    public int getNewXPosOnStep(int eyeDirection, int xPos){//позиция по координате X в направлении взгляда
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

    public int getNewYPosOnStep(int eyeDirection, int yPos){//позиция по координате Y в направлении взгляда
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

class Cell extends AnyCell {//ячейка поля, которая может быть пустой или живой
    private float maxEnergy = 100;//максимум энергии
    private final float MAXENERGYDECPERSTEP = 1f;//уменьшение макс.енергии за шаг
    private float energy;//энергия клетки (1-100)
    private final int color;//цвет (0- пустая клетка)
    private float fightLevel=0;//уровень мастерства драки (0-100)
    private int eyeDirection=0;//напрвление взгляда (0 - вверх)
    private int nextAction;//запланированное действие
    private int attacked=0;//(0..10) флаг атаки. Опускается на 1 за каждый следующий шаг
    private boolean madeAction = false;

/*дальше всё что касается мозга*/
    private final int LAYERS_CNT_MIN = 2;//при инициализации, мин. кол-во скрытых слоев
    private final int LAYERS_CNT_MAX = 8;//при инициализации, макс. кол-во скрытых слоев
    private final int LAYERS_POW_MIN = 3;//при инициализации, мин. кол-во нейронов в скрытом слое
    private final int LAYERS_POW_MAX = 30;//при инициализации, макс. кол-во нейронов в скрытом слое

    private int hidenLayersCnt;//кол-во скрытых слоев
    private int hidenLayersPow;//кол-во нейронов в каждом слое
    private final int INPUT_SIGNAL_COUNT = 8;//кол-во входных нейронов
    /*
    1- уровень энергии
    2- уровень мастерства драки
    3- уровень атаки
    4- бот перед ним или нет
    5- если бот, то одного цвета или нет
    6- кол-во собратьев рядом
    7- напр взгляда по X
    8- напр взгляда по Y
    */
    private final int OUTPUT_SIGNAL_COUNT = 4;//кол-во выходных нейронов
    /*
    1- есть
    2- идти
    3- напасть
    4- делиться
    */

    /*дальше всё что касается деления*/
    public final float CHANCETOCHANGECOLOR = 0.001f;//шанс поменять цвет
    public final float CHANCETOMUTATE = 0.2f;//шанс мутировать при делении

    private NNetLayer [] nnet;

    public void live(){//функция жизни, вызывается раз за ход
        madeAction = false;
        thinc();
        reduceEnergy(myWorld.FOOD_LEVEL_PER_STEP);
        reduceAttacked();
        maxEnergy-=MAXENERGYDECPERSTEP;
    }

    public void makeAction(){
        if(madeAction)return;
        switch (nextAction){
            case 0: break;
            case 1:{
                eat();
                break;
            }
            case 2:{
                step();
                break;
            }
            case 3:{
                atack();
                break;
            }
            case 4:{
                split();
                break;
            }
            default: throw new ArrayStoreException();
        }
        madeAction = true;
    }

    private void setRndBrain(){//инициализация мозга
        hidenLayersCnt = (int) Math.round((Math.random() * (LAYERS_CNT_MAX-LAYERS_CNT_MIN)) + LAYERS_CNT_MIN);
        hidenLayersPow = (int) Math.round((Math.random() * (LAYERS_POW_MAX-LAYERS_POW_MIN)) + LAYERS_POW_MIN);

        nnet = new NNetLayer[hidenLayersCnt+1];
        nnet[0] = new NNetLayer(INPUT_SIGNAL_COUNT,hidenLayersPow);
        for(int i=1;i<hidenLayersCnt;i++){
            nnet[i] = new NNetLayer(hidenLayersPow,hidenLayersPow);
            nnet[i].setPrevLayer(nnet[i-1]);
        }
        nnet[hidenLayersCnt] = new NNetLayer(hidenLayersPow,OUTPUT_SIGNAL_COUNT,1);
        nnet[hidenLayersCnt].setPrevLayer(nnet[hidenLayersCnt-1]);
    }

    private void copyBrainAndMutate(Cell parentCell, Boolean mutate){
        nnet = new NNetLayer[parentCell.nnet.length];
        for(int i=0;i<parentCell.nnet.length;i++){
            nnet[i] = new NNetLayer(parentCell.nnet[i],mutate);
            if(i>0)nnet[i].setPrevLayer(nnet[i-1]);
        }
    }

    private int friendsNearCount(){//кол-во клеток одного цвета рядом
        int res=0;
        for(int i=0;i<8;i++) {//смотрим вокруг
            int newX = getNewXPosOnStep(i, xPos);
            int newY = getNewYPosOnStep(i, yPos);
            if ((newX>=0)&(newY>=0)&(newX<myWorld.WEIGHT)&(newY<myWorld.HEIGHT)){
                res += (this.color==myWorld.getCellByPos(newX,newY).getColor())?1:0;
            }
        }
        return res;
    }

    private void split(){//деление
        //ищем место, куда можно поделиться
        int splitX = -1;
        int splitY = -1;
        for(int i=0;i<8;i++) {//смотрим вокруг
            int newX = getNewXPosOnStep(i, xPos);
            int newY = getNewYPosOnStep(i, yPos);
            if (newX < 0 | newY < 0 | newX >= myWorld.WEIGHT | newY >= myWorld.HEIGHT) continue;

            if (myWorld.isEmptyCell(newX,newY)){
                splitX = newX;
                splitY = newY;
                break;
            }
        }
        if ((splitX >= 0)&(splitY >= 0)){//нашли
            boolean mutate = Math.random()<=CHANCETOMUTATE;
            myWorld.cellArray[splitY][splitX]=null;
            myWorld.cellArray[splitY][splitX] = new Cell(splitX,splitY,this,mutate);
            this.energy/=2;
        }
    }

    public void thinc(){//принять решение, что делаем
   /*   1- уровень энергии
        2- уровень мастерства драки
        3- уровень атаки
        4- бот перед ним или нет
        5- если бот, то одного цвета или нет
        6- кол-во собратьев рядом
        7- напр взгляда по X
        8- напр взгляда по Y*/

        float maxOutput = -100;
        int maxEyeDirection=0;
        int maxResult=0;

        for(int i=0;i<8;i++){//смотрим вокруг
            int newX = getNewXPosOnStep(i,xPos);
            int newY = getNewYPosOnStep(i,yPos);
            if(newX<0 | newY <0 | newX>=myWorld.WEIGHT | newY>=myWorld.HEIGHT) continue;
            float [] inparr = new float[8];
            inparr[0] = this.energy/100;
            inparr[1] = this.fightLevel/100;
            inparr[2] = this.attacked/10;
            inparr[3] = (myWorld.isEmptyCell(newX,newY))?0:1;
            if(inparr[3]==1){
                inparr[4]=(this.color==myWorld.getCellByPos(newX,newY).getColor())?1:-1;
            }else inparr[4]=0;
            inparr[5] = friendsNearCount();
            inparr[6] = newX - xPos;
            inparr[7] = newY - yPos;

            nnet[0].calc(inparr);
            for(int j=1;j<nnet.length;j++){
                nnet[j].calc();
            }

            for (int j=0;j<OUTPUT_SIGNAL_COUNT;j++){
                if (nnet[nnet.length-1].lastOutputs[j] > maxOutput){
                    maxOutput = nnet[nnet.length-1].lastOutputs[j];
                    maxResult = j;
                    maxEyeDirection = i;
                }
            }
        }
        eyeDirection = maxEyeDirection;
        nextAction = maxResult+1;
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

    private void atack(){//атакуем
        int opponentX = getNewXPosOnStep(eyeDirection,xPos);
        int opponentY = getNewYPosOnStep(eyeDirection,yPos);

        if ((opponentX >= 0)&(opponentX < myWorld.WEIGHT)&(opponentY >= 0)&(opponentY < myWorld.HEIGHT)){
            AnyCell opponentCell = myWorld.getCellByPos(opponentX,opponentY);
            if(! myWorld.isEmptyCell(opponentCell)){
                fight((Cell)opponentCell);
            }
        }
    }

    private void fight(Cell opponentCell){//определение победителя в схватке
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

    public Cell(int xPos, int yPos, Cell parentCell,boolean mutate) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.energy = parentCell.energy/2;
        float rnd = (float) Math.random();
        if (rnd <= CHANCETOCHANGECOLOR){
            this.color = (int) (Math.random()*9+1);
        }else this.color = parentCell.color;

        myWorld = parentCell.myWorld;
        myWorld.cellCnt +=1;
        copyBrainAndMutate(parentCell,mutate);
    }

    private void die(){//смерть
        myWorld.cellArray[yPos][xPos] = new EmptyCell(xPos,yPos);
        myWorld.cellCnt -=1;
    }

    private float getEnergy() {
        return energy;
    }

    private void addEnergy(float energy) {
        this.energy += energy;
        this.energy = (this.energy>maxEnergy)?maxEnergy:this.energy;
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

    @Override
    public Color getRealColor() {
        return switch (color){
            case 1: yield Color.green;
            case 2: yield Color.red;
            case 3: yield Color.CYAN;
            case 4: yield Color.magenta;
            case 5: yield Color.ORANGE;
            case 6: yield Color.GRAY;
            case 7: yield Color.YELLOW;
            case 8: yield Color.pink;
            case 9: yield Color.BLACK;
            case 10: yield Color.DARK_GRAY;
            default: yield Color.WHITE;
        };
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

class EmptyCell extends AnyCell{//пустая ячейка поля
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

    @Override
    public Color getRealColor() {
        return (cellInWater)?Color.BLUE:Color.WHITE;
    }
}