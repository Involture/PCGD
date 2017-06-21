package pcgd.dynamics;

import pcgd.graphs.Edge;
import pcgd.graphs.SemiEdge;
import pcgd.graphs.Vertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nono on 15/06/17.
 */
public class Renaming {

    /**
     * A bijective renaming function
     */

    //private boolean ok;
    private Map<String, String> r;

    public Renaming(){
        //this.ok = true;
        this.r = new HashMap<>();
    }

    /**
     * Sets R(a) = b, returns a boolean to indicate if this was possible or not
     * @param a an antecedent
     * @param b the image of a
     * @return false if a already has an image, true otherwise.
     */
    public boolean bind(String a, String b){
        boolean ok = (this.r.put(a,b) == null);
        return ok;
    }

    /**
     * Returns R(s)
     * @param s the antecedent
     * @return R(s), or null if no image was set for s.
     */
    public String get(String s){
        return this.r.get(s);
    }


    /*public boolean getOk(){
        return this.ok;
    }*/

    /*public void setNo(){
        this.ok = false;
    }*/

    /**
     * Test if this renaming is trivial, which means it is the identity.
     * @return true is R is the identity, false otherwise
     */
    public boolean isTrivial(){
        for(String key : r.keySet()){
            if(! key.equals(r.get(key))){return false;}
        }
        return true;
    }

    /**
     * Applies the renaming to a vertex and returns the result. May throw a RuntimeException if the vertex name has no image by the renaming.
     * @param v the vertex to rename
     * @return a renamed vertex, the state is not changed.
     */
    public Vertex apply(Vertex v){
        String n = r.get(v.getName());
        if(n==null){throw new RuntimeException("Undefined renaming on vertex");}
        return new Vertex(n,v.getState());
    }

    /**
     * Applies the renaming to a semi edge and returns the result. May throw a RuntimeException if the vertex name has no image by the renaming.
     * @param s the semi edge to rename
     * @return a renamed semi edge, the ports is not changed
     */
    public SemiEdge apply(SemiEdge s){
        String n = r.get(s.getName());
        if(n==null){throw new RuntimeException("Undefined renaming on semi-edge");}
        return new SemiEdge(n,s.getPort());
    }

    /**
     * Applies the renaming to an edge and returns the result. May throw a RuntimeException if one of the vertices names has no image by the renaming.
     * @param e the edge to rename
     * @return a renamed edge, the ports are not changed
     */
    public Edge apply(Edge e){
        String n1 = r.get(e.getName1());
        String n2 = r.get(e.getName2());
        if(n1==null || n2==null){throw new RuntimeException("Undefined renaming on edge");}
        return new Edge(n1,e.getPort1(), n2, e.getPort2());
    }

    /**
     * Tests if the renaming contains the given name as an antecedent
     * @param s a name
     * @return true if this name has an image by the renaming; false otherwise
     */
    public boolean containsAntecedent(String s){
        return this.r.containsKey(s);
    }

    /**
     * Tests if the renaming contains the given name as an image
     * @param s a name
     * @return true if the renaming contains a name a such as R(a) = s; false otherwise
     */
    public boolean containsImage(String s){
        return this.r.containsValue(s);
    }

    /**
     * Returns the inverse of the renaming (it is a bijection).
     * @return the inverse of the renaming.
     */
    public Renaming mirror(){
        Renaming mirror = new Renaming();
        //if(! ok){mirror.setNo();};
        for(String s : this.r.keySet()){
            mirror.bind(this.r.get(s), s);
        }
        return mirror;
    }
}
