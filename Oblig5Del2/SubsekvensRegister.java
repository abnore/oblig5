/*  Oppgave 6
 *      ↳ Bygg en monitor rundt 
 *      ↳ Ta ut vilkårlige HashMaps (med indeks)
 
 *      
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SubsekvensRegister {
    private ArrayList<HashMap<String, Subsekvens>> hashBeholder = new ArrayList<>();
    final static int lengdeSubsekvens = 3; // konstant lengde på sekvenser

    public void leggTilHashmap(HashMap<String,Subsekvens> hm){
        hashBeholder.add(hm);
    }
    
    public HashMap<String, Subsekvens> taUtHashmap(){ // returnerer og fjerner den valgte hashmap'en
        HashMap<String, Subsekvens> midlertidig = hashBeholder.get(0);
        hashBeholder.remove(0);
        return midlertidig;
    }
    
    public int hvorMangeHashmap(){
        return hashBeholder.size();
    }

    public static HashMap<String, Subsekvens> lesFil(String fil) {
        /* Leser inn en fil og returnerer en HashMap som senere
         * skal legges inn i beholderen / arraylisten
         */
        String filnavn = fil;
        HashMap<String,Subsekvens> returnerendeHashMap = new HashMap<>();
        // Legge inn i en try catch pga File og Scanner trenger en Exception handler, spesielt FileNotFoundException - men slår sammen til én IOException
        try {
            Scanner innlesing = new Scanner(new File(filnavn));
            while (innlesing.hasNextLine()){
                String linje = innlesing.nextLine().trim(); // fjerner evt terminator eller whitespace
                if (linje.equalsIgnoreCase("amino_acid")) { // hopper over "amino_acid" linjen
                    continue;
                } else {
                    char[] charFraLinje = linje.toCharArray();  // istedet for split(), får en array av hver bokstav
                    int lengde = charFraLinje.length;
                    int maksSubsekvenser = lengde-(lengdeSubsekvens-1); // dette er om subsekvensene er 3 lange
                    if(lengde >= lengdeSubsekvens){ // om linjen er mindre enn 3 må vi bryte
                        for (int i = 0; i < maksSubsekvenser; i++) {
                            StringBuilder sekvens = new StringBuilder(); // Siden Strings er immutable så blir det 3x færre strings ved å bygge én og konvertere
                            for (int j = 0; j < lengdeSubsekvens; j++){
                                sekvens.append(charFraLinje[i+j]); // så bokstav ved indeks i (+0, +1 og +2) fra der vi er (tilsvarer tre bokstaver f.o.m der vi er)
                            }
                            String resultat = sekvens.toString();
                            Subsekvens sub = new Subsekvens(resultat, 1);
                            
                            returnerendeHashMap.put(resultat, sub);
                        }
                    } else {
                        break; // stopper programmet
                    }
                }
            }
            innlesing.close();
            
        } catch (IOException e) {
            System.err.println("Her har det skjedd en feil med filen!"); // Velger System.err av pedagogiske grunner
            e.printStackTrace(System.err);
        }
        return returnerendeHashMap;
    } 

    public static HashMap<String, Subsekvens> flettSammenTo(HashMap<String, Subsekvens> hm1, HashMap<String, Subsekvens> hm2){
        HashMap<String, Subsekvens> res = new HashMap<>(hm1); // initialiserer den som den ene (hm1 i dette tilfellet)
        
        for (String key1 : hm1.keySet()) {  // for hver key
            for(String key2 : hm2.keySet()){ // sjekker med hver key i den andre
                if (key1.equalsIgnoreCase(key2)){ // 
                    res.put(key2, hm2.get(key2)); // putter den inn først (kanskje dette er den lengste filen, og den overskriver uansett)
                    Subsekvens value1 = hm1.get(key1); // henter ut Subsekvensene fra den jeg sammenligner med og den nye
                    Subsekvens value2 = res.get(key2); // verdien på den som er puttet inn
                    int nyttTall = value1.hentAntall() + value2.hentAntall(); // slår sammen antallene
                    value2.endreAntall(nyttTall); // endrer tallet på den som er i den nye
            } else {
                 res.put(key2, hm2.get(key2)); // om ikke, bare putter den inn
            }
        }
    } 
        return res;
    }     
}

