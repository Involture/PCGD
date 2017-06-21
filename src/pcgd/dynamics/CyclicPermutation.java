package pcgd.dynamics;

import pcgd.graphs.SemiEdge;
import pcgd.graphs.Subgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nono on 14/06/17.
 */
public class CyclicPermutation {
    private List<Subgraph> parts;
    private List<Map<SemiEdge, SemiEdge>> attachments;

    public CyclicPermutation(){
        this.parts = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }

    /**
     * Adds a part to the Cyclic permutation, with the followings checks:
     *  - All parts must be added first, and then all attachments, so if an attachment has already been added, we throw an exception,
     *  - If the subgraph is non-trivially-overlapping with itself, we throw an exception,
     *  - If the subgraph is non-trivially-overlapping with any of the other subgraphs already added, we throw an exception.
     * @param s the subgraph to add
     * @throws PCGDException if one of the above conditions is met.
     */
    public void addPart (Subgraph s) throws PCGDException{
        if(this.attachments.size() > 0){
            throw new PCGDException("An Attachment Map as already been added, add all parts first.");
        }
        PCGDException e = s.bordersOverlapOn(s);
        if(e != null){//Parts MUST NOT be non-trivially-self-overlapping
            throw new PCGDException("Self-overlap risk in part "+parts.size()+" between vertices "+e.getMessage());
        }
        int k = 0;
        while(k<parts.size()){
            e = s.bordersOverlapOn(parts.get(k));
            if(e!=null){//Parts MUST NOT be overlapping with each others
                throw new PCGDException("Overlap risk between part "+parts.size()+" and part "+k+" between vertices "+e.getMessage());
            }
            e=parts.get(k).bordersOverlapOn(s);
            if(e!=null){//Parts MUST NOT be overlapping with each others
                throw new PCGDException("Overlap risk between part "+k+" and part "+parts.size()+" between vertices "+e.getMessage());
            }
            k++;
        }
        parts.add(s);
    }

    /**
     * Adds an attachment map to the cyclic permutation. The following properties are testes and an exception is thrown if they are not satisfied :
     *  - There can not be more attachments than parts in a cyclic permutation
     *  - For the i-nth attachment alpha(i) :
     *      * dom( alpha(i) ) = S(P(i)) [The domain of the i-th attachment must be the semi-edges set of the i-th part]
     *      * im( alpha(i) ) = S(P(i+1)) [The image of the i-th attachment must be the semi-edges set of the (i+1)-th part]
     *      * So we are sure that alpha does map S(Pi) into S(Pi+1).
     * @param attachment the attachment map to add
     * @throws PCGDException if one of the above condition is not met.
     */
    public void addAttachment (Map<SemiEdge, SemiEdge> attachment) throws PCGDException{
        if(this.attachments.size() >= this.parts.size()){
            throw new PCGDException("Number of attachments exceeding number of parts !");
        }
        PCGDException e = checkAttachment(attachment);
        if(e != null ) {
            throw e;
        }
        attachments.add(attachment);
    }

    /**
     * Checks if the number of attachment and the number of parts are the same.
     * @return true if there is the same number of attachment and parts in this cyclic permutation.
     */
    public boolean checkSizes(){
        return this.parts.size() == this.attachments.size();
    }

    /**
     * Check if a given attachment map alpha (which would become the i-th attachment map of the permutation)
     * actually maps S(Pi) into S(Pi+1). See method 'addAttachment()' for more details.
     * @param alpha the attachment to test.
     * @return a PCGDException with a message explainig the problem, if there is a problem; null otherwise.
     */
    private PCGDException checkAttachment(Map<SemiEdge, SemiEdge> alpha){
        PCGDException e;
        SemiEdge[] sp = parts.get(attachments.size()).getSemiEdges().toArray(new SemiEdge[0]);
        SemiEdge[] domalpha = alpha.keySet().toArray(new SemiEdge[0]);
        e = included(sp, domalpha);
        if(e != null){
            return new PCGDException("Semi edge "+e.getMessage()+" of part "+attachments.size()+" does not appear in the domain of attachment "+attachments.size());
        }
        e = included(domalpha, sp);
        if(e != null){
            return new PCGDException("Semi edge "+e.getMessage()+" of the domain of attachment "+attachments.size()+" does not appear in part "+attachments.size());
        }
        //
        SemiEdge[] sp1 = parts.get((attachments.size()+1)%parts.size()).getSemiEdges().toArray(new SemiEdge[0]);
        SemiEdge[] imalpha = alpha.values().toArray(new SemiEdge[0]);
        e = included(sp1, imalpha);
        if(e != null){
            return new PCGDException("Semi edge "+e.getMessage()+" of part "+((attachments.size()+1)%parts.size())+" does not appear in the image of attachment "+attachments.size());
        }
        e = included(sp1, imalpha);
        if(e != null){
            return new PCGDException("Semi edge "+e.getMessage()+" of the image of attachment "+attachments.size()+" does not appear in part "+((attachments.size()+1)%parts.size()));
        }
        //
        return null;
    }

    /**
     * Test the inclusion of the 'small' semi edges set into the 'big' one.
     * i.e. for all element e of 'small' there is an element e' in 'big' such as e = e'
     * @param small a semi-edge array
     * @param big a semi-edge array
     * @return true if 'small' is included in 'big',
     */
    private PCGDException included (SemiEdge[] small, SemiEdge[] big){
        int i = 0;
        while(i<small.length){
            int j = 0;
            while(j < big.length && (!small[i].equals(big[j]))){
                j++;
            }
            if(j==big.length){return new PCGDException(small[i].toString());}
            i++;
        }
        return null;
    }

    /**
     * Return the number of attachment maps and parts of it is the same, -1 otherwise.
     * @return the number of attachment maps and parts of it is the same, -1 otherwise
     */
    public int size(){
        if(checkSizes()){
            return this.parts.size();
        }else{
            return -1;
        }
    }

    /**
     * Get the i-th part of the cyclic permutation.
     * @param i the index of the desired part.
     * @return the i-th part.
     */
    public Subgraph getPattern(int i){
        return this.parts.get(i);
    }

    /**
     * Get the i-th attachment map of the cyclic permutation
     * @param i the index of the desired attachment
     * @return the i-th attachment
     */
    public Map<SemiEdge, SemiEdge> getAttachment(int i){
        return this.attachments.get(i);
    }

}

