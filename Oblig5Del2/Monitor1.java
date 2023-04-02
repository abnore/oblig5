import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Monitor1 {
    private Lock laas = new ReentrantLock();
    private Condition ikkeTomt = laas.newCondition();
    private Condition ikkeFullt = laas.newCondition();

    protected SubsekvensRegister subsekvensRegister;
    /* leggTilHashmap()
     * taUtHashmap()
     * hvorMangeHashmap()
     * lesFil()
     * flettSammenTo()
     */
    public Monitor1(SubsekvensRegister subsekvensRegister){
        this.subsekvensRegister = subsekvensRegister;
    }
   
}
