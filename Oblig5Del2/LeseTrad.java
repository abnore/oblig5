import java.util.concurrent.CountDownLatch;

public class LeseTrad implements Runnable {
    private Monitor1 monitor1;
    private Monitor2 monitor2;
    private String fil;
    private CountDownLatch countdown;

    public LeseTrad(Monitor2 monitor2, String fil, CountDownLatch countdown){
        this.monitor2 = monitor2;
        this.fil = fil;
        this.countdown = countdown;
    }
    
    public LeseTrad(Monitor1 monitor1, String fil, CountDownLatch countdown) {
        this.monitor1 = monitor1;
        this.fil = fil;
        this.countdown = countdown;
    }
    
    public void run(){
        if(monitor1!=null){
            try {
                monitor1.leggTilHashmap(SubsekvensRegister.lesFil(this.fil + Thread.currentThread().getName()));
                countdown.countDown();
                
            } catch (Exception e) {
                System.err.println("Ingen monitor1 tilgjengelig");
            }
        } else {
            try {
                monitor2.leggTilHashmap(SubsekvensRegister.lesFil(this.fil + Thread.currentThread().getName()));
                countdown.countDown();
                
            } catch (Exception e) {
                System.err.println("Ingen monitor2 tilgjengelig");
            }
        }
    }
}

