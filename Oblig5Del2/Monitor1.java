import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

public class Monitor1 {
    private Lock laas = new ReentrantLock(true); // en lås for å sikre kritiske regioner
    private Condition ikkeTomt = laas.newCondition(); // en tilstand for å holde styr på om stativet ikke er tomt
    SubsekvensRegister subsekvensRegister;

    public Monitor1(){
        subsekvensRegister = new SubsekvensRegister();
    }
    public SubsekvensRegister hentSubsekvensRegister(){
        return subsekvensRegister;
    }

    public void leggTilHashmap(HashMap<String,Subsekvens> hm){
        laas.lock();
        try {
            subsekvensRegister.leggTilHashmap(hm);
            ikkeTomt.signalAll();
        } finally {
            laas.unlock();
        }
    }

    public HashMap<String, Subsekvens> taUtHashmap() throws InterruptedException {
        laas.lock();
        try {
            while (subsekvensRegister.hvorMangeHashmap() == 0) {
                ikkeTomt.await();
            }
            return subsekvensRegister.taUtHashmap();
        } finally {
            laas.unlock();
        }
    }

     public int hvorMangeHashmap(){ 
        return subsekvensRegister.hvorMangeHashmap();
    }

     public static HashMap<String, Subsekvens> lesFil(String fil){
        return SubsekvensRegister.lesFil(fil);
    }

     public static HashMap<String,Subsekvens> flettSammenTo(HashMap<String,Subsekvens> hm1, HashMap<String,Subsekvens> hm2){
        return SubsekvensRegister.flettSammenTo(hm1, hm2); 
    }
        
}
