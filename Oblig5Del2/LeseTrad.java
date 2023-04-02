import java.util.concurrent.CountDownLatch;

public class LeseTrad implements Runnable {
    private Monitor1 monitor;
    private String fil;
    private CountDownLatch antallHashmaps;

    public LeseTrad(Monitor1 monitor, String fil, CountDownLatch antallHashmaps){
        this.monitor = monitor;
        this.fil = fil;
        this.antallHashmaps = antallHashmaps;
    }
    
    public void run(){
        while (antallHashmaps.getCount() > 0 && !Thread.interrupted()){
            antallHashmaps.countDown();

            monitor.leggTilHashmap(Monitor1.lesFil(fil));
            int tall = monitor.hvorMangeHashmap();
            System.out.println(tall+" hashmaps");
        }
    }

}
