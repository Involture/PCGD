package pcgd.graphs;

/**
 * Created by nono on 13/06/17.
 */
public class SemiEdge {

    private String name;
    private int port;

    public SemiEdge(String name, int port){
        this.name = name;
        this.port = port;
    }

    @Override
    public String toString(){
        return "("+name+":"+port+")";
    }

    @Override
    public boolean equals(Object o){
        return o instanceof SemiEdge && ((SemiEdge) o).name.equals(name) && ((SemiEdge) o).port == port;
    }

    @Override
    public int hashCode(){
        return name.hashCode()+port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

}
