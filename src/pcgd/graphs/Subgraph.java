package pcgd.graphs;

import pcgd.dynamics.PCGDException;
import pcgd.dynamics.Renaming;
import pcgd.graphs.Exceptions.EdgePortException;
import pcgd.graphs.Exceptions.VertexNameException;

import java.io.IOException;
import java.util.*;

/**
 * Created by nono on 13/06/17.
 */
public class Subgraph extends Graph{

    protected Map<String, SemiEdge> semiEdges;

    public Subgraph (){
        super();
        this.semiEdges = new HashMap<>();
    }

    /**
     * Adds a new semi edge to the subgraph. Checks if the name is the name of a vertex of the graph and if the port is not already used.
     * @param s a Semi Edge
     * @throws VertexNameException if the name of the vertex does not belong to the graph
     * @throws EdgePortException if the port is already used by an edge or semi edge
     */
    public void addSemiEdge(SemiEdge s) throws VertexNameException, EdgePortException {
        if(! this.vertices.containsKey(s.getName())){
            //The vertex from wich the semi edge goes is not in the graph !
            throw new VertexNameException("Trying to add semi-edge "+s+". Vertex \""+s.getName()+"\" not in the graph.");
        }
        if( this.semiEdges.containsKey(s.toString())){
            throw new EdgePortException("Trying to add semi-edge "+s+". It is already in the graph.");
        }
        for(String str : this.edges.keySet()){
            Edge e = this.edges.get(str);
            if(e.getName1().equals(s.getName())){
                //Semi Edge and Edge comming out of the same vertex, do they have the same port ?
                if(e.getPort1() == s.getPort()){
                    throw new EdgePortException("Trying to add semi-edge "+s+". Port "+s.getPort()+" of \""+s.getName()+"\" already in use in "+e);
                }
            }else if(e.getName2().equals(s.getName())){
                //Semi Edge and Edge comming out of the same vertex, do they have the same port ?
                if(e.getPort2() == s.getPort()){
                    throw new EdgePortException("Trying to add semi-edge "+s+". Port "+s.getPort()+" of \""+s.getName()+"\" already in use in "+e);
                }
            }

        }
        this.semiEdges.put(s.toString(), s);
    }

    /**
     * Adds a new semi edge to the subgraph. Checks if the name is the name of a vertex of the graph and if the port is not already used.
     * @param name a vertex name
     * @param port a port
     * @throws VertexNameException if the name of the vertex does not belong to the graph
     * @throws EdgePortException if the port is already used by an edge or semi edge
     */
    public void addSemiEdge(String name, int port) throws VertexNameException, EdgePortException{
        addSemiEdge(new SemiEdge(name,port));
    }

    /**
     * Adds a new edge to the subgraph. Checks if the names are names of vertex in the graph and if ports are free.
     * @param e the edge.
     * @throws EdgePortException if one of the ports of a vertex is already used
     * @throws VertexNameException if one of the names is not in the graph
     */
    @Override
    public void addEdge(Edge e) throws VertexNameException, EdgePortException{
        if(!this.vertices.containsKey(e.getName1())){
            throw new VertexNameException("Trying to add edge "+e+". Vertex \""+e.getName1()+"\" not in the graph.");
        }
        if(!this.vertices.containsKey(e.getName2())){
            throw new VertexNameException("Trying to add edge "+e+". Vertex \""+e.getName2()+"\" not in the graph.");
        }
        if(e.getName1().equals(e.getName2())){
            throw new VertexNameException("Trying to add edge between \""+e.getName1()+"\" and itself. This is forbidden");
        }
        for(String s : edges.keySet()){
            Edge c = edges.get(s);
            if(c.getName1().equals(e.getName1())){
                if(c.getPort1() == e.getPort1()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort1()+" of \""+c.getName1()+"\" already in use in "+c);
                }
            }else if(c.getName1().equals(e.getName2())){
                if(c.getPort1() == e.getPort2()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort1()+" of \""+c.getName1()+"\" already in use in "+c);
                }
            }else if(c.getName2().equals(e.getName1())){
                if(c.getPort2() == e.getPort1()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort2()+" of \""+c.getName2()+"\" already in use in "+c);
                }
            }else if(c.getName2().equals(e.getName2())){
                if(c.getPort2() == e.getPort2()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort2()+" of \""+c.getName2()+"\" already in use in "+c);
                }
            }
        }
        for(String key : semiEdges.keySet()){
            SemiEdge s = semiEdges.get(key);
            if(e.getName1().equals(s.getName())){
                if(e.getPort1() == s.getPort()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+e.getPort1()+" of \""+e.getName1()+"\" already in use in "+s);
                }
            }else if(e.getName2().equals(s.getName())){
                if(e.getPort2() == s.getPort()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+e.getPort2()+" of \""+e.getName2()+"\" already in use in "+s);
                }
            }
        }
        this.edges.put(e.toString(), e);
    }

    /**
     * Adds a new edge {name1:port1, name2:port2}. Checks if the names are names of vertex in the graph and if ports are free.
     * @param name1 name of the first vertex
     * @param port1 port used on the first vertex
     * @param name2 name of the second vertex
     * @param port2 port used on the second vertex
     * @throws EdgePortException if one of the ports of a vertex is already used
     * @throws VertexNameException if one of the names is not in the graph
     */
    @Override
    public void addEdge(String name1, int port1, String name2, int port2) throws VertexNameException, EdgePortException {
        addEdge(new Edge(name1, port1, name2, port2));
    }

    /**
     * Get the list of the semi edges of the subgraph.
     * @return a Collection of the semi edges of the subgraph
     */
    public Collection<SemiEdge> getSemiEdges(){
        return this.semiEdges.values();
    }

    /**
     * Writes a JSON description, compatible with the visualizer, into the file of given name.
     * This method can only be used of the subgraphs DOES NOT CONTAIN ANY SEMI EDGE, as the visualizer can not treat semi edges.
     * @param filename the name of the file where to output JSON
     * @throws IOException in case of I/O accident.
     * @throws RuntimeException if the subgraph contains semi edges.
     */
    @Override
    public void exportAsJSON (String filename) throws IOException{
        if(! this.semiEdges.isEmpty()){
            throw new RuntimeException("Subgraph still contains semi-edges, impossible to export");
        }
        super.exportAsJSON(filename);
    }

    /**
     * Tests if this subgraph contains a given semi-edge.
     * @param s a semi edge
     * @return true if s belongs to this; false otherwise.
     */
    public boolean containsSemiEdge(SemiEdge s){
        return this.semiEdges.containsKey(s.toString());
    }

    /**
     * Tests if this subgraph contains a given sedge.
     * @param e a semi edge
     * @return true if e belongs to this; false otherwise.
     */
    public boolean containsEdge(Edge e){
        return this.edges.containsKey(e.toString()) || this.edges.containsKey(new Edge(e.getName2(), e.getPort2(), e.getName1(), e.getPort1()).toString());
    }

    /**
     * Tests if this subgraph contains an edge wich one half is the given semi-edge s.
     * @param s a semi edge
     * @return The edge containing s if it is found; null otherwise.
     */
    public Edge containsEdgeforSemiEdge (SemiEdge s){
        for(Edge e : this.edges.values()){
            if((    e.getName1().equals(s.getName()) && e.getPort1() == s.getPort())
                    ||
                    (e.getName2().equals(s.getName()) && e.getPort2() == s.getPort())
                    ){
                return e;
            }
        }
        return null;
    }

    /**
     * Tests if this subgraph contains a Semi edge which is one half of the given edge.
     * @param e an edge
     * @return the semi-edge if it is found; null otherwise.
     */
    public SemiEdge containsSemiEdgeforEdge (Edge e){
        for(SemiEdge s : this.semiEdges.values()){
            if((    e.getName1().equals(s.getName()) && e.getPort1() == s.getPort())
                    ||
                    (e.getName2().equals(s.getName()) && e.getPort2() == s.getPort())
                    ){
                return s;
            }
        }
        return null;
    }

    /**
     * Tests if the vertex of given name is a border vertex, i.e it is in B(this).
     * Border vertices are the vertices carrying the semi edges of the subgraph.
     * @param vertexName the name of a vertex
     * @return true if there is a vertex with the given name in the subgraph and this vertex holds at least one semi edge.
     */
    public boolean isBorder(String vertexName){
        for(SemiEdge s : this.semiEdges.values()){
            if(s.getName().equals(vertexName)){return true;}
        }
        return false;
    }

    /**
     * Returns the degree (number of edges and semi-edges comming out of it) of the vertex of given name in this graph
     * @param vertexName the name of the vertex
     * @return the degree of the vertex.
     */
    @Override
    public int degreeOfVertex(String vertexName){
        int degree = 0;
        for(SemiEdge s : this.semiEdges.values()){
            if(s.getName().equals(vertexName)){degree++;}
        }
        return super.degreeOfVertex(vertexName) + degree;
    }

    /**
     * Tests if this subgraph has a border vertex v such that it is consistent with any vertex of the other graph.
     * Note that the test is only made one way, to declare that two subgraphs are non overlaping, this method must be
     * called twice, inverting this and other.
     * @param other an other subgraph
     * @return A PCGDException containing the names of the overlaping vertices; null if there is no overlap possible.
     */
    public PCGDException bordersOverlapOn(Subgraph other){
        for(Vertex localV : this.vertices.values()){
            if(this.isBorder(localV.getName())){
                for(Vertex otherV : other.vertices.values()){
                    Renaming r = new Renaming();
                    if(twoVerticesConsistent(localV, otherV, other, r) && !r.isTrivial()){
                        return new PCGDException("\""+localV.getName()+"\" and \""+otherV.getName()+"\"");
                    }
                }
            }
        }
        return null;
    }

    protected boolean TVC_semiEdgesIncompatibility(Vertex localVertex, Subgraph other, Renaming rename){
        //Test Local's SemiEdges
        for(SemiEdge localSemi : this.semiEdges.values()){
            if(localSemi.getName().equals(localVertex.getName())) { //localSemi is comming out of localVertex
                SemiEdge otherSemi = rename.apply(localSemi);
                if(!(other.containsSemiEdge(otherSemi) || other.containsEdgeforSemiEdge(otherSemi) != null)){
                    //localSemi is unmatched in other, neither as a semiEdge nor a full edge.
                    return true;
                }
            }//else nothing to do
        }
        return false;
    }

    /**
     * This method tries to calculate a renaming R such that R(this) is superposable with a subgraph other; starting by two
     * given vertices which are the first 'gluing point' candidate. if they are consistent (they do not disagree on states and connectivity),
     * we continue recursively by following the edges to an other pair of point we try to glue. We modify the renaming as we test.
     * At the end we return a boolean to say if this was a success or not. if it was not, do not consider the renaming.
     * @param localVertex a vertex of this that we try to match with otherVertex
     * @param otherVertex a vertex of other that we try to match with localVertex
     * @param other a subgraph
     * @param rename a renaming that we calculate.
     * @return true if this and other are consistent and localVertex and otherVertex coincides; false otherwise.
     */
    @Override
    protected boolean twoVerticesConsistent(Vertex localVertex, Vertex otherVertex, Subgraph other, Renaming rename){
        //Test if we already tested this case
        if(TVC_alreadyTested(localVertex, otherVertex, rename)){return true;}
        //Test if the vertices are in the same state
        if(TVC_differentStates(localVertex, otherVertex)){return false;}
        //Test if the vertices have the same degree
        if(TVC_differentDegrees(localVertex, otherVertex, other)){return false;}
        //Test if we can bind the names of the two vertices
        if(TVC_cantBind(localVertex, otherVertex, rename)){return false;}
        //Test Local's SemiEdges
        if(TVC_semiEdgesIncompatibility(localVertex, other, rename)){return false;}
        //Test localVertex's Edges and recur
        return TVC_edgesAndRecur(localVertex, other, rename);
    }
}
