import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.CountDownLatch;

public class Oblig5del2B {
    /*
     * Del 2 med tråder for lesing av fil og fletting
     */
    public static void main(String[] args) throws InterruptedException{
        long tid = System.currentTimeMillis();
        SubsekvensRegister subsekvensRegister = new SubsekvensRegister();
        Monitor2 monitor2 = new Monitor2(subsekvensRegister);
        final int ANT_TRAADER = 8;

        if (args.length == 0) { // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0]; // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);
        long starttid = System.currentTimeMillis();

    /****************************************************************************************************
     * Leser inn filen
     ****************************************************************************************************/

        BufferedReader leser = null;
        ArrayDeque<String> filnavn = new ArrayDeque<>();
        try {
            FileReader innlestFil = new FileReader(mappenavn + "/metadata.csv");
            leser = new BufferedReader(innlestFil);
            String linje;
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

    /****************************************************************************************************
     * Oppretter hashmaps med tråder
     ****************************************************************************************************/

        CountDownLatch countdownLes = new CountDownLatch(filnavn.size());
        for (String string : filnavn) { // Oppretter hashmaps i beholderen basert på metadata.csv
            String fil = mappenavn + "/" + string;
            LeseTrad lt = new LeseTrad(monitor2, fil, countdownLes);
            Thread traad = new Thread(lt, string);
            traad.start();
        }
        System.out.println("venter på trådene...");
        countdownLes.await();
  
        float lesetid = System.currentTimeMillis() - starttid;
        System.out.println("Det tok " + (int) lesetid / 1000 + " sekunder med å lage hashmaps");

    /****************************************************************************************************
     * Fletter hashmaps med tråder
     ****************************************************************************************************/
        starttid = System.currentTimeMillis();
        CountDownLatch countdownFlett = new CountDownLatch(ANT_TRAADER);
        FletteTrad flettetraad = new FletteTrad(monitor2, countdownFlett);

        for(int i = 0; i < ANT_TRAADER; i++){
            Thread traad = new Thread(flettetraad);
            traad.start();
        }
    
        System.out.println("venter på trådene...");
        countdownFlett.await();
        lesetid = System.currentTimeMillis() - starttid;
        System.out.println("Det tok " + (int) lesetid / 1000 + " sekunder med å flette hashmaps");

        // Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en
        // HashMap flettetHashmap, slik at jeg kan lese av den
        
        HashMap<String, Subsekvens> flettetHashmap = monitor2.taUtHashmap();

    /*****************************************************************************************************
     * Finner den største subsekvensen
     *****************************************************************************************************/

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
    /*****************************************************************************************************
     * Ferdig
     *****************************************************************************************************/
     
        System.out.println();
        System.out.println("Subsekvensen med flest antall er " + stoerst);
        System.out.println();
        System.out.println("Vi fikk " + teller + " subsekvenser");
        float avsluttet = System.currentTimeMillis() - tid;
        System.out.println("Oppgaven tok " + (int) avsluttet + " ms, som er " + (float) (avsluttet / 1000) + "sek");        
    }
}
