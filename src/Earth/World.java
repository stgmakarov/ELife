package Earth;

import org.jetbrains.annotations.NotNull;

public class World {//описание мира
    private int worldTime = 0; //Время существования мира
    public final int HEIGHT; //Высота мира (координата y)
    public final int WEIGHT; //Ширина мира (координата x)
    public final int WATERHEIGHT; //Высота воды (координата y)
    private float foodLevel;
    AnyCell [][] cellArray;
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

    public World(int height, int weight, int waterheight, float foodLevel, int cellCnt){
        HEIGHT = height;
        WEIGHT = weight;
        WATERHEIGHT = waterheight;
        setFoodLevel(foodLevel);
        createCellArr(cellCnt);
    }

    public float getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(float foodLevel) {
        if(foodLevel>0 && foodLevel <= 100){
            this.foodLevel = foodLevel;};
    }
}
