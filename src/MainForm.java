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
        if(world==null)return;
        for(int i=0;i<world.HEIGHT;i++){
            for(int j=0;j<world.WEIGHT;j++){
                if(world.cellArray[i][j]==null){
                    continue;
                }
                g.setColor(Color.BLACK);
                g.drawRect(j*5,(world.HEIGHT-i)*5,5,5);
                g.setColor(world.cellArray[i][j].getRealColor());
                g.fillRect(j*5+1,(world.HEIGHT-i)*5+1,4,4);
            }
        }
    }
}

