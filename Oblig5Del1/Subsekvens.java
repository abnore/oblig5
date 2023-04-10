/*
 * Oppgave 1 
 *  ↳ Tar var på en subsekvens (String) og et antall (et heltall)
 *  ↳ Lag en toString metode som skriver ut e.g (ABC,4)
 */

public class Subsekvens {
    public final String subsekvens;
    private int antall;

    public Subsekvens(String subsekvens, int antall){
        this.subsekvens = subsekvens;
        this.antall = antall;
    }
    public int hentAntall(){
        return antall;
    }
    public void endreAntall(int nyttTall){
        antall += nyttTall;
    }

    @Override
    public String toString(){
        return "("+subsekvens+","+antall+")";
    }
}
