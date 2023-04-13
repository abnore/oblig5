/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Andreas Nore - andrebn@uio.no
 */
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * Klassen {@code Monitor2} tar hånd om synkronisering/parallellisering ifm
 * både innlesing av filer, opprettelse av {@code HashMaps} og fletting.
 * <p> Anvender seg av {@code ReadWriteLock} for å synkronisere lesing og fletting
 * 
 * @see FletteTrad
 * @see LeseTrad
 * @see ReadWriteLock
 */
public class Monitor2 {
    /** fair read/write lock som setter trådene i kø */
    private ReadWriteLock laas;
    /** readlock slik at flere tråder kan lese av informasjon samtidig */
    private Lock readLock;
    /** writelock for kritiske regioner, slik at endring kan kun gjøres av én tråd */
    private Lock writeLock;
    /** beholder for hashmaps initieres her */
    SubsekvensRegister subsekvensRegister;
    /** lokalt brukt for fletting av to HasMaps samtidig */
    ArrayDeque<HashMap<String, Subsekvens>> mapPar;

    /**
     * Konstruktøren initierer beholdere og er ansvarlig for synkronisering mellom tråder
     * 
     */
    public Monitor2() {
        subsekvensRegister = new SubsekvensRegister();
        mapPar = new ArrayDeque<>();
        laas = new ReentrantReadWriteLock(true);
        readLock = laas.readLock();
        writeLock = laas.writeLock();
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
     * Henter ut og returnerer en beholder på to {@code HashMaps}. Om
     * det er 0 eller 1 {@code HashMaps} returnerer den null som interrupter
     * potensielle tråder som står i kø. Dette gjør at tråden som tar ut de to gjenværende
     * legger inn den siste, resten stopper.
     * 
     * @return ArrayDeque<>  med to stk {@code HashMaps}
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
     * Kalles kun av {@code hentUtTo} og trenger derfor ingen lock eller conditions da kun en tråd vil kalle den om gangen. <p>Returnerer {@code null} om registeret er tomt.
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

