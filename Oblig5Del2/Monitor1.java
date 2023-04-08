import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

public class Monitor1 {
    private Lock laas;
    // private Condition IKKE_NAVNGITT; // #######!!!!!!!!!!!!!!!!###########

    SubsekvensRegister subsekvensRegister;

    public Monitor1(){
        subsekvensRegister = new SubsekvensRegister();
        laas = new ReentrantLock();

    }
    
    public SubsekvensRegisterhentRegister() {
        return subsekvensRegister;
    }
    public void leggTilHashmap(HashMap<String,Subsekvens> hm){
        laas.lock();
        try {
            subsekvensRegister.leggTilHashmap(hm);
        } finally {
            laas.unlock();
        }
    }

    public HashMap<String,Subsekvens> taUtHashmap(){
        return subsekvensRegister.taUtHashmap();

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
