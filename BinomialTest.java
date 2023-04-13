import java.math.BigDecimal;

public class BinomialTest {
    /**
     * BinominalTest sjekker hva sannsynligheten er for at minimum antall suksesser
     * inntreffer
     * ved et gitt antall forsøk. Antar at sannsynligheten er 0.5 for at hver
     * suksess vil inntreffe.
     * 
     * @param antForsok
     * @param suksesser
     * @return p-value, sannsynligheten for at et minst så ekstremt tilfellet vil
     *         inntreffe.
     */
    public static double test(int antForsok, int suksesser) {
        double pValue = 0;
        while (suksesser <= antForsok) {
            pValue += (binomialkoeffisient(antForsok, suksesser).multiply(new BigDecimal(0.5).pow(antForsok)))
                    .doubleValue();
            suksesser++;
        }
        return pValue;
    }

    /**
     * Funksjon som regner ut og returnerer hva binomialkoeffisienten til n over k
     * er.
     * 
     * @param N
     * @param K
     * @return ret
     */
    public static BigDecimal binomialkoeffisient(final int N, final int K) {
        BigDecimal resultat = BigDecimal.ONE;
        for (int k = 0; k < K; k++) {
            resultat = resultat.multiply(BigDecimal.valueOf(N - k))
                    .divide(BigDecimal.valueOf(k + 1));
        }
        return resultat;
    }
}