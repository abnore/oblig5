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
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Klassen {@code SubsekvensRegister} har en beholder, og metodene {@code leggTilHashmap}, {@code taUtHashmap}, {@code hvorMangeHashmap}, 
 * {@code lesFil} og {@code flettSammenTo}.
 * 
 */
public class SubsekvensRegister {
    /** Lokal beholder som holder HashMaps */
    private Deque<HashMap<String, Subsekvens>> hashBeholder = new ArrayDeque<>();
    
    /** Hvor mange bokstaver en sekvens skal være */
    private final static int LENGDE_SUBSEKVENS = 3;

    /**
     *  Legger til hashmaps på slutten av en lokalt lagret beholder.
     * 
     * @param hm    hashmapet som blir lagt til
     */
    public void leggTilHashmap(HashMap<String, Subsekvens> hm) {
        hashBeholder.add(hm);
    }

    /**
     * Tar ut og returnerner den første HashMapen i beholderen. Gir null hvis tom
     *   
     * @return HashMap<String, Subsekvens>
     */
    public HashMap<String, Subsekvens> taUtHashmap() {
        return hashBeholder.pollFirst();
    }
    /**
     * Gir et tall på hvor mange HashMaps det er i beholderen
     * 
     * @return int  antallet hashmaps
     */
    public int hvorMangeHashmap() {
        return hashBeholder.size();
    }

    /**
     * Statisk metode som tar inn et filnavn der det er sekvenser med bokstaver. Lager en ny streng og oppretter
     * en {@code Subsekvens} med antall 1, lagrer dette i en {@code HashMap} og returnerer den for lagring.
     * 
     * @param filnavn   tar inn en string som filnavn
     * @return  HashMap<>
     * 
     * @see Subsekvens
     */

     public static HashMap<String, Subsekvens> lesFil(String filnavn) {
     
        HashMap<String, Subsekvens> returnerendeHashMap = new HashMap<>();
        // Legge inn i en try catch pga File og BufferedReader trenger en Exception
        // handler, spesielt FileNotFoundException - men slår sammen til én IOException
        try (FileReader fil = new FileReader(filnavn);
                BufferedReader filLeser = new BufferedReader(fil)){
            
            String linje;

            while ((linje = filLeser.readLine()) != null) {
                if (linje.contains("amino_acid")) { // hopper over "amino_acid" linjen
                    continue;
                } else {
                    int lengde = linje.length();
                    int maksSubsekvenser = lengde-(LENGDE_SUBSEKVENS-1); // dette er om subsekvensene er 3 lange
                    
                    if(lengde >= LENGDE_SUBSEKVENS){  // om linjen er mindre enn 3 må vi bryte
                        for (int i = 0; i < maksSubsekvenser; i++) {
                            String resultat = linje.substring(i, i+3);
                            returnerendeHashMap.put(resultat, new Subsekvens(resultat, 1));
                        }
                    } else {
                        break; // stopper programmet
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Her har det skjedd en feil med filen!"); // Velger System.err av pedagogiske grunner
            e.printStackTrace(System.err);
        } 
        return returnerendeHashMap;
    }
    /**
     * Statisk metode som tar tar to HashMaps og slår den sammen. Om noen sekvenser er like summerer vi antallet deres.<p>
     * Returnerer en ny {@code HashMap} som er en kombinasjonen av de to med oppdaterte antall.
     *  
     * @param hm1    første hashmap
     * @param hm2    andre hashmap
     * @return res   HashMap<> som er flettet
     */
    public static HashMap<String, Subsekvens> flettSammenTo(HashMap<String, Subsekvens> hm1, HashMap<String, Subsekvens> hm2) {
        /** oppretterer pekere til resultatet og den minste av parameterene */
        HashMap<String, Subsekvens> res, hash2;

        /** initialiserer res som den største av de den tar inn */
        res = (hm1.size() >= hm2.size()) ? new HashMap<>(hm1) : new HashMap<>(hm2);
        hash2 = (hm1.size() >= hm2.size()) ? hm2 : hm1;

        for (String key : hash2.keySet()) {
            if (res.containsKey(key)) {
                Subsekvens value1 = hash2.get(key);
                Subsekvens value2 = res.get(key);
                value2.endreAntall(value1.hentAntall()); // Oppdaterer antallet om den finnes i begge
            } else {
                res.put(key, hash2.get(key)); // putter den inn om den ikke allerede finnes
            }
        }
        return res;
    }
}
