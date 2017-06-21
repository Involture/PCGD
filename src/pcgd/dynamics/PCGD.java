package pcgd.dynamics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nono on 14/06/17.
 */
public class PCGD {

    private List<CyclicPermutation> cycles;
    private int n;

    public PCGD(){
        this.cycles = new ArrayList<>();
        int n = 0;
    }

    /**
     * Add a cyclic permutation to the PCGD. This cycle is guaranteed (no overlaps, correct attachment mapping, ...)
     * by the tests made during its creation, except for the matching number of parts and attachments which is checked here.
     * @param p the cyclic permutation to add
     * @throws PCGDException if the number of parts and attachments are different in p
     */
    public void addPermutation(CyclicPermutation p) throws PCGDException{
        if(p.checkSizes()) {
            cycles.add(p);
        }else{
            throw new PCGDException("Impossible to add a cyclic permutation with different numbers of parts and attachment maps.");
        }
    }

    /**
     * Get the size of the PCGD, i.e. the number of cycles it contains.
     * @return the number of cyclic permutations in this PCGD
     */
    public int size(){
        return cycles.size();
    }

    /**
     * Get the i-th cycle of the PCGD
     * @param i the index of the desired cycle.
     * @return the i-th cyclic permutation
     */
    public CyclicPermutation get(int i){
        return i<cycles.size()?cycles.get(i):null;
    }

}
