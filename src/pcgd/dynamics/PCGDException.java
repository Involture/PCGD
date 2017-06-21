package pcgd.dynamics;

/**
 * Created by nono on 14/06/17.
 */
public class PCGDException extends Exception{

    /**
     * A general exception thrown when there are problems with PCGD in the wide meaning (PCGD, Cyclic permutation,
     * subgraph wich is part of a cyclic permutation, attachment map, ...)
     * @param message the message giving details about the cause of the exception.
     */
    public PCGDException(String message){
        super(message);
    }
}
