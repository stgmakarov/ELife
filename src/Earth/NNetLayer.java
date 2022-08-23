package Earth;

public class NNetLayer {
    float [] lastOutputs;
    AbsNeyron [] nLayer;
    private NNetLayer prevLayer;

    public void setPrevLayer(NNetLayer prevLayer) {
        this.prevLayer = prevLayer;
    }

    public NNetLayer(int inputCnt, int neyronCnt){
        int activationType = (int)Math.random()*2;
        lastOutputs = new float[neyronCnt];
        nLayer = new AbsNeyron[neyronCnt];
        for(int i=0;i<neyronCnt;i++){
            switch (activationType){
                case 1:
                    nLayer[i] = new Neyron1(inputCnt);
                case 2:
                    nLayer[i] = new Neyron2(inputCnt);
            }
        }
    }

    public NNetLayer(int inputCnt, int neyronCnt, int activationType){
        lastOutputs = new float[neyronCnt];
        nLayer = new AbsNeyron[neyronCnt];
        for(int i=0;i<neyronCnt;i++){
            switch (activationType){
                case 1:
                    nLayer[i] = new Neyron1(inputCnt);
                case 2:
                    nLayer[i] = new Neyron2(inputCnt);
            }
        }
    }

    public void calc(float [] input){
        for(int i=0;i<nLayer.length;i++){
            nLayer[i].activateFunc(input);
            lastOutputs[i] = nLayer[i].lastOutput;
        }
    }

    public void calc(){
        for(int i=0;i<nLayer.length;i++){
            nLayer[i].activateFunc(prevLayer.lastOutputs);
            lastOutputs[i] = nLayer[i].lastOutput;
        }
    }
}

abstract class AbsNeyron{
    float [] weights;
    float lastOutput;
    public AbsNeyron(int inpCnt){
        weights = new float[inpCnt];
        for(int i=0;i<inpCnt;i++){
            weights[i] = (float) (Math.random()*0.02-0.01);
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