import Earth.World;

public class ELife {
    public static final int WORLDHEIGHT = 250;
    public static final int WATERLEVEL = 25;
    public static final int WORLDWEIGHT = 500;
    public static MainForm mainForm;
    static World myWorld;
    public static void main(String[] args) throws InterruptedException {
        myWorld = new World(WORLDHEIGHT,WORLDWEIGHT,WATERLEVEL,10,5000, 8);
        mainForm = new MainForm(WORLDHEIGHT, WORLDWEIGHT);
        mainForm.setWorld(myWorld);
        while (true){
            myWorld.printInfo();
            myWorld.live();
            mainForm.repaint();
            //Thread.sleep(50);
        }
    }
}
