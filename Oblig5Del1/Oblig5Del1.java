import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;

public class Oblig5Del1 {
    /*
     * Main filen for Del 1:
     *  ↳ tar inn et mappenavn og utfører analyse av datafilene
     */
    public static void main(String[] args) {
        
        long starttid = System.currentTimeMillis(); // Timer for programmets varighet

        SubsekvensRegister beholder = new SubsekvensRegister(); // Oppretter en ny beholder av hashmaps med medfølgende metoder
        /*
         * Mappenavn som parameter
         */
        if (args.length == 0) {
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0]; // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);

        /*
         * Leser inn filen metadata og lagrer i en ArrayList slik at vi har tilgang til alle filene i samme mappe
         */
        ArrayList<String> filnavn = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(mappenavn + "/metadata.csv"));
            while (sc.hasNextLine()) {
                String[] lestLinje = sc.nextLine().split(","); // her fjerner jeg "true, false" referanse
                                                               // ########################
                filnavn.add(lestLinje[0]); // går inn i metadatafilen og leser linjene inn i filnavn
            }
        } catch (Exception e) {
            e.printStackTrace(); // skriver ut feilen om det blir noe
        }

        /*
         * Leser alle filene og oppretter hashmaps for hver enkelt subsekvens
         */

        int antallLest = 0;
        long sek = System.currentTimeMillis() / 1000;
        String oppgave = "Oppretter Hashmaps";
        for (String string : filnavn) { // Oppretter hashmaps i beholderen basert på metadata.csv
            String fil = mappenavn + "/" + string;
            HashMap<String, Subsekvens> hm = SubsekvensRegister.lesFil(fil);
            int antallIgjen = filnavn.size();
            beholder.leggTilHashmap(hm);
            antallLest++;
            statusBar(antallLest, antallIgjen, oppgave, sek);
        }
        System.out.println();
        int antallHashmaps = beholder.hvorMangeHashmap();
        System.out.println("I Denne beholderen er det: " + antallHashmaps + " HashMaps");

        /*
         * Fletter alle hashmaps og legger, slik at jeg kun har én hashmaps, som er en samling av alle
         */
        oppgave = "Fletter HashMaps";
        antallLest = 0;
        int total = beholder.hvorMangeHashmap()-1;
        sek = System.currentTimeMillis()/1000; 
        while (beholder.hvorMangeHashmap() > 1) {
            HashMap<String, Subsekvens> hm1 = beholder.taUtHashmap();
            HashMap<String, Subsekvens> hm2 = beholder.taUtHashmap();
            HashMap<String, Subsekvens> sammenslatt = SubsekvensRegister.flettSammenTo(hm1, hm2);
            antallLest++;
            statusBar(antallLest,total, oppgave, sek);   
            beholder.leggTilHashmap(sammenslatt);
        }
        /*
         * Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en
         * HashMap flettetHashmap, slik at jeg kan lese av den, men jeg må legge til
         * igjen etter det siden jeg fjerner den ved å returnere den.
         */
        HashMap<String, Subsekvens> flettetHashmap = beholder.taUtHashmap();
        beholder.leggTilHashmap(flettetHashmap);

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
        System.out.println();
        System.out.println("Subsekvensen med flest antall er " + stoerst);
        System.out.println();
        System.out.println("Vi fikk " + teller + " subsekvenser");
        float avsluttet = System.currentTimeMillis() - starttid;
        System.out.println("Oppgaven tok " + (int)avsluttet + " ms, som er " + (float)(avsluttet/1000) + "sek");

    }
    public static void statusBar(int antall, int total, String oppgave, long sek){
        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\033[0;31m";
        String ANSI_GREEN = "\u001B[32m";
        long current = System.currentTimeMillis() / 1000;
        float prosent = (float)antall/total*100;
        String farge, tekst, timeglass;

        if (((int) (current - sek))%2 == 0) {
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
        System.out.print("\r"+"⦾ "+ oppgave + "   "+timeglass+ "  " +((int)(current-sek))+" sek"  + ANSI_RESET + "\r");

    }
}
