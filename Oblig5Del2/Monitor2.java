import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;


public class Monitor2 {
    private Lock laas = new ReentrantLock(true); // en lås for å sikre kritiske regioner
    private Condition ikkeTomt = laas.newCondition(); // en tilstand for å holde styr på om stativet ikke er tomt
    SubsekvensRegister subsekvensRegister;
    
    @SuppressWarnings("unchecked")
    private HashMap<String, Subsekvens>[] mapPar = new HashMap[2]; // en enkel array med to stk av selvlagd hashmaps

    public Monitor2(SubsekvensRegister subsekvensRegister) {
        this.subsekvensRegister = subsekvensRegister;
        mapPar[0] = new HashMap<String, Subsekvens>();
        mapPar[1] = new HashMap<String, Subsekvens>();
    }

    public void leggTilHashmap(HashMap<String, Subsekvens> hm) {
        laas.lock();
        try {
            subsekvensRegister.leggTilHashmap(hm);
        } finally {
            laas.unlock();
        }
    }
    public void settInnFlettet(HashMap<String, Subsekvens> hm) {
        laas.lock();
        try {
            leggTilHashmap(hm);
            ikkeTomt.signalAll();
        } finally {
            laas.unlock();
        }
    }
    public HashMap<String, Subsekvens>[] hentUtTo() throws InterruptedException{
        laas.lock();
        try{
            while (hvorMangeHashmap() < 2) {
                ikkeTomt.await();
            }
            mapPar[0] = taUtHashmap();
            mapPar[1] = taUtHashmap();

            return mapPar;
        } finally {
            laas.unlock();
        }
    }
    public HashMap<String, Subsekvens> taUtHashmap() throws InterruptedException{
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

}

