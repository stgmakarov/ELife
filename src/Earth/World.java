package Earth;

import org.jetbrains.annotations.NotNull;

public class World {//мир
    private int worldTime = 0; //время
    public float energyPerEat;
    public final int HEIGHT; //высота мира (координата y)
    public final int WEIGHT; //ширина мира (координата x)
    public final int WATERHEIGHT; //высота моря (координата y)
    private float foodLevel; //кол-во еды (фотосинтез 0-100)
    AnyCell [][] cellArray; //все ячейки мира (занятые+свободные)
    int cellCnt=0;

    public boolean isEmptyCell(int xPos, int yPos){
        return cellArray[yPos][xPos].isEmptyCell();
    }

    public boolean isCellInWater(@NotNull Cell cell){
        return cell.yPos <= WATERHEIGHT;
    }

    public int getWorldTime() {
        return worldTime;
    }

    public void addWorldTime() {
        this.worldTime +=1;
    }

    public void checkConsistency(){
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                if((cellArray[i][j].yPos != i)|(cellArray[i][j].xPos != j)){
                    throw new ArrayStoreException();
                }
            }
        }
    }

    private void createCellArr(int cellCnt){
        float goodCellChance = (float) cellCnt/(HEIGHT*WEIGHT);
        cellArray = new AnyCell[HEIGHT][WEIGHT];
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WEIGHT;j++){
                boolean isGoodCell = Math.random() < goodCellChance;
                if (isGoodCell) {
                    cellArray[i][j] = new Cell(this,j,i,100,1);
                }else {
                    cellArray[i][j] = new EmptyCell(j,i);
                }
            }
        }
    }

    public void setEnergyPerEat(float energyPerEat){
        this.energyPerEat = energyPerEat;
    }

    public World(int height, int weight, int waterheight, float foodLevel, int initCellCnt, float energyPerEat){
        HEIGHT = height;
        WEIGHT = weight;
        WATERHEIGHT = waterheight;
        setEnergyPerEat(energyPerEat);
        setFoodLevel(foodLevel);
        createCellArr(initCellCnt);
    }

    public float getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(float foodLevel) {
        if(foodLevel>0 && foodLevel <= 100){
            this.foodLevel = foodLevel;};
    }
}
