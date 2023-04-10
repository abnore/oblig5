import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayDeque;
import java.io.FileReader;
import java.io.BufferedReader;

public class Hoved {
    /*
    * Dette er testprogrammet mitt jeg bruker underveis for å catche bugs, prøve ut nye ting og eksperimentere
    */
    public static void main(String[] args) {
        long tid = System.currentTimeMillis();
        Monitor1 monitor1 = new Monitor1();
        // final int ANT_TRAADER = 9;

        if (args.length == 0) { // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0]; // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);
        long starttid = System.currentTimeMillis();
  
        /*
         * Leser inn filen
         */

        BufferedReader leser = null;
        ArrayDeque<String> filnavn = new ArrayDeque<>();
        try {
            FileReader innlestFil = new FileReader(mappenavn + "/metadata.csv");
            leser = new BufferedReader(innlestFil);
            String linje;
            // Scanner sc = new Scanner(new File(mappenavn + "/metadata.csv"));
            while ((linje = leser.readLine()) != null) {
                String[] lestLinje = linje.split(","); // her fjerner jeg "true, false" referanse ##############
                filnavn.add(lestLinje[0]); // går inn i metadatafilen og leser linjene inn i filnavn
            }
        } catch (IOException e) {
            e.printStackTrace(); // skriver ut feilen om det blir noe
        } finally {
            try {
                if (leser != null) {
                    leser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
         * Oppretter hashmaps
         */

        CountDownLatch countdown = new CountDownLatch(filnavn.size());
        for (String string : filnavn) { // Oppretter hashmaps i beholderen basert på metadata.csv
            String fil = mappenavn + "/" + string;
            LeseTrad lt = new LeseTrad(monitor1, fil, countdown);
            Thread traad = new Thread(lt, string);
            traad.start();          
        }
        try {
            System.out.println("Venter på å lage tråder...");
            countdown.await();
            
        } catch (Exception e) {
            System.err.println("Tydeligvis var det noe galt med countdownen");
        }
        System.out.println("Ferdig med å vente");
        float lesetid = System.currentTimeMillis() - starttid;
        System.out.println((int)lesetid/1000 +" sekunder med å lage hashmaps");

        /*
         *  Fletter
         */

        while (monitor1.hvorMangeHashmap() > 1) {
            HashMap<String, Subsekvens> hm1 = monitor1.taUtHashmap();
            HashMap<String, Subsekvens> hm2 = monitor1.taUtHashmap();
            HashMap<String, Subsekvens> sammenslatt = SubsekvensRegister.flettSammenTo(hm1, hm2);
            monitor1.leggTilHashmap(sammenslatt);
        }
         // Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en HashMap flettetHashmap, slik at jeg kan lese av den
        HashMap<String, Subsekvens> flettetHashmap = monitor1.taUtHashmap();

        /*
         * Finner den største subsekvensen
         */
        int teller = 0;
        Subsekvens stoerst = null;
        int stoerstVerdi = 0;
        for (String key : flettetHashmap.keySet()) {
            teller++;
            if (flettetHashmap.get(key).hentAntall() > stoerstVerdi) {
                stoerstVerdi = flettetHashmap.get(key).hentAntall();
                stoerst = flettetHashmap.get(key);
            }
        }
        /*
         * Ferdig
         */
        System.out.println();
        System.out.println("Subsekvensen med flest antall er " + stoerst);
        System.out.println();
        System.out.println("Vi fikk " + teller + " subsekvenser");
        float avsluttet = System.currentTimeMillis() - tid;
        System.out.println("Oppgaven tok " + (int) avsluttet + " ms, som er " + (float) (avsluttet / 1000) + "sek");

    }
}

