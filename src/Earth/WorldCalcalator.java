package Earth;

import java.util.concurrent.*;

public class WorldCalcalator {
    private World world;
    public WorldCalcalator(World world) throws InterruptedException, ExecutionException {
        if(world==null) return;
        this.world = world;
        Calc task = new Calc(world.cellArray, 0, world.cellArray.length);
        // Создать общий пул, это функция, предоставляемая jdk1.8
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Future<Integer> future = pool.submit(task); // Отправить разложенную задачу SumTask
        future.get();
        pool.shutdown (); // Завершение работы пула потоков
    }
}

class Calc extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 10; // Каждая маленькая задача может накапливать максимум 20
    private AnyCell[][] arry;
    private int start;
    private int end;

    public Calc(AnyCell[][] cellArray, int start, int end) {
        super();
        this.arry = cellArray;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        // Когда разница между концом и началом меньше порога, начинается фактическое накопление
        if(end - start <THRESHOLD){
            for(int i=start;i<end;i++){
                for(int j=0;j<arry[i].length;j++){
                    if (!arry[i][j].isEmptyCell()){
                        ((Cell)arry[i][j]).live();
                        sum += 1;
                    }
                }
            }
            return sum;
        } else {// Когда разница между концом и началом больше порогового значения, то есть когда количество, которое нужно накапливать, превышает 20, разбить большую задачу на маленькие задачи
            int middle = (start+ end)/2;
            Calc left = new Calc(arry, start, middle);
            Calc right = new Calc(arry, middle, end);
            // Выполняем две небольшие задачи параллельно
            left.fork();
            right.fork();
            // Объединяем накопленные результаты двух небольших задач
            return left.join()+right.join();
        }
    }
}

/*
class Calc extends RecursiveAction{
    private static final int THRESHOLD = 50;
    private int start;
    private int end;
    private World world;

    public Calc(int start, int end, World world){
        super();
        this.start = start;
        this.end = end;
        this.world = world;
    }

    @Override
    protected void compute() {
        if((end - start) < THRESHOLD){
            for(int i=start;i<end;i++){
                for(int j=0;j<world.WEIGHT;j++){
                    if (!world.isEmptyCell(j,i)){
                        ((Cell)world.cellArray[i][j]).live();
                    }
                }
            }
        }else {
            int middle =(start+end)/2;
            Calc left = new Calc(start, middle+1, world);
            Calc right = new Calc(middle-1, end, world);
            // Выполняем две "маленькие задачи" параллельно
            left.fork();
            right.fork();
        }
    }
}*/
