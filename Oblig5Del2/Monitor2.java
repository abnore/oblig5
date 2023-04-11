import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;


public class Monitor2 {
    private ReadWriteLock laas = new ReentrantReadWriteLock(true);
    private Lock readLock = laas.readLock();
    private Lock writeLock = laas.writeLock();
    private Condition ikkeTomt = writeLock.newCondition();
    SubsekvensRegister subsekvensRegister;
    
    @SuppressWarnings("unchecked")
    private HashMap<String, Subsekvens>[] mapPar = new HashMap[2]; // en enkel array med to stk av selvlagd hashmaps

    public Monitor2(SubsekvensRegister subsekvensRegister) {
        this.subsekvensRegister = subsekvensRegister;
        mapPar[0] = new HashMap<>();
        mapPar[1] = new HashMap<>();
    }

    public void leggTilHashmap(HashMap<String, Subsekvens> hm) {
        writeLock.lock();
        try {
            subsekvensRegister.leggTilHashmap(hm);
            ikkeTomt.signalAll();
        } finally {
            writeLock.unlock();
        }
    }
    public void settInnFlettet(HashMap<String, Subsekvens> hm) {
        writeLock.lock();
        try {
            leggTilHashmap(hm);
            ikkeTomt.signalAll();
        } finally {
            writeLock.unlock();
        }
    }
    public HashMap<String, Subsekvens>[] hentUtTo() throws InterruptedException{
         writeLock.lock();
        try{
           if (hvorMangeHashmap() < 2) {
                return null;
            }

            mapPar[0] = taUtHashmap();
            mapPar[1] = taUtHashmap();

            return mapPar;
        } finally {
            writeLock.unlock();
        }
    }
    public HashMap<String, Subsekvens> taUtHashmap() throws InterruptedException{
            return subsekvensRegister.taUtHashmap();
    }

    public int hvorMangeHashmap() {
        readLock.lock();
        try {
            return subsekvensRegister.hvorMangeHashmap();   
        } finally {
            readLock.unlock();
        }
    }

}

