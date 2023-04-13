/*****************************************************************************************************
 * Del 2 med tråder for lesing av fil og fletting
 *****************************************************************************************************/
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Oblig5Del2B {
    public static void main(String[] args) throws InterruptedException{
        /** Måler tiden fra programstart til slutt*/
        long tid = System.currentTimeMillis();
        /** monitor objektet ansvarlig for kjøring av tråder */
        Monitor2 monitor2 = new Monitor2();
        /** Maks antall tråder for fletting */
        final int ANT_TRAADER = 8;

        if (args.length == 0) { // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        /** Parameter gitt av bruker for hvilken mappe vi skal teste */
        String mappenavn = args[0];

        System.out.format("\n\u001B[36m" + "+----------------------------------------+--------------+%n");
        System.out.format( "| %-34s | %-12s |%n", "Oppretter en beholder for mappenavnet:", mappenavn);
        System.out.format("\u001B[36m" + "+----------------------------------------+--------------+%n" + "\u001B[0m\n");

    /****************************************************************************************************
     * Leser inn filene og lagrer de i filnavnOversikt
     ****************************************************************************************************/
        long starttid = System.currentTimeMillis();
        /** Lagrer alle filnavn i denne beholderen */
        ArrayDeque<String> filnavnOversikt = new ArrayDeque<>();

        /** Anvender meg av en try-with av ressursene for å avgrense ressurslekkasjer da både FileReader og BufferedReader er Closable*/
        try (FileReader innlestFil = new FileReader(mappenavn + "/metadata.csv");
             BufferedReader leser = new BufferedReader(innlestFil)) 
        {
            String linje;
            while ((linje = leser.readLine()) != null) {
                String[] lestLinje = linje.split(","); // her fjerner jeg "true, false" referanse ##############
                filnavnOversikt.add(lestLinje[0]); // går inn i metadatafilen og leser linjene inn i filnavnOversikt
            }
        } catch (IOException e) {
         
            e.printStackTrace();
        }
        float lesetid = System.currentTimeMillis() - starttid;
  
        System.out.printf("\u001B[32m"+"Det tok " +"%.2f" + " ms med å lese inn filene\n"+"\u001B[0m", lesetid);
        System.out.println("▹ Leste "+filnavnOversikt.size()+" filer");

    /****************************************************************************************************
     * Oppretter hashmaps med tråder, executorService og BlockingQueue
     ****************************************************************************************************/
        starttid = System.currentTimeMillis();
        
        /** Anvender meg av så mange logiske kjerner JVM er i stand til å gi meg for å øke antall parallelle oppgaver */
        int traaderForLesing = Runtime.getRuntime().availableProcessors();
        System.out.println("▹ "+traaderForLesing+" tråder tilgjengelig for lesing");
        /** bruker countdownlatch størrelse for antall filer og teller ned for hver fil lest */
        CountDownLatch countdownLes = new CountDownLatch(filnavnOversikt.size());
        /** executor slik at jeg kan delegere til trådene i henhold til oppgavene som gjenstår */
        ExecutorService executor = Executors.newFixedThreadPool(traaderForLesing);
        /** gir som paramterer alle filene i en blockingqueue for å bidra til oppgavesynkronisering */
        BlockingQueue<String> filnavnKo = new LinkedBlockingQueue<>(filnavnOversikt);

        /** Oppretter og submiter tråder til executor. Det ideelle antallet */
        for (int str = 0; str < traaderForLesing; str++) {
            LeseTrad lesetraad = new LeseTrad(monitor2, mappenavn+"/", countdownLes, filnavnKo);
            executor.submit(lesetraad);
        }
        /** avslutter executor og awaiter på countdown for å fortsette programmet */
        executor.shutdown();
        System.out.println("\nventer på trådene...");
        countdownLes.await();
  
        lesetid = System.currentTimeMillis() - starttid;
        System.out.printf("\u001B[32m"+"Det tok " + "%.2f" + " s å lage hashmaps\n"+ "\u001B[0m",lesetid / 1000);
        System.out.println("▹ Lagde "+monitor2.hvorMangeHashmap()+" hashmaps");

    /****************************************************************************************************
     * Fletter hashmaps med tråder
     ****************************************************************************************************/
        starttid = System.currentTimeMillis();

        CountDownLatch countdownFlett = new CountDownLatch(ANT_TRAADER);
        
        System.out.println("\n▹ " + ANT_TRAADER + " tråder tilgjengelig for fletting");
        for(int i = 0; i < ANT_TRAADER; i++){
            FletteTrad flettetraad = new FletteTrad(monitor2, countdownFlett);
            Thread traad = new Thread(flettetraad);
            traad.start();
        }
    
        System.out.println("venter på trådene...");
        countdownFlett.await();
        lesetid = System.currentTimeMillis() - starttid;
        System.out.printf("\u001B[32mDet tok " + "%.2f" + " s å flette hashmaps\u001B[0m\n", lesetid/1000);
  
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
     
        System.out.println("\033[3m▹ Fikk " + teller + " subsekvenser\033[0m");
        float avsluttet = System.currentTimeMillis() - tid;
        System.out.println("\n\u001B[32m▹ Oppgaven tok " + (int) avsluttet + " ms, som er " + (float) (avsluttet / 1000) + "sek\u001B[0m");        
        System.out.println();
        System.out.println("        \033[4;31m▷ Subsekvensen med flest antall er "+ stoerst+"\033[0m");
        System.out.println();
        System.out.println("\033[3m(Merk: det kan være flere med samme verdi...)\033[0m");
        System.out.println();
        System.out.println("┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅");
    }
}
