import java.util.ArrayDeque;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Oblig5Del1 {
    /****************************************************************************************************
     * Main filen for Del 1:
     * ↳ tar inn et mappenavn og utfører analyse av datafilene
     ****************************************************************************************************/
    public static void main(String[] args) {
        long starttid = System.currentTimeMillis(); // Timer for programmets varighet
        SubsekvensRegister beholder = new SubsekvensRegister();
    /*
     * Mappenavn som parameter
     */
        if (args.length == 0) {
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0]; // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);

    /****************************************************************************************************
     * Leser inn filen metadata og lagrer i en ArrayList slik at vi har tilgang til
     * alle filene i samme mappe
     ****************************************************************************************************/

        BufferedReader leser = null;
        ArrayDeque<String> filnavn = new ArrayDeque<>();
        try {
            FileReader innlestFil = new FileReader(mappenavn + "/metadata.csv");
            leser = new BufferedReader(innlestFil);
            String linje;
            
            while ((linje = leser.readLine()) != null){
                String[] lestLinje = linje.split(","); // her fjerner jeg "true, false" referanse ##########
                filnavn.add(lestLinje[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (leser != null) {
                    leser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    /****************************************************************************************************
     * Leser alle filene og oppretter hashmaps for hver enkelt subsekvens
     ****************************************************************************************************/

        int antallLest = 1;
        long sek = System.currentTimeMillis();
        for (String string : filnavn) { // Oppretter hashmaps i beholderen basert på metadata.csv
            String fil = mappenavn + "/" + string;
            beholder.leggTilHashmap(SubsekvensRegister.lesFil(fil));
            statusBar(antallLest++, filnavn.size(), "Oppretter Hashmaps", sek);
        }
        System.out.println();
        System.out.println("I Denne beholderen er det: " + beholder.hvorMangeHashmap() + " HashMaps");

    /***************************************************************************************************
     * Fletter alle hashmaps og legger, slik at jeg kun har én hashmaps, som er en
     * samling av alle
     ***************************************************************************************************/

        antallLest = 1;
        int total = (beholder.hvorMangeHashmap()-1);
        sek = System.currentTimeMillis(); 
        while (beholder.hvorMangeHashmap() > 1) {
            beholder.leggTilHashmap(SubsekvensRegister.flettSammenTo(beholder.taUtHashmap(), beholder.taUtHashmap())); 
            statusBar(antallLest++, total , "Fletter HashMaps", sek);   
        }
    /****************************************************************************************************
     * Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en
     * HashMap flettetHashmap, slik at jeg kan lese av den, men jeg må legge til
     * igjen etter det siden jeg fjerner den ved å returnere den.
     ****************************************************************************************************/

        System.out.println();
        HashMap<String, Subsekvens> flettetHashmap = beholder.taUtHashmap();

        int teller = 0;
        Subsekvens stoerst = null;
        int stoerstVerdi = 0;
        int likeMangeAntall = 0;
        for (String key : flettetHashmap.keySet()) {
            teller++;
            if (flettetHashmap.get(key).hentAntall() > stoerstVerdi) {
                stoerstVerdi = flettetHashmap.get(key).hentAntall();
                stoerst = flettetHashmap.get(key);
                likeMangeAntall = 0 ;
            }
            if (flettetHashmap.get(key).hentAntall() == stoerstVerdi){
                likeMangeAntall++;
            }
        }

        System.out.println();
        System.out.println("Subsekvensen med flest antall er " + stoerst);
        System.out.println("Det er "+likeMangeAntall+" subsekvenser med det tallet");
        System.out.println();
        System.out.println("Vi fikk " + teller + " subsekvenser");
        float avsluttet = System.currentTimeMillis() - starttid;
        System.out.println("Oppgaven tok " + (int)avsluttet + " ms, som er " + (float)(avsluttet/1000) + "sek");

    }
    
    /*****************************************************************************************************
     * Litt pynt for terminalen
     *****************************************************************************************************/

    public static void statusBar(int antall, int total, String oppgave, long sek){
        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\033[0;31m";
        String ANSI_GREEN = "\u001B[32m";
        long current = System.currentTimeMillis();
        float prosent = (float)antall/total*100;
        String farge, tekst, timeglass;

        if (((int) (current - sek)/1000)%2 == 0) {
            timeglass = "⧖";
        } else {
            timeglass = "⧗";
        }
        
        if(prosent==100){
            farge = ANSI_GREEN;
            tekst = "Fullført";
        } else {
            farge = ANSI_RED;
            tekst ="";
        }
        System.out.print(farge+"\r                                          " + (int) prosent + "% "+ tekst);
        System.out.print("\r"+"⦾ "+ oppgave + "   "+timeglass+ "  " +((int)(current-sek)/1000)+" sek"  + ANSI_RESET + "\r");   
    }   
}
