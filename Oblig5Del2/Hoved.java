import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;

public class Hoved {
    /*
    * Dette er testprogrammet mitt jeg bruker underveis for å catche bugs, prøve ut nye ting og eksperimentere
    */
    public static void main(String[] args) {
        SubsekvensRegister beholder = new SubsekvensRegister(); // Oppretter en ny beholder av hashmaps med medfølgende metoder

        if (args.length == 0) {              // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        String mappenavn = args[0];           // Det som skrives inn som parameter i terminal
        System.out.println("Oppretter en beholder for mappenavnet: " + mappenavn);

        ArrayList<String> filnavn = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(mappenavn+"/metadata.csv")); 
            while(sc.hasNextLine()){
                String[] lestLinje = sc.nextLine().split(","); // her fjerner jeg "true, false" referanse  ########################
                filnavn.add(lestLinje[0]); // går inn i metadatafilen og leser linjene inn i filnavn
            }
        } catch (Exception e) {
            e.printStackTrace(); // skriver ut feilen om det blir noe
        }
        for (String string : filnavn) { // Oppretter hashmaps i beholderen basert på metadata.csv
            String fil = mappenavn + "/" + string;
            HashMap<String, Subsekvens> hm = SubsekvensRegister.lesFil(fil);
            beholder.leggTilHashmap(hm);
        }
        int antallHashmaps = beholder.hvorMangeHashmap();
        System.out.println("I Denne beholderen er det: "+antallHashmaps+" HashMaps"); // Skriver ut hvor mange hashmaps jeg har

        while(beholder.hvorMangeHashmap() > 1){
            HashMap<String, Subsekvens> hm1 =  beholder.taUtHashmap();
            HashMap<String, Subsekvens> hm2 = beholder.taUtHashmap();
            HashMap<String, Subsekvens> sammenslatt = SubsekvensRegister.flettSammenTo(hm1,hm2);
            beholder.leggTilHashmap(sammenslatt);
        }
        /*
         * Da har jeg flettet alle, sitter igjen med 1 hashmap. Denne lagrer jeg da i en HashMap flettetHashmap, slik at jeg kan lese av den, men jeg må legge til igjen etter det siden jeg fjerner den ved å returnere den.
         */
        HashMap<String, Subsekvens> flettetHashmap = beholder.taUtHashmap();
        beholder.leggTilHashmap(flettetHashmap);

        int teller = 0;
        Subsekvens stoerst = null; // lager en peker stoerst som initialiseres som null, etter loopen vil den peke på en key
        int stoerstVerdi = 0;
        for (String key : flettetHashmap.keySet()) {
            teller++;
            if (flettetHashmap.get(key).hentAntall() > stoerstVerdi) {
                stoerstVerdi = flettetHashmap.get(key).hentAntall();
                stoerst = flettetHashmap.get(key);
            }
        }
        System.out.println("Subsekvensen med flest antall er " + stoerst);
        // Hardkodet 
        // String fil1 = mappenavn + "/" + "fil1.csv";
        // String fil2 = mappenavn + "/" + "fil2.csv";
        // String fil3 = mappenavn + "/" + "fil3.csv";

        // HashMap<String, Subsekvens> hash1 = SubsekvensRegister.lesFil(fil1); 
        // HashMap<String, Subsekvens> hash2 = SubsekvensRegister.lesFil(fil2);
        // HashMap<String, Subsekvens> hash3 = SubsekvensRegister.lesFil(fil3);
        // HashMap<String, Subsekvens> flettet1 = SubsekvensRegister.flettSammenTo(hash1, hash2);
        // HashMap<String, Subsekvens> flettet2 = SubsekvensRegister.flettSammenTo(flettet1, hash3);
        
        // <variabelnavnHashMap>.forEach((key, value) -> System.out.print(value)); // Lambda uttrykk som er identisk med uttrykket under i praksis
        // System.out.println();
        
    
        /*
         * Denne loopen finner den Subsekvens med høyest antall, hvis det er én med størst tall!
         */
        // int teller = 0;
        // Subsekvens stoerst = flettet2.get("");
        // int stoerstVerdi = 0;
        // for (String key : flettet2.keySet()) {
        //     teller++;
        //     if(flettet2.get(key).hentAntall() > stoerstVerdi){
        //         stoerstVerdi = flettet2.get(key).hentAntall();
        //         stoerst = flettet2.get(key);
        //     }
        // }
        // System.out.println("Subsekvensen med flest antall er "+stoerst);


        System.out.println();
        System.out.println("Vi fikk " + teller + " subsekvenser");

        }
    }

