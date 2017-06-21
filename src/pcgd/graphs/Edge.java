package pcgd.graphs;

/**
 * Created by nono on 13/06/17.
 */
public class Edge {

    /**
     * An edge with ports, represented by two Strings and two ints
     */

    protected String name1;
    protected String name2;
    protected int port1;
    protected int port2;

    public Edge(String n1, int p1, String n2, int p2){
        this.name1 = n1;
        this.name2 = n2;
        this.port1 = p1;
        this.port2 = p2;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public int getPort1() {
        return port1;
    }

    public int getPort2() {
        return port2;
    }

    @Override
    public String toString(){
        return "{"+name1+":"+port1+" , "+name2+":"+port2+"}";
    }

    @Override
    public boolean equals (Object o){
        return o instanceof Edge &&
                ((((Edge) o).name1.equals(this.name1) && ((Edge) o).name2.equals(this.name2))||(((Edge) o).name1.equals(this.name2) && ((Edge) o).name2.equals(this.name1)))&&
                ((((Edge) o).port1 == this.port1 && ((Edge) o).port2 == this.port2)||(((Edge) o).port1 == this.port2 && ((Edge) o).port2 == this.port1));
    }

    @Override
    public int hashCode(){
        return this.name1.hashCode() + this.name2.hashCode() + this.port1 + this.port2;
    }

    public String toJSONString(){
        return "{\"source\":\""+this.getName1()+"\",\"target\":\""+this.getName2()+"\",\"portIn\":"+this.getPort1()+",\"portOut\":"+getPort2()+"}";
    }
}
