
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;

public class SubsekvensRegister {

    private Deque<HashMap<String, Subsekvens>> hashBeholder = new ArrayDeque<>();
    final static int lengdeSubsekvens = 3; // konstant lengde på sekvenser

    public void leggTilHashmap(HashMap<String, Subsekvens> hm) {
        hashBeholder.add(hm);
    }

    public HashMap<String, Subsekvens> taUtHashmap() { // returnerer og fjerner den valgte hashmap'en
        // return hashBeholder.peekFirst();
        return hashBeholder.removeFirst();
    }

    public int hvorMangeHashmap() {
        return hashBeholder.size();
    }

    public static HashMap<String, Subsekvens> lesFil(String filnavn) {
        /*
         * Leser inn en fil og returnerer en HashMap som senere
         * skal legges inn i beholderen / arraylisten
         */
        BufferedReader filLeser = null;

        HashMap<String, Subsekvens> returnerendeHashMap = new HashMap<>();
        // Legge inn i en try catch pga File og BufferedReader trenger en Exception
        // handler, spesielt FileNotFoundException - men slår sammen til én IOException
        try {
            FileReader fil = new FileReader(filnavn);
            filLeser = new BufferedReader(fil);
            String linje;

            while ((linje = filLeser.readLine()) != null) {
                if (linje.contains("amino_acid")) { // hopper over "amino_acid" linjen
                    continue;
                } else {
                    int lengde = linje.length();
                    int maksSubsekvenser = lengde-(lengdeSubsekvens-1); // dette er om subsekvensene er 3 lange
                    
                    if(lengde >= lengdeSubsekvens){  // om linjen er mindre enn 3 må vi bryte
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
        } finally {
            try {
                if (filLeser != null) {
                    filLeser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnerendeHashMap;
    }

    public static HashMap<String, Subsekvens> flettSammenTo(HashMap<String, Subsekvens> hm1,
            HashMap<String, Subsekvens> hm2) {
        HashMap<String, Subsekvens> res, hash2;

        if (hm1.size() >= hm2.size()) { // initialiserer resMapen som den største av de den tar inn
            res = new HashMap<>(hm1);
            hash2 = hm2;
        } else {
            res = new HashMap<>(hm2);
            hash2 = hm1;
        }
        for (String key : hash2.keySet()) {
            if (res.containsKey(key)) {
                Subsekvens value1 = hash2.get(key); // henter ut Subsekvensene fra den jeg sammenligner med og den nye
                Subsekvens value2 = res.get(key); // verdien på den som er puttet inn
                value2.endreAntall(value1.hentAntall()); // endrer tallet på den som er i den nye
            } else {
                res.put(key, hash2.get(key)); // om ikke, bare putter den inn
            }
        }
        return res;
    }
}
