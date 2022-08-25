package Earth;

import org.jetbrains.annotations.NotNull;

public class World {//мир
    private int worldTime = 0; //время
    public final int PREDATOR_FIGHT_LEV = 5;
    public static final float UNDERWATERFOOD = 0.85f;
    public final int HEIGHT; //высота мира (координата y)
    public final int WEIGHT; //ширина мира (координата x)
    public final int WATERHEIGHT; //высота моря (координата y)
    public final int MINCELLCOUNT = 100; //минимальное кол-во жизни. Если становится меньше, добавляем CNTTOADD клеток
    public final int CNTTOADD = 500; // кол-во клеток для добавления (см MINCELLCOUNT)
    private float foodLevel;
    public final float FOOD_LEVEL_PER_STEP; //кол-во еды, которое каждыя клетка тратит за ход
    public AnyCell [][] cellArray; //все ячейки мира (занятые+свободные)
    int cellCnt=0;

    public AnyCell getCellByPos(int x, int y){ //ссылка на ячейку по позиции
        return cellArray[y][x];
    }

    public void printInfo(){ //инфо
        if(worldTime%10!=0)return;
        System.out.println("Шаг " + worldTime);
        System.out.println("Кол-во живих клеток " + cellCnt);
        int atackLev = 0;
        for(int i=0;i<HEIGHT;i++) {
            for (int j = 0; j < WEIGHT; j++) {
                if (!cellArray[i][j].isEmptyCell()) {
                    atackLev += ((Cell) (cellArray[i][j])).getAttacked();
                }
            }
        }
        System.out.println("Уровень войны " + atackLev);
        System.out.println("================================");
    }

    public static int getRndColor(){
        return (int) (Math.random()*8+2);
    }

    public void live() throws Exception { //один шаг жизни мира
        worldTime += 1;
/*        if (worldTime%500==0) {
            setFoodLevel(foodLevel*0.999f);
        }*/
        checkConsistency();
/*        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                if (!isEmptyCell(j,i)){
                    ((Cell)cellArray[i][j]).live();
                }
            }
        }*/
        //ускоряем
        WorldCalcalator worldCalcalator = new WorldCalcalator(this);

        checkConsistency2();

        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                if (!isEmptyCell(j,i)){
                    ((Cell)cellArray[i][j]).makeAction();
                }
            }
        }
        if (this.cellCnt < MINCELLCOUNT){
            addCell(CNTTOADD);
        }
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                cellArray[i][j].setCellInWater(WATERHEIGHT);
            }
        }

    }

    public boolean isEmptyCell(int xPos, int yPos){ //ячейка с живой клеткой или пустой
        return cellArray[yPos][xPos].isEmptyCell();
    }
    public boolean isEmptyCell(AnyCell cell){ //ячейка с живой клеткой или пустой
        return cell.isEmptyCell();
    }

    public boolean isCellInWater(@NotNull Cell cell){ //ячейка в воде или на суше
        return cell.yPos <= WATERHEIGHT;
    }

    public int getWorldTime() {
        return worldTime;
    }

    public void addWorldTime() {
        this.worldTime +=1;
    }

    public void checkConsistency(){ //проверка консистентности мира (все клетки на своих местах)
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                if((cellArray[i][j].yPos != i)|(cellArray[i][j].xPos != j)){
                    throw new ArrayStoreException();
                }
            }
        }
    }

    public void checkConsistency2(){ //проверка консистентности мира (все клетки на своих местах)
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                if(!cellArray[i][j].isEmptyCell()){
                    if( !((Cell)cellArray[i][j]).calculated ) {
                        //throw new ArrayStoreException("Клетка "+i+" "+j);
                        System.out.println("Не посчитана клетка "+i+" "+j);
                    }
                }
            }
        }
    }

    private void createCellArr(int cellCnt){ //инициализация поля
        float goodCellChance = (float) cellCnt/(HEIGHT*WEIGHT);
        cellArray = new AnyCell[HEIGHT][WEIGHT];
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                boolean isGoodCell = Math.random() < goodCellChance;
                if (isGoodCell) {
                    cellArray[i][j] = new Cell(this,j,i,100,getRndColor());
                }else {
                    cellArray[i][j] = new EmptyCell(j,i);
                }
            }
        }
    }

    public void addCell(int cellCnt){ //добавление рандомных клеток
        float goodCellChance = (float) cellCnt/(HEIGHT*WEIGHT - this.cellCnt);
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                if (cellArray[i][j].isEmptyCell()){
                    boolean isGoodCell = Math.random() < goodCellChance;
                    if (isGoodCell) {
                        cellArray[i][j] = null;
                        cellArray[i][j] = new Cell(this,j,i,100,(int)(Math.random()*2+2));
                    }
                }
            }
        }
    }

    //конструктор мира
    public World(int height, int weight, int waterheight, float foodLevel, int initCellCnt, float foodLevelPerStep){
        HEIGHT = height;
        WEIGHT = weight;
        WATERHEIGHT = waterheight;
        this.FOOD_LEVEL_PER_STEP = foodLevelPerStep;
        setFoodLevel(foodLevel);
        createCellArr(initCellCnt);
    }

    public float getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(float foodLevel) {
            this.foodLevel = foodLevel;
    }
}
