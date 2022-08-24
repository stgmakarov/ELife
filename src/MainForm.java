import Earth.AnyCell;
import Earth.World;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private World world;
    public MainForm(int height, int weight){
        setContentPane(this.panelMain);
        setSize(weight,height);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setWorld(World world){
        this.world = world;
    }

    public void paint(Graphics g){
        for(int i=0;i<world.HEIGHT;i++){
            for(int j=0;j<world.WEIGHT;j++){
                if(world.cellArray[i][j]==null){
                    continue;
                }
                g.setColor(world.cellArray[i][j].getRealColor());
                g.fillRect(j,world.HEIGHT-i,1,1);
            }
        }
    }
}

