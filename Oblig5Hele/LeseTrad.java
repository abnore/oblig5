/**
 *
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Andreas Nore -andrebn@uio.no
 */
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * {@code LeseTrad} klassen er en {@code Runnable} som overskriver run metoden for {@code Thread} og er ansvarlig for innlesning av filer og lager {@code HashMaps}.
 * 
 */
public class LeseTrad implements Runnable {
    /** monitor objektet som tar seg av synkronisering */
    private Monitor2 monitor2;
    /** Mappenavnet gitt som parameter og brukt til å lese filer */
    private final String mappenavn;
    /** countdownlatch som holder teller på trådene som kjører */
    private final CountDownLatch countdown;
    /** Listen over filer for å danne en kø med executor*/
    private BlockingQueue<String> filnavnKoe;

    /**
     * Leser filnavn fra {@code filnavnKoe} og kaller {@code lesFil} for å opprette {@code HashMaps}<p>
     * 
     * <p>
     * Utfører lesingen, legger resultatet tilbake i registeret via {@code leggTilHashmap} og kaller {@code countDown}
     * <p>
     * 
     * 
     * @param monitor   Tar inn en monitor
     * @param countdown Tar inn en countdownlatch
     * 
     * @see SubsekvensRegister
     * @see Runnable
     * @see Thread
     * @see BlockingQueue
     * @see CountDownLatch
     */
    public LeseTrad(Monitor2 monitor2, String mappenavn, CountDownLatch countdown, BlockingQueue<String> filnavnKoe){
        this.monitor2 = monitor2;
        this.mappenavn = mappenavn;
        this.countdown = countdown;
        this.filnavnKoe = filnavnKoe;
    }

    public void run(){
        String filnavn;

        while ((filnavn = filnavnKoe.poll()) != null && !Thread.interrupted()) {
            monitor2.leggTilHashmap(SubsekvensRegister.lesFil(mappenavn+filnavn));
            countdown.countDown();
        }
    }
}

