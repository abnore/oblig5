import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class FletteTrad implements Runnable{
    private Monitor2 monitor;
    private CountDownLatch countdown;
    @SuppressWarnings("unchecked")
    private HashMap<String, Subsekvens>[] mapPar = new HashMap[2];

    public FletteTrad(Monitor2 monitor, CountDownLatch countdown) {
        this.monitor = monitor;
        this.countdown = countdown;
    }
    public void run() {
        try {
            while(!Thread.interrupted() && monitor.hvorMangeHashmap()>1){
                mapPar = monitor.hentUtTo();
                HashMap<String, Subsekvens> flettet = SubsekvensRegister.flettSammenTo(mapPar[0], mapPar[1]);
                monitor.settInnFlettet(flettet);
            }
        } catch (Exception e) {
          System.out.println("FAEN TA!!");
          e.printStackTrace();
        } finally {
        countdown.countDown();
        }
        
    }
}
