
/** 
 * 
 * Obligatoriske oppgave 5 - Parallellitet og tråder
 * 
 * Oppgave 12 Fullt program <p>
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Andreas Nore - andrebn@uio.no
 * 13.4.2023
*/


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.math3.stat.inference.BinomialTest;
import org.apache.commons.math3.stat.inference.AlternativeHypothesis;

public class Oblig5Hele {
    public static void main(String[] args) throws InterruptedException {
        /** Måler tiden fra programstart til slutt */
        long tid = System.currentTimeMillis();
        /** monitor objektene ansvarlig for kjøring av tråder */
        Monitor2 hattSykdom = new Monitor2();
        Monitor2 hattSykdomIkke = new Monitor2();
        /** Maks antall tråder for fletting */
        final int ANT_TRAADER = 8;
        /** Sannsynlighet for binomialtesting */
        final double SANNSYNLIGHET = 0.5;

        if (args.length == 0) { // dette gjør at vi må legge til et mappenavn
            System.err.println("Error: Vennligst gi et mappenavn som et command-line argument.");
            return;
        }
        /** Parameter gitt av bruker for hvilken mappe vi skal teste */
        String mappenavn = args[0];
        System.out.println("┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅");

        System.out.format("\n\u001B[36m" + "+----------------------------------------+--------------+%n");
        System.out.format("| %-34s | %-12s |%n", "Oppretter en beholder for mappenavnet:", mappenavn);
        System.out.format("\u001B[36m" + "+----------------------------------------+--------------+%n" + "\u001B[0m\n");

    /****************************************************************************************************
     * Leser inn filene og lagrer de i filnavnOversikt
     ****************************************************************************************************/
        long starttid = System.currentTimeMillis();
        /** Lagrer alle filnavn i disse beholderene basert på om de har vært smittet eller ikke */
        Deque<String> filnavnSmittet = new ArrayDeque<>();
        Deque<String> filnavnFriske = new ArrayDeque<>();

        /** Anvender meg av en try-with av ressursene for å avgrense ressurslekkasjer da både FileReader og BufferedReader er Closable */
        try (FileReader innlestFil = new FileReader(mappenavn + "/metadata.csv");
             BufferedReader leser = new BufferedReader(innlestFil)) 
        {
            String linje;
            while ((linje = leser.readLine()) != null) {
                String[] lestLinje = linje.split(",");
                try {
                    if(lestLinje[1].equals("False")){
                        filnavnFriske.add(lestLinje[0]);
                    } else {
                        filnavnSmittet.add(lestLinje[0]);
                    }
                    
                } catch (Exception e) {
                    filnavnFriske.add(lestLinje[0]);
                    System.err.println("Denne filen har ikke true/false betingelser");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        float lesetid = System.currentTimeMillis() - starttid;

        System.out.printf("\u001B[32m" + "Det tok " + "%.2f" + " ms å lese inn filene\n" + "\u001B[0m", lesetid);
        System.out.println("▹ Leste " + (filnavnSmittet.size()+filnavnFriske.size()) + " filer");

    /****************************************************************************************************
     * Oppretter hashmaps med tråder, executorService og BlockingQueue
     ****************************************************************************************************/
        starttid=System.currentTimeMillis();
        /** Anvender meg av så mange logiske kjerner JVM er i stand til å gi meg for å øke antall parallelle oppgaver */
        int traaderForLesing = Runtime.getRuntime().availableProcessors();
        System.out.println("▹ "+traaderForLesing+" tråder tilgjengelig for lesing");
        /** bruker countdownlatch størrelse for antall filer og teller ned for hver fil lest */ 
        CountDownLatch countdownLes = new CountDownLatch(filnavnSmittet.size()+filnavnFriske.size());
        /** executor slik at jeg kan delegere til trådene i henhold til oppgavene som gjenstår */
        ExecutorService executor = Executors.newFixedThreadPool(traaderForLesing);
        /** gir som parameterer alle filene i en blockingqueue hver for å bidra til oppgavesynkronisering */
        BlockingQueue<String> filnavnTrue = new LinkedBlockingQueue<>(filnavnSmittet);
        BlockingQueue<String> filnavnFalse = new LinkedBlockingQueue<>(filnavnFriske);

        /** Oppretter og submiter tråder til executor. Det ideelle antallet av logiske tråder*/
        for(int str = 0;str<traaderForLesing;str++)
        {
            LeseTrad lesetraadTrue = new LeseTrad(hattSykdom, mappenavn + "/", countdownLes, filnavnTrue);
            LeseTrad lesetraadFalse = new LeseTrad(hattSykdomIkke, mappenavn + "/", countdownLes, filnavnFalse);
            executor.submit(lesetraadTrue);
            executor.submit(lesetraadFalse);
        }
        /** avslutter executor og awaiter på countdownLes å fortsette programmet */
        System.out.println("\nventer på trådene...");
        executor.shutdown();
        countdownLes.await();

        lesetid=System.currentTimeMillis()-starttid;
        System.out.printf("\u001B[32m"+"Det tok "+"%.2f"+" s å lage hashmaps\n"+"\u001B[0m",lesetid/1000);
        System.out.println("▹ Lagde "+(hattSykdom.hvorMangeHashmap()+hattSykdomIkke.hvorMangeHashmap())+" hashmaps");
        
    /****************************************************************************************************
     * Fletter hashmaps med tråder - uten executor service for morro skyld 8+8 parallelt
     ****************************************************************************************************/
        starttid = System.currentTimeMillis();

        CountDownLatch latchFriske = new CountDownLatch(ANT_TRAADER);
        CountDownLatch latchSmittet = new CountDownLatch(ANT_TRAADER);

        System.out.println("\n▹ " + ANT_TRAADER+"+"+ANT_TRAADER + " tråder for fletting");
        for (int i = 0; i < ANT_TRAADER; i++) {
            FletteTrad traadHattSykdom = new FletteTrad(hattSykdom, latchSmittet);
            Thread traadSyk = new Thread(traadHattSykdom);
            traadSyk.start();
            FletteTrad traadHattSykdomIkke = new FletteTrad(hattSykdomIkke, latchFriske);
            Thread traadFrisk = new Thread(traadHattSykdomIkke);
            traadFrisk.start();
        }
        System.out.println("venter på trådene...");
        latchSmittet.await();
        latchFriske.await();
        lesetid = System.currentTimeMillis() - starttid;
        System.out.printf("\u001B[32mDet tok " + "%.2f" + " s å flette hashmaps\u001B[0m\n", lesetid / 1000);
        
        /** Resultatet av flettingen */
        HashMap<String, Subsekvens> smittet = hattSykdom.taUtHashmap();
        HashMap<String, Subsekvens> friske = hattSykdomIkke.taUtHashmap();

    /*****************************************************************************************************
     * Finner antall subsekvenser
     *****************************************************************************************************/

        int teller = 0;
        teller += smittet.size();

        for (String key : friske.keySet()) {
            if (!smittet.containsKey(key)) {
                teller++;
            }
        }
        System.out.println("\033[3m▹ Fikk " + teller + " subsekvenser\033[0m");
  
    /****************************************************************************************************
     * Sammenligner gjennom compareTo metoden i Subsekvens hvilken som forekommer oftest i forhold 
     ****************************************************************************************************/

        int forekomst = 0;
        String indeks = "";
        Subsekvens flestForekomst = null;

        /** Om ikke vi tester Data skjer den første blokken som oppgaven sier - uten binomialtest.
         * Sjekkes Data er vi i else blokken med full binomialtest og analyse
         */
        if(! mappenavn.equalsIgnoreCase("Data") ){

            for (Map.Entry<String, Subsekvens> entry : smittet.entrySet()) {
                String sekvens = entry.getKey();
                Subsekvens smittetSubsekvens = entry.getValue();
                int smittetAntall = smittetSubsekvens.hentAntall();
               
                /** Om den finnes, sett differansen - om ikke sett 0 */
                Subsekvens friskeSubsekvens = friske.get(sekvens);
                int differansen = friskeSubsekvens != null ? smittetAntall - friskeSubsekvens.hentAntall() : 0;
    
                if (smittetSubsekvens.compareTo(friskeSubsekvens) > 0 && differansen > forekomst) {
                    forekomst = differansen;
                    indeks = sekvens;
                } else if (smittetSubsekvens.compareTo(friskeSubsekvens) <= 0 && smittetAntall > forekomst) {
                    forekomst = smittetAntall;
                    indeks = sekvens;
                }
            }
            flestForekomst = smittet.get(indeks);
            System.out.println();
            System.out.println("   \033[4;31m▷ Subsekvensen(e) som forekommer oftest blandt de smittete er:\033[0m");
            System.out.println("   "+flestForekomst);
        
        } else {

            List<String> oftestForekomst = new ArrayList<>();
            HashMap<String, Double> lavePVerdier = new HashMap<>();
    
            for (Map.Entry<String, Subsekvens> entry : smittet.entrySet()) {

                int forsok = 0;
                int suksess = 0;
                
                String sekvens = entry.getKey();
                Subsekvens smittetSubsekvens = entry.getValue();
                Subsekvens friskeSubsekvens = friske.get(sekvens);
                
                int smittetAntall = smittetSubsekvens.hentAntall();
                forsok += smittetAntall;
                suksess = smittetAntall;
                /** Om sekvensen var hos de friske, returner antallet; om ikke returner 0 */
                int friskeAntall = friskeSubsekvens != null ? friskeSubsekvens.hentAntall() : 0;
                
                forsok += friskeAntall;
    
                if (smittetAntall - friskeAntall >= 7) {
                    oftestForekomst.add(sekvens);
                    
                }
                /** Binomial test - sjekker null-hypotesen og lagrer sekvensene som har lavere p enn 0.05 */
                BinomialTest binomialTest = new BinomialTest();
                double p = binomialTest.binomialTest(forsok, suksess, SANNSYNLIGHET, AlternativeHypothesis.GREATER_THAN );

                if (p < SANNSYNLIGHET) {
                    lavePVerdier.put(sekvens,p);              
                }
            }

            System.out.println();
            System.out.println("▷ Etter en binomialanalyse er det " + lavePVerdier.size()
                    + " sekvenser med p-verdi under 5%.\nDette kan indikere en sterk sammenheng mellom sekvensen og risikoen\nfor å bli smittet.\nHusk at statistisk assosiasjon trenger ikke bety årsak.");
            System.out.print("\n   \033[4;31m▷ De sekvensene som opptrer minst 7 ganger mer er følgende:\033[0m\n");
            System.out.println("   (sekvens, antall) p-verdi\n");
            for (String string : oftestForekomst) {
                System.out.print(String.format("   | %s \u001B[32m%.4f\u001B[0m |\n", smittet.get(string), lavePVerdier.get(string)));
            }
            System.out.println();
            }

    /*****************************************************************************************************
     * Ferdig
     *****************************************************************************************************/
     
        float avsluttet = System.currentTimeMillis() - tid;
        System.out.println();
        System.out.println("\u001B[32m▹ Oppgaven tok " + (int) avsluttet + " ms, som er " + (float) (avsluttet / 1000) + "sek\u001B[0m");        
        System.out.println();
        System.out.println("┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅");
    
        }
    }

   
