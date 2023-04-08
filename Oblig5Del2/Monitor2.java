import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
// import java.util.concurrent.CountDownLatch;

public class Monitor2 {
    private static Lock laas;
    private static Condition ferdig;
    SubsekvensRegister subsekvensRegister;

    public Monitor2(Monitor1 monitor) {
        subsekvensRegister = monitor.hentRegister();
        laas = new ReentrantLock();
        ferdig = laas.newCondition();
    }
    public void leggTilHashmap(HashMap<String, Subsekvens> hm) {
        laas.lock();
        try {
            subsekvensRegister.leggTilHashmap(hm);
        } finally {
            ferdig.signalAll();
            laas.unlock();
        }
    }

    public HashMap<String, Subsekvens> taUtHashmap() throws InterruptedException{
        laas.lock();
        System.out.println("Skal til å ta ut..");
            try {
                if(subsekvensRegister.hvorMangeHashmap() >= 1){
                    return subsekvensRegister.taUtHashmap();
                    } ferdig.await();  
           } catch (Exception e) {
                e.printStackTrace();
            } finally {
                laas.unlock();
            }
            return subsekvensRegister.taUtHashmap(); 
    }

    public int hvorMangeHashmap() {
        laas.lock();
        try {
            return subsekvensRegister.hvorMangeHashmap();   
        } finally {
            laas.unlock();
        }
    }

    public static HashMap<String, Subsekvens> lesFil(String fil) {
        return SubsekvensRegister.lesFil(fil);
    }

    public static HashMap<String, Subsekvens> flettSammenTo(HashMap<String, Subsekvens> hm1, HashMap<String, Subsekvens> hm2) {
        laas.lock();
        System.out.println("akkurat låst for fletting");
        try {
            System.out.println("flettet 2");
            return SubsekvensRegister.flettSammenTo(hm1, hm2);
        } finally {
            laas.unlock();
        }
    }

}

