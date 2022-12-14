import Earth.World;

public class ELife {
    public static final int WORLDHEIGHT = 100;
    public static final int WATERLEVEL = 20;
    public static final int WORLDWEIGHT = 200;
    public static MainForm mainForm;
    static World myWorld;
    public static void main(String[] args) throws Exception {
        //myWorld = new World(WORLDHEIGHT,WORLDWEIGHT,WATERLEVEL,80000,500, 5);
        myWorld = new World(WORLDHEIGHT,WORLDWEIGHT,WATERLEVEL,10,500, 8.1f);
        mainForm = new MainForm(WORLDHEIGHT*MainForm.SQ_SIZE+116, WORLDWEIGHT*MainForm.SQ_SIZE+20);
        mainForm.setWorld(myWorld);
        while (true){
            myWorld.printInfo();
            myWorld.live();
            mainForm.repaint();
            //Thread.sleep(50);
        }
    }
}
