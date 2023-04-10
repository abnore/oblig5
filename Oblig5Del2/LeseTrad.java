import java.util.concurrent.CountDownLatch;
import java.util.HashMap;

public class LeseTrad implements Runnable {
    private Monitor2 monitor;
    private String fil;
    private CountDownLatch countdown;

    public LeseTrad(Monitor2 monitor, String fil, CountDownLatch countdown){
        this.monitor = monitor;
        this.fil = fil;
        this.countdown = countdown;
    }
    
    public void run(){
            HashMap<String,Subsekvens> hm = Monitor2.lesFil(fil);
            monitor.leggTilHashmap(hm);
            countdown.countDown();
        }
    }

