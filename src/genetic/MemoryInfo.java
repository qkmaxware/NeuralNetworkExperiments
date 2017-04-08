/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import plus.system.Timer;
import plus.system.TimerListener;

public class MemoryInfo extends JPanel{
    
    public Timer timer;
    
    private final int mb = 1024*1024;
    private final String unit = "mb";
    
    public MemoryInfo(int refreshRate){
        JProgressBar pb = new JProgressBar();
        pb.setMinimum(0); pb.setMaximum(100);
        Runtime run = Runtime.getRuntime();
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.add(pb);
        
        timer = new Timer(refreshRate);
        timer.AddListener(new TimerListener(){
            @Override
            public void OnTimerTick(double d) {
                long percent = (GetUsedMemory(run))/run.totalMemory();
                pb.setValue((int)(percent*100));
                String value = GetUsedMemory(run)/mb+"/"+run.totalMemory()/mb+unit;
                pb.setString(value);
            }
        });
        timer.start();
    } 
    
    public long GetUsedMemory(Runtime run){
        return run.totalMemory() - run.freeMemory();
    }
    
}
