import Earth.AnyCell;
import Earth.World;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    public static final int SQ_SIZE = 5;
    private JPanel panelMain;
    private World world;
    public MainForm(int height, int weight){
        setContentPane(this.panelMain);
        setSize(weight,height);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    public void setWorld(World world){
        this.world = world;
    }

    public void paint(Graphics g){
        if(world==null)return;
        for(int i=0;i<world.HEIGHT;i++){
            for(int j=0;j<world.WEIGHT;j++){
                if(world.cellArray[i][j]==null){
                    continue;
                }
                int pX1 = j*SQ_SIZE+10;
                int pX2 = j*SQ_SIZE+11;
                int pY1 = (world.HEIGHT-i)*SQ_SIZE+100;
                int pY2 = (world.HEIGHT-i)*SQ_SIZE+101;
                g.setColor(world.cellArray[i][j].getRealColor());
                g.fillRect(pX2,pY2, SQ_SIZE -1, SQ_SIZE -1);
                g.setColor(Color.BLACK);
                g.drawRect(pX1,pY1, SQ_SIZE, SQ_SIZE);

 /*               int pX1 = j*SQ_SIZE+10;
                int pY1 = (world.HEIGHT-i)*SQ_SIZE+100;
                g.setColor(world.cellArray[i][j].getRealColor());
                g.fillRect(pX1,pY1, SQ_SIZE, SQ_SIZE);*/
            }
        }
    }
}

