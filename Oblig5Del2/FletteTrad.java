import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;


public class FletteTrad implements Runnable{
    private Monitor2 monitor;
    private CountDownLatch countdown;
    private Lock laas;


    public FletteTrad(Monitor2 monitor, CountDownLatch countdown) {
        this.monitor = monitor;
        this.countdown = countdown;
        laas = new ReentrantLock();
    }
    public void run() {
        laas.lock();
        while(countdown.getCount()>0){
            try{
                HashMap<String, Subsekvens> hm1 = monitor.taUtHashmap();
                HashMap<String, Subsekvens> hm2 = monitor.taUtHashmap();
                Monitor2.flettSammenTo(hm1, hm2);
                countdown.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
        } laas.unlock();

        }
    }
    }
