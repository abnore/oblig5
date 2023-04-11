/*****************************************************************************************************
 * Del 2 med tråder for lesing av fil og fletting
 *****************************************************************************************************/
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.CountDownLatch;

public class Oblig5del2B {
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

    /****************************************************************************************************
     * Leser inn filene
     ****************************************************************************************************/
        long starttid = System.currentTimeMillis();
        
        BufferedReader leser = null;
        ArrayDeque<String> filnavnOversikt = new ArrayDeque<>();
        try {
            FileReader innlestFil = new FileReader(mappenavn + "/metadata.csv");
            leser = new BufferedReader(innlestFil);
            String linje;
            while ((linje = leser.readLine()) != null) {
                String[] lestLinje = linje.split(","); // her fjerner jeg "true, false" referanse ##############
                filnavnOversikt.add(lestLinje[0]); // går inn i metadatafilen og leser linjene inn i filnavnOversikt
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
        float lesetid = System.currentTimeMillis() - starttid;
        System.out.println("Det tok " + lesetid + " ms med å lese inn filene");

    /****************************************************************************************************
     * Oppretter hashmaps med tråder
     ****************************************************************************************************/
        starttid = System.currentTimeMillis();
        
        CountDownLatch countdownLes = new CountDownLatch(filnavnOversikt.size());
        
        for (String filnavn : filnavnOversikt) { // Oppretter hashmaps i beholderen basert på metadata.csv
            LeseTrad lesetraad = new LeseTrad(monitor2, mappenavn + "/", countdownLes);
            Thread traad = new Thread(lesetraad, filnavn);
            traad.start();
        }
        System.out.println("venter på trådene...");
        countdownLes.await();
  
        lesetid = System.currentTimeMillis() - starttid;
        System.out.println("Det tok " + (int) lesetid / 1000 + " sekunder å lage hashmaps");

    /****************************************************************************************************
     * Fletter hashmaps med tråder
     ****************************************************************************************************/
        starttid = System.currentTimeMillis();

        CountDownLatch countdownFlett = new CountDownLatch(ANT_TRAADER);
        
        for(int i = 0; i < ANT_TRAADER; i++){
            FletteTrad flettetraad = new FletteTrad(monitor2, countdownFlett);
            Thread traad = new Thread(flettetraad);
            traad.start();
        }
    
        System.out.println("venter på trådene...");
        countdownFlett.await();
        lesetid = System.currentTimeMillis() - starttid;
        System.out.println("Det tok " + lesetid + " ms å flette hashmaps");
  
        /*****************************************************************************************************
         * Finner den største subsekvensen
         *****************************************************************************************************/
        
        HashMap<String, Subsekvens> flettetHashmap = monitor2.taUtHashmap();

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
