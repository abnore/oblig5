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
        SubsekvensRegister sr = new SubsekvensRegister();
        Monitor1 monitor = new Monitor1(sr);
        

        if (args.length == 0) { // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0]; // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);
  
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
        CountDownLatch antallHashmaps = new CountDownLatch(filnavn.size());
        for (String string : filnavn) { // Oppretter hashmaps i beholderen basert på metadata.csv
            String fil = mappenavn + "/" + string;
            LeseTrad lt = new LeseTrad(monitor, fil, antallHashmaps);
            Thread traad = new Thread(lt);
            traad.start();            
        }

        while (monitor.hvorMangeHashmap() > 1) {
            HashMap<String, Subsekvens> hm1 = monitor.taUtHashmap();
            HashMap<String, Subsekvens> hm2 = monitor.taUtHashmap();
            HashMap<String, Subsekvens> sammenslatt = Monitor1.flettSammenTo(hm1, hm2);
            monitor.leggTilHashmap(sammenslatt);
        }
        /*
         * Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en
         * HashMap flettetHashmap, slik at jeg kan lese av den, men jeg må legge til
         * igjen etter det siden jeg fjerner den ved å returnere den.
         */
        HashMap<String, Subsekvens> flettetHashmap = monitor.taUtHashmap();
        monitor.leggTilHashmap(flettetHashmap);

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
    }
}

