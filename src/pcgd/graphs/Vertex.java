package pcgd.graphs;

/**
 * Created by nono on 13/06/17.
 */
public class Vertex {

    protected String name;
    protected int state;

    public Vertex (String name, int state){
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString(){
        return name+"("+state+")";
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Vertex && ((Vertex) o).getName().equals(name) && ((Vertex) o).getState() == state;
    }

    public String toJSONString(){
        return "{ \"name\":\""+this.getName()+"\",\"state\":"+this.getState()+"}";
    }
}
