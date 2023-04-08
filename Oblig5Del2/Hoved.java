import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class Hoved {
    /*
    * Dette er testprogrammet mitt jeg bruker underveis for å catche bugs, prøve ut nye ting og eksperimentere
    */
    public static void main(String[] args) {
        Monitor1 monitor1 = new Monitor1();
        final int ANT_TRAADER = 9;

        if (args.length == 0) { // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0]; // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);
        long starttid = System.currentTimeMillis();
  
        ArrayList<String> filnavn = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(mappenavn + "/metadata.csv"));
            while (sc.hasNextLine()) {
                String[] lestLinje = sc.nextLine().split(","); // her fjerner jeg "true, false" referanse
                                                               // ########################
                filnavn.add(lestLinje[0]); // går inn i metadatafilen og leser linjene inn i filnavn
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace(); // skriver ut feilen om det blir noe
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

        countdown = new CountDownLatch(9);
        Monitor2 monitor2 = new Monitor2(monitor1);
        /*
         * Fletter
         */
        System.out.println(monitor2.hvorMangeHashmap());

        for (int i = 0; i < ANT_TRAADER; i++ ) { // Oppretter hashmaps i beholderen basert på metadata.csv
            FletteTrad ft = new FletteTrad(monitor2, countdown);
            Thread traad = new Thread(ft);
            traad.start();
        }
        try {
            System.out.println("Venter på fletting...");
            countdown.await();

        } catch (Exception e) {
            System.err.println("Tydeligvis var det noe galt med countdownen");
        }
        // System.out.println("Fletter");
        // while (monitor.hvorMangeHashmap() > 1) {
        //     HashMap<String, Subsekvens> hm1 = monitor.taUtHashmap();
        //     HashMap<String, Subsekvens> hm2 = monitor.taUtHashmap();
        //     HashMap<String, Subsekvens> sammenslatt = Monitor1.flettSammenTo(hm1, hm2);
        //     monitor.leggTilHashmap(sammenslatt);
        // }

        System.out.println("Ferdig med fletting");
        /*
         * Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en
         * HashMap flettetHashmap, slik at jeg kan lese av den, men jeg må legge til
         * igjen etter det siden jeg fjerner den ved å returnere den.
         */
        HashMap<String, Subsekvens> flettetHashmap = null;
        try {
            flettetHashmap = monitor2.taUtHashmap();
            monitor2.leggTilHashmap(flettetHashmap);
        } catch (Exception e) {
           
        }

        int teller = 0;
        Subsekvens stoerst = null; // lager en peker stoerst som initialiseres som null, etter loopen vil den peke
                                   // på en key
        int stoerstVerdi = 0;
        for (String key : flettetHashmap.keySet()) {
            teller++;
            if (flettetHashmap.get(key).hentAntall() > stoerstVerdi) {
                stoerstVerdi = flettetHashmap.get(key).hentAntall();
                stoerst = flettetHashmap.get(key);
            }
        }
        System.out.println("Subsekvensen med flest antall er " + stoerst);
        System.out.println();
        System.out.println("Vi fikk " + teller + " subsekvenser");
        float avsluttet = System.currentTimeMillis() - starttid;
        System.out.println("Tiden er "+avsluttet +" ms, som er "+(avsluttet/1000)+"sek");
    }
}

