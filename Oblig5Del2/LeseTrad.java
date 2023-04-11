import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class LeseTrad implements Runnable {
    private Monitor2 monitor2;
    private final String mappenavn;
    private final CountDownLatch countdown;
    private BlockingQueue<String> filnavnKo;

    public LeseTrad(Monitor2 monitor2, String mappenavn, CountDownLatch countdown, BlockingQueue<String> filnavnKo){
        this.monitor2 = monitor2;
        this.mappenavn = mappenavn;
        this.countdown = countdown;
        this.filnavnKo = filnavnKo;
    }

    public void run(){
        String filnavn;

        while ((filnavn = filnavnKo.poll()) != null && !Thread.interrupted()) {
            // Behandle filnavnet, for eksempel lese filen og utføre logikk
            monitor2.leggTilHashmap(SubsekvensRegister.lesFil(mappenavn+filnavn));
            countdown.countDown();
        }
            // try {
            //     monitor2.leggTilHashmap(SubsekvensRegister.lesFil(this.fil + Thread.currentThread().getName()));
            // } catch (Exception e) {
            //     System.err.println("Ingen monitor2 tilgjengelig");
            // } finally {
            //     countdown.countDown();
            // }
    }
}

