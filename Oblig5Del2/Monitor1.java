// import java.util.concurrent.locks.Lock;
// import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

public class Monitor1 {
    // private Lock laas;
    // private Condition IKKE_NAVNGITT; // #######!!!!!!!!!!!!!!!!###########

    protected SubsekvensRegister subsekvensRegister;

    public Monitor1(SubsekvensRegister subsekvensRegister){
        this.subsekvensRegister = subsekvensRegister;
        // laas = new ReentrantLock();
        // IKKE_NAVNGITT = laas.newCondition(); // #######!!!!!!!!!!!!!!!!###########
    }

    public void leggTilHashmap(HashMap<String,Subsekvens> hm){
        subsekvensRegister.leggTilHashmap(hm);
        System.out.println("Legger til");

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
