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
            while(!Thread.interrupted()){
                mapPar = monitor.hentUtTo();
                if(mapPar == null){
                    break;
                }
                HashMap<String, Subsekvens> flettet = SubsekvensRegister.flettSammenTo(mapPar[0], mapPar[1]);
                monitor.settInnFlettet(flettet);
            }
        } catch (Exception e) {
          System.out.println("Tråd "+Thread.currentThread().getName()+" er ferdig");
        } finally {
        countdown.countDown();
        }
        
    }
}
