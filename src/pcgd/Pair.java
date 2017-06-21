package pcgd;

/**
 * Created by nono on 14/06/17.
 */
public class Pair<V> {

    private V first;
    private V second;

    public Pair(V a, V b){
        first = a;
        second = b;
    }

    public V getFirst(){
        return first;
    }

    public V getSecond(){
        return second;
    }
}
