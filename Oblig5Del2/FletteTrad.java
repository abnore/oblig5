import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class FletteTrad implements Runnable{
    private Monitor2 monitor;
    private CountDownLatch countdown;
    // private boolean paaJobb = true;

    public FletteTrad(Monitor2 monitor, CountDownLatch countdown) {
        this.monitor = monitor;
        this.countdown = countdown;
    }
    public void run() {
        try {
            while(!Thread.interrupted() && monitor.hvorMangeHashmap()>1){
                HashMap<String, Subsekvens> hm1 = monitor.taUtHashmap();
                HashMap<String, Subsekvens> hm2 = monitor.taUtHashmap();
                HashMap<String, Subsekvens> flettet = Monitor2.flettSammenTo(hm1, hm2);
                monitor.leggTilHashmap(flettet);
            }
            countdown.countDown();
        } catch (Exception e) {
          System.out.println("FAEN TA!!");
        }
        
    }
}
