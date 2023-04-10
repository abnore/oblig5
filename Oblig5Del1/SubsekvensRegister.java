/*  Oppgave 2
 *      ↳ Enkel beholder, velger ArrayList som tar vare på alle HashMaps
 *      ↳ Ta ut vilkårlige HashMaps
 *      ↳ spørre hvor mange HashMaps den inneholder
 * Oppgave 3
 *      ↳ Statisk metode som leser én fil med én persons rep, og lager én hashmap
 * Oppgave 4
 *   (ide- hva om sammenfletting ikke fjerner, men tar ut én, legger den inn i en annen, if.equalsToIgnoreCase
 *    så endrer jeg antall, og så remover jeg. så indeks 1 går inn i 0 while>1)
 *      ↳ Statisk metode som slår sammen to hashmaps, med parametere som er
 *         referanser til de to som skal slås sammen, mens returverdien er
 *         referanse til den sammenslåtte hashmapen
 *      ↳ Er subsekvensene like skal antallet oppdateres ved å summere
 */
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;

/*
Deque<Integer> arrayQueue = new ArrayDeque<>();
Deque<Integer> linkedListQueue = new LinkedList<>();

// these operations are useful and same on both.
arrayQueue.push()/addFirst()/addLast();

arrayQueue.remove()/removeFirst()/removeLast()/pop();

arrayQueue.peek()/peekFirst()/peekLast()/size()/
 */

public class SubsekvensRegister {

    private Deque<HashMap<String, Subsekvens>> hashBeholder = new ArrayDeque<>();
    final static int lengdeSubsekvens = 3; // konstant lengde på sekvenser

    public void leggTilHashmap(HashMap<String,Subsekvens> hm){
        hashBeholder.add(hm);
    }

    public HashMap<String, Subsekvens> taUtHashmap(){ // returnerer og fjerner den valgte hashmap'en
        return hashBeholder.removeFirst();
    }
    
    public int hvorMangeHashmap(){
        return hashBeholder.size();
    }

    public static HashMap<String, Subsekvens> lesFil(String filnavn) {
        /* Leser inn en fil og returnerer en HashMap som senere
         * skal legges inn i beholderen / arraylisten
         */
        BufferedReader filLeser = null;
        
        HashMap<String,Subsekvens> returnerendeHashMap = new HashMap<>();
        // Legge inn i en try catch pga File og BufferedReader trenger en Exception handler, spesielt FileNotFoundException - men slår sammen til én IOException
        try {
            FileReader fil = new FileReader(filnavn);
            filLeser = new BufferedReader(fil);
            String linje;

            while ((linje = filLeser.readLine()) != null){
                if (linje.equals("amino_acid")) { // hopper over "amino_acid" linjen
                    continue;
                } else {
                    // char[] charFraLinje = linje.toCharArray();  // istedet for split(), får en array av hver bokstav
                    // int lengde = charFraLinje.length;
                    int lengde = linje.length();
                    int maksSubsekvenser = lengde-(lengdeSubsekvens-1); // dette er om subsekvensene er 3 lange
                    if(lengde >= lengdeSubsekvens){ // om linjen er mindre enn 3 må vi bryte
                        for (int i = 0; i < maksSubsekvenser; i++) {
                            // StringBuilder sekvens = new StringBuilder(); // Siden Strings er immutable så blir det 3x færre strings ved å bygge én og konvertere
                            // for (int j = 0; j < lengdeSubsekvens; j++){
                            //     // sekvens.append(charFraLinje[i+j]); // så bokstav ved indeks i (+0, +1 og +2) fra der vi er (tilsvarer tre bokstaver f.o.m der vi er)
                            // }
                            String resultat = linje.substring(i, i+3);
                            // String resultat = sekvens.toString();
                            Subsekvens sub = new Subsekvens(resultat, 1);
                            returnerendeHashMap.put(resultat, sub);
                        }
                    } else {
                        break; // stopper programmet
                    } 
                }
            }
        } catch (IOException e) {
            System.err.println("Her har det skjedd en feil med filen!"); // Velger System.err av pedagogiske grunner
            e.printStackTrace(System.err);
        } finally {
            try {
                if(filLeser != null){
                    filLeser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnerendeHashMap;
    } 

    public static HashMap<String, Subsekvens> flettSammenTo(HashMap<String, Subsekvens> hm1, HashMap<String, Subsekvens> hm2){
        HashMap<String, Subsekvens> res, minste;

        if (hm1.size() >= hm2.size()) { // initialiserer resMapen som den største av de den tar inn
            res = new HashMap<>(hm1);
            minste = hm2;
        } else {
            res = new HashMap<>(hm2);
            minste = hm1;
        }
        for (String key : minste.keySet()) {
            if (res.containsKey(key)) {
                Subsekvens value1 = res.get(key); // verdien på den som er puttet inn
                Subsekvens value2 = minste.get(key); // henter ut Subsekvensene fra den jeg sammenligner med og den nye
                value1.endreAntall(value2.hentAntall()); // endrer tallet på den som er i den nye
            } else {
                res.put(key, minste.get(key)); // om ikke, bare putter den inn
            }
         }
        return res;
    }     
}

