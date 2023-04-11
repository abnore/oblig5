import java.util.concurrent.CountDownLatch;

public class LeseTrad implements Runnable {
    private Monitor2 monitor2;
    private final String fil;
    private final CountDownLatch countdown;

    public LeseTrad(Monitor2 monitor2, String fil, CountDownLatch countdown){
        this.monitor2 = monitor2;
        this.fil = fil;
        this.countdown = countdown;
    }

    public void run(){
            try {
                monitor2.leggTilHashmap(SubsekvensRegister.lesFil(this.fil + Thread.currentThread().getName()));
            } catch (Exception e) {
                System.err.println("Ingen monitor2 tilgjengelig");
            } finally {
                countdown.countDown();
            }
    }
}

