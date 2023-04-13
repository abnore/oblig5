/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Andreas Nore - andrebn@uio.no
 */

/**
 * {@code Subsekvens} er et objekt bestående av en {@code String} og en
 * {@code int}
 * <p>
 * Lagres i et register i en {@code HashMaps<String, Subsekvens>}
 * <blockquote>
 * 
 * <pre>
 * Subsekvens sub = new Subsekvens("ABC",1);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * 
 * @see String
 * @see Integer
 */
public class Subsekvens implements Comparable<Subsekvens>{
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
    
    /**
     * Sammenligner to {@code Subsekvenser} gjennom å sjekke antallet.
     * Om parameter {@code o} er mindre returneres 1.
     * <p>
     * Dersom den er større returneres -1, og dersom de er like returnes 0
     * <p>
     * Dersom parameter er null returnes 0.
     * 
     * @param o  objektet som skal bli sammenlignet
     * @return en negativt int, 0 eller en positiv int så lenge dette objektet er mindre enn, lik eller større enn det spesifiserte objektet.
     * 
     * @see Subsekvenser
     * 
     */
    @Override
    public int compareTo(Subsekvens o) {
        if(o == null) return 0;

        if (this.antall > o.antall){
            return 1;
        } else if (this.antall < o.antall){
            return -1;
        } else {
            return 0;
        }
    }
}
