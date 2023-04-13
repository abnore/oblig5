/**
 * 
 * Objekt bestående av en String og et antall int 
 * Lagres i et register i HashMaps<String, Subsekvens>
 * 
 * 
 * @author Andreas Nore - andrebn@uio.no
 */


public class Subsekvens {
    /** Tre bokstaver fra innlest fil  */
    public final String SUBSEKVENS;
    /** Hvor mange ganger den oppstår i hver fil */
    private int antall;

    /**
     * Konstruktøren som oppretter subsekvenser
     * @param subsekvens    String av tre bokstaver
     * @param antall    int som starter på 1 deretter tas det summen
     */
    public Subsekvens(String subsekvens, int antall){
        SUBSEKVENS = subsekvens;
        this.antall = antall;
    }
    /**
     * Returnerer antall forekomster av denne bokstavsekvensen 
     * @return int
     */
    public int hentAntall(){
        return antall;
    }
    /**
     * Summerer antallet når vi sammenligner med andre sekvenser
     * @param nyttTall
     */
    public void endreAntall(int nyttTall){
        antall += nyttTall;
    }
    /**
     * Skriver ut objektet e.g (ABC,1)
     */
    @Override
    public String toString(){
        return "("+SUBSEKVENS+","+antall+")";
    }
}
