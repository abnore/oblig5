/**
 * Monitor objektet som tar hånd om synkronisering/parallellisering
 * 
 * @author Andreas Nore - andrebn@uio.no
 */
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayDeque;
import java.util.HashMap;

public class Monitor2 {
    /** fair read/write lock som setter trådene i kø */
    private ReadWriteLock laas = new ReentrantReadWriteLock(true);
    /** readlock slik at flere tråder kan lese av informasjon samtidig */
    private Lock readLock = laas.readLock();
    /** writelock for kritiske regioner, slik at endring kan kun gjøres av én tråd */
    private Lock writeLock = laas.writeLock();
    /** beholder for hashmaps initieres her */
    SubsekvensRegister subsekvensRegister;
    /** lokalt brukt for fletting av to HasMaps samtidig */
    ArrayDeque<HashMap<String, Subsekvens>> mapPar;

    /**
     * Konstruktøren initierer beholdere og er ansvarlig for synkronisering mellom tråder
     */
    public Monitor2() {
        this.subsekvensRegister = new SubsekvensRegister();
        mapPar = new ArrayDeque<>();
    }

    /**
     * Legger til hashmaps synkronisert gjennom writelocks. Ingen conditions da vi ikke tar ut og legger inn samtidig
     * 
     * @param hm    HashMap<String, Subsekvens> som legges inn
     */
    public void leggTilHashmap(HashMap<String, Subsekvens> hm) {
        writeLock.lock();
        try {
            subsekvensRegister.leggTilHashmap(hm);

        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Legger inn etter fletting. Ingen conditions siden vi starter med mange og jobber oss ned til én. Har logikk som sørger for at trådene
     * avsluttes
     * 
     * @param hm    HashMap<String, Subsekvens> som er flettet
     */
    public void settInnFlettet(HashMap<String, Subsekvens> hm) {
        writeLock.lock();
        try {
            leggTilHashmap(hm);

        } finally {
            writeLock.unlock();
        }
    }
    /**
     * Henter ut to HashMaps samtidig og returnerer en beholder på to HashMaps. Om det er 0 eller 1 HashMaps returnerer den null som interrupter
     * potensielle tråder som står i kø. Dette gjør at tråden som tar ut de to siste legger inn den siste, resten stopper.
     * 
     * @return  Deque<> med to stk HashMaps
     */
    public ArrayDeque<HashMap<String, Subsekvens>> hentUtTo(){
         writeLock.lock();
        try{
           if (hvorMangeHashmap() < 2) {
                return null;
            }

            mapPar.add(taUtHashmap());
            mapPar.add(taUtHashmap());

            return mapPar;
        } finally {
            writeLock.unlock();
        }
    }
    /**
     * Kalles kun av hentUtTo og trenger derfor ingen lock eller conditions da kun en tråd vil kalle den om gangen
     * 
     * @return HashMap<String, Subsekvens>
     */
    public HashMap<String, Subsekvens> taUtHashmap(){
            return subsekvensRegister.taUtHashmap();
    }
    /**
     * Gir et antall på hvor mange HashMaps det er i beholderen
     * 
     * @return HashMaps<String, Subsekvens>
     */
    public int hvorMangeHashmap() {
        readLock.lock();
        try {
            return subsekvensRegister.hvorMangeHashmap();   
        } finally {
            readLock.unlock();
        }
    }

}

