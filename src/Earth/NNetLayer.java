package Earth;

public class NNetLayer {//слой персептронов
    float [] lastOutputs;
    AbsNeyron [] nLayer;
    private NNetLayer prevLayer;
    private final int activationType;

    public void setPrevLayer(NNetLayer prevLayer) {
        this.prevLayer = prevLayer;
    }

    public NNetLayer(int inputCnt, int neyronCnt){//конструктор, заполняет значениями по рандому
        this.activationType = (int)(Math.random()*2);
        lastOutputs = new float[neyronCnt];
        nLayer = new AbsNeyron[neyronCnt];
        for(int i=0;i<neyronCnt;i++){
            switch (activationType){
                case 0:{
                    nLayer[i] = new Neyron1(inputCnt);
                    break;
                }
                default: {
                    nLayer[i] = new Neyron2(inputCnt);
                    break;
                }
            }
        }
    }

    public NNetLayer(NNetLayer copyFrom, Boolean mutate){
        this.activationType = copyFrom.activationType;
        int neyronCnt = copyFrom.nLayer.length;
        lastOutputs = new float[neyronCnt];
        nLayer = new AbsNeyron[neyronCnt];
        for(int i=0;i<neyronCnt;i++){
            int inputCnt = copyFrom.nLayer[i].weights.length;
            switch (activationType){
                case 0:{
                    nLayer[i] = new Neyron1(copyFrom.nLayer[i], mutate);
                    break;
                }
                default:{
                    nLayer[i] = new Neyron2(copyFrom.nLayer[i], mutate);
                    break;
                }

            }
        }
    }

    public NNetLayer(int inputCnt, int neyronCnt, int activationType){//конструктор определенного типа нейронов
        this.activationType = activationType;
        lastOutputs = new float[neyronCnt];
        nLayer = new AbsNeyron[neyronCnt];
        for(int i=0;i<neyronCnt;i++){
            switch (activationType){
                case 1:{
                    nLayer[i] = new Neyron1(inputCnt);
                    break;
                }
                case 2:{
                    nLayer[i] = new Neyron2(inputCnt);
                    break;
                }
            }
        }
    }

    public void calc(float [] input){ //расчёт выхода по входу
        for(int i=0;i<nLayer.length;i++){
            nLayer[i].activateFunc(input);
            lastOutputs[i] = nLayer[i].lastOutput;
        }
    }

    public void calc(){ //расчёт выхода по входу
        for(int i=0;i<nLayer.length;i++){
            nLayer[i].activateFunc(prevLayer.lastOutputs);
            lastOutputs[i] = nLayer[i].lastOutput;
        }
    }
}

abstract class AbsNeyron{ //нейрон (абстрактный класс без активации)
    public final float CHANCETOMUTATEFORSINAPSE = 0.01f;//шанс для отдельного синапса поменять значение при мутации
    public final float MAXPERCENTFORSINAPSE = 0.1f;//процент, на который синапс может поменять значение
    float [] weights;
    float lastOutput;
    public AbsNeyron(int inpCnt){
        weights = new float[inpCnt];
        for(int i=0;i<inpCnt;i++){
            weights[i] = (float) (Math.random()*0.02-0.01);
        }
    }

    public AbsNeyron(AbsNeyron copyFrom, Boolean mutate){
        int inpCnt = copyFrom.weights.length;
        weights = new float[inpCnt];
        for(int i=0;i<inpCnt;i++){
            weights[i] = copyFrom.weights[i];
            if (mutate){
                float rnd = (float) Math.random();
                if (rnd <= CHANCETOMUTATEFORSINAPSE){
                    float rnd2 = (float) (1 + Math.random()*MAXPERCENTFORSINAPSE*2 - MAXPERCENTFORSINAPSE);
                    weights[i] *= rnd2;
                }
            }
        }
    }

    public AbsNeyron(float [] weights){
        this.weights = new float[weights.length];
        for(int i=0; i<weights.length; i++){
            this.weights[i] = weights[i];
        }
    }

    public float calcRes(float [] input){
        float res=0;
        for(int i=0; i<weights.length;i++){
            res = weights[i]*input[i];
        }
        return res;
    }

    abstract float activateFunc(float [] input);
}

class Neyron1 extends AbsNeyron {

    public Neyron1(AbsNeyron copyFrom, Boolean mutate) {
        super(copyFrom, mutate);
    }

    public Neyron1(int inpCnt) {
        super(inpCnt);
    }

    public Neyron1(float[] weights) {
        super(weights);
    }

    float activateFunc(float[] input) {
        float res = calcRes(input);
        lastOutput = (res > 0.5) ? 1 : 0;
        return lastOutput;
    }
}

class Neyron2 extends AbsNeyron{
    public Neyron2(AbsNeyron copyFrom, Boolean mutate) {
        super(copyFrom, mutate);
    }
    public Neyron2(int inpCnt) {
        super(inpCnt);
    }

    public Neyron2(float[] weights) {
        super(weights);
    }

    float activateFunc(float [] input){
        float res=calcRes(input);
        lastOutput = ((res>0.45)&(res<=0.55))?1:0;
        return lastOutput;
    }
}