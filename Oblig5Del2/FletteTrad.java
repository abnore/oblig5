/**
 * Runnable klasse som overskriver run metoden for Threads. Tar ut to HashMaps fra SubsekvensRegisteret lagret i en monitor
 * Utfører fletting gjennom legger resultatet tilbake i registeret
 *  
 * @author Andreas Nore -andrebn@uio.no
 */
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


public class FletteTrad implements Runnable{
    /** monitor objektet som tar seg av synkronisering */
    private Monitor2 monitor;
    /** countdownlatch som holder teller på trådene som kjører */
    private CountDownLatch countdown;
    /** lokal beholder slik at jeg kan hente to HashMaps om gangen */
    ArrayDeque<HashMap<String, Subsekvens>> mapPar;

    /**
     * Konstruktøren 
     * 
     * @param monitor   Tar inn en monitor
     * @param countdown Tar inn en countdownlatch
     */
    public FletteTrad(Monitor2 monitor, CountDownLatch countdown) {
        this.monitor = monitor;
        this.countdown = countdown;
    }
    
    /**
     * run metoden fra Runnable interfacet. Så lenge den ikke blir interrupted henter den to hashmaps, fletter og setter inn i beholderen
     * avslutter med å dekrementere countdown
     */
    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
                mapPar = monitor.hentUtTo();
                if(mapPar == null){
                    Thread.currentThread().interrupt();
                }
                HashMap<String, Subsekvens> flettet = SubsekvensRegister.flettSammenTo(mapPar.poll(), mapPar.poll());
                monitor.settInnFlettet(flettet);
            }
        } catch (Exception e) {
             
        } finally {
        countdown.countDown();
        }
        
    }
}
