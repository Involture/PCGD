package pcgd.graphs;

import pcgd.dynamics.CyclicPermutation;
import pcgd.dynamics.PCGD;
import pcgd.dynamics.Renaming;
import pcgd.graphs.Exceptions.EdgePortException;
import pcgd.graphs.Exceptions.VertexNameException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nono on 13/06/17.
 */
public class Graph{

    protected static final String FRESH_PREFIX = "newVertex";//THIS MUST NOT CONTAIN FRESH_KEYWORD
    protected static final String FRESH_KEYWORD = "fresh";

    protected int freshNumber;
    protected Map<String, Vertex> vertices;
    protected Map<String, Edge> edges;

    /**
     * Creates an empty graph.
     */
    public Graph (){
        if (FRESH_PREFIX.contains(FRESH_KEYWORD)) {
            throw new RuntimeException("FRESH_PREFIX can not contain \"fresh\"");
        }
        this.freshNumber = 0;
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * Adds a new Vertex of given name and state to the Graph. Throws an exception if a vertex with the same name
     * already exists in the graph.
     * @param name the name of the new vertex
     * @param state the state of the new vertex
     * @throws VertexNameException if the name is already used.
     */
    public void addVertex(String name, int state) throws VertexNameException{
        addVertex(new Vertex(name,state));
    }

    /**
     * Adds a vertex to the graph but checks before if the same is fresh.
     * @param v the vertex
     * @throws VertexNameException if the name is already used
     */
    public void addVertex(Vertex v) throws VertexNameException{
        if(this.vertices.containsKey(v.getName())){throw new VertexNameException("Trying to add vertex "+v+". Conflicting vertex name : "+v.getName());}
        this.vertices.put(v.getName(), v);
    }

    /**
     * Adds a new edge {name1:port1, name2:port2} to the graph.
     * @param name1 name of the first vertex
     * @param port1 port used on the first vertex
     * @param name2 name of the second vertex
     * @param port2 port used on the second vertex
     * @throws EdgePortException if one of the ports of a vertex is already used
     * @throws VertexNameException if one of the names is not in the graph
     */
    public void addEdge (String name1, int port1, String name2, int port2) throws EdgePortException, VertexNameException{
        addEdge(new Edge(name1, port1, name2, port2));
    }

    /**
     * Adds an edge to the graph.
     * @param e the edge.
     * @throws EdgePortException if one of the ports of a vertex is already used
     * @throws VertexNameException if one of the names is not in the graph
     */
    public void addEdge(Edge e)throws EdgePortException, VertexNameException{
        if(!this.vertices.containsKey(e.getName1())){
            throw new VertexNameException("Trying to add edge "+e+". Vertex "+e.getName1()+" not in the graph.");
        }
        if(!this.vertices.containsKey(e.getName2())){
            throw new VertexNameException("Trying to add edge "+e+". Vertex "+e.getName2()+" not in the graph.");
        }
        for(String s : edges.keySet()){
            Edge c = edges.get(s);
            if(c.getName1().equals(e.getName1())){
                if(c.getPort1() == e.getPort1()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort1()+" of "+c.getName1()+" already in use in "+c);
                }
            }else if(c.getName1().equals(e.getName2())){
                if(c.getPort1() == e.getPort2()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort1()+" of "+c.getName1()+" already in use in "+c);
                }
            }else if(c.getName2().equals(e.getName1())){
                if(c.getPort2() == e.getPort1()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort2()+" of "+c.getName2()+" already in use in "+c);
                }
            }else if(c.getName2().equals(e.getName2())){
                if(c.getPort2() == e.getPort2()){
                    throw new EdgePortException("Trying to add edge "+e+". Port "+c.getPort2()+" of "+c.getName2()+" already in use in "+c);
                }
            }
        }
        this.edges.put(e.toString(), e);
    }

    /**
     * Writes a JSON description, compatible with the visualizer, into the file of given name.
     * @param filename the name of the file where to output JSON
     * @throws IOException in case of I/O accident.
     */
    public void exportAsJSON (String filename) throws IOException{
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)))){
            Vertex[] verticesArray = this.vertices.values().toArray(new Vertex[0]);
            Edge[] halfEdgesArray = this.edges.values().toArray(new Edge[0]);
            bw.write("{\n\t\"nodes\":[\n");
            int i = 0;
            while(i<verticesArray.length-1){
                bw.write("\t\t"+verticesArray[i].toJSONString()+",\n");
                i++;
            }
            if(i==verticesArray.length-1){
                bw.write("\t\t"+verticesArray[i].toJSONString()+"\n");
            }
            bw.write("\t],\n\n\t\"links\":[\n");
            i = 0;
            while(i<halfEdgesArray.length-1){
                bw.write("\t\t"+halfEdgesArray[i].toJSONString()+",\n");
                i++;
            }
            if(i==halfEdgesArray.length-1){
                bw.write("\t\t"+halfEdgesArray[i].toJSONString()+"\n");
            }
            bw.write("\t]\n}");
        }catch(IOException e){throw e;}
    }

    /**
     * Returns the degree (number of edges comming out of it) of the vertex of given name in this graph
     * @param vertexName the name of the vertex
     * @return the degree of the vertex.
     */
    public int degreeOfVertex(String vertexName){
        int degree = 0;
        for(Edge e : this.edges.values()){
            if(e.getName1().equals(vertexName)){degree++;}
            else if(e.getName2().equals(vertexName)){degree++;}
        }
        return degree;
    }

    /**
     * Part of the TwoVerticesConsistent (TVC) method, tests if the renaming already maps localVertex to otherVertex.
     * @param localVertex one of the two vertices we are trying to unify, the one in the localGraph
     * @param otherVertex one of the two vertices we are trying to unify, the one in the other subgraph
     * @param rename the current renaming we are trying to calculate.
     * @return true if localVertex and otherVertex are mapped to each other, false otherwise (mapped to something else or not mapped at all)
     */
    protected boolean TVC_alreadyTested(Vertex localVertex, Vertex otherVertex, Renaming rename){
        //Test if we already tested this case
        if(rename.containsAntecedent(localVertex.getName())
                && rename.containsImage(otherVertex.getName())
                && rename.get(localVertex.getName()).equals(otherVertex.getName())) {
            //Case already considered
            return true;
        }
        return false;
    }

    /**
     * Part of the TwoVerticesConsistent (TVC) method, tests if localVertex and otherVertex are in different states
     * @param localVertex one of the two vertices we are trying to unify, the one in the localGraph
     * @param otherVertex one of the two vertices we are trying to unify, the one in the other subgraph
     * @return true if the states of the two vertices are different, false if they are equal
     */
    protected boolean TVC_differentStates(Vertex localVertex, Vertex otherVertex){
        //Test if the vertices are in different states
        if(localVertex.getState() != otherVertex.getState()){
            return true;
        }
        return false;
    }

    /**
     * Part of the TwoVerticesConsistent (TVC) method, tests if localVertex and otherVertex have different degrees in their respective graphs
     * @param localVertex one of the two vertices we are trying to unify, the one in the localGraph
     * @param otherVertex one of the two vertices we are trying to unify, the one in the other subgraph
     * @param other the other subgraph that we are trying to unify this to.
     * @return true if the two vertices have different degrees, false if they have the same degree
     */
    protected boolean TVC_differentDegrees(Vertex localVertex, Vertex otherVertex, Subgraph other){
        //Test if the vertices have different degree
        if(this.degreeOfVertex(localVertex.getName()) != other.degreeOfVertex(otherVertex.getName())){
            return true;
        }
        return false;
    }

    /**
     * Part of the TwoVerticesConsistent (TVC) method, tests if localVertex can not be mapped to otherVertex (the method bind of renaming returns false)
     * @param localVertex one of the two vertices we are trying to unify, the one in the localGraph
     * @param otherVertex one of the two vertices we are trying to unify, the one in the other subgraph
     * @param rename the current renaming we are trying to calculate.
     * @return true if localVertex can not be mapped to otherVertex; false otherwise.
     */
    protected Boolean TVC_cantBind(Vertex localVertex, Vertex otherVertex, Renaming rename){
        //Test if we can bind the names of the two vertices
        return (! rename.bind(localVertex.getName(), otherVertex.getName()));
    }

    protected boolean TVC_edgesAndRecur(Vertex localVertex, Subgraph other, Renaming rename){
        //Test localVertex's Edges and recur
        Boolean ok;
        for(Edge localEdge : this.edges.values()){
            SemiEdge closeHalf;//The semi edge wich is the half of localEdge on the localVertex side, if localEdge is linked to localVertex
            SemiEdge farHalf; // The half on the opposite side of the edge, far from localVertex
            if(localEdge.getName1().equals(localVertex.getName())) {
                //localEdge"s end 1 is localVertex
                closeHalf = new SemiEdge(localEdge.getName1(), localEdge.getPort1());
                farHalf = new SemiEdge(localEdge.getName2(), localEdge.getPort2());
            }else if(localEdge.getName2().equals(localVertex.getName())){
                //localEdge's end 2 is localVertex
                closeHalf = new SemiEdge(localEdge.getName2(), localEdge.getPort2());
                farHalf = new SemiEdge(localEdge.getName1(), localEdge.getPort1());
            }else {
                //This edge is not linked to localVertex, we don't care
                closeHalf = null;
                farHalf = null;
            }
            //
            if(closeHalf != null){
                if(other.containsSemiEdge(rename.apply(closeHalf))){
                    //localEdge is matched by a semi edge in other, it's ok !
                    //nothing to do
                }else{
                    Edge otherEdge = other.containsEdgeforSemiEdge(rename.apply(closeHalf));
                    if(otherEdge == null){
                        //LocalEdge is unmatched in other
                        return false;
                    }else{
                        if(otherEdge.getName1().equals(rename.apply(closeHalf).getName())){
                            if(otherEdge.getPort2() != farHalf.getPort()){
                                //the 'far' port is different !
                                return false;
                            }else{
                                //We now follow this edge and test if the graphs are also consistent by the other end of the edge
                                ok = twoVerticesConsistent(
                                        this.vertices.get(farHalf.getName()),
                                        other.vertices.get(otherEdge.getName2()),
                                        other,
                                        rename
                                );
                                if(!ok){return false;}
                            }
                        }else{
                            if(otherEdge.getPort1() != farHalf.getPort()){
                                //the 'far' port is different !
                                return false;
                            }else{
                                //We now follow this edge and test if the graphs are also consistent by the other end of the edge
                                ok = twoVerticesConsistent(
                                        this.vertices.get(farHalf.getName()),
                                        other.vertices.get(otherEdge.getName1()),
                                        other,
                                        rename
                                );
                                if(!ok){return false;}
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * This method tries to calculate a renaming R such that R(this) is superposable with a subgraph other; starting by two
     * given vertices which are the first 'gluing point' candidate. if they are consistent (they do not disagree on states and connectivity),
     * we continue recursively by following the edges to an other pair of point we try to glue. We modify the renaming as we test.
     * At the end we return a boolean to say if this was a success or not. if it was not, do not consider the renaming.
     * @param localVertex a vertex of this we try to match with otherVertex
     * @param otherVertex a vertex of other we try to match with localVertex
     * @param other a subgraph
     * @param rename a renaming that we calculate.
     * @return true if this and other are consistent and localVertex and otherVertex coincides; false otherwise.
     */
    protected boolean twoVerticesConsistent(Vertex localVertex, Vertex otherVertex, Subgraph other, Renaming rename){
        //Test if we already tested this case
        if(TVC_alreadyTested(localVertex, otherVertex, rename)){return true;}
        //Test if the vertices are in the same state
        if(TVC_differentStates(localVertex, otherVertex)){return false;}
        //Test if the vertices have the same degree
        if(TVC_differentDegrees(localVertex, otherVertex, other)){return false;}
        //Test if we can bind the names of the two vertices
        if(TVC_cantBind(localVertex, otherVertex, rename)){return false;}
        //Test localVertex's Edges and recur
        return TVC_edgesAndRecur(localVertex, other, rename);
    }


    /**
     * Calculates all the renamings such that the given pattern is included (after renaming) in this. As this is a graph,
     * it does not contains any semi-edge, so we can use twoVerticesConsistent to do so. (superposition means inclusion here).
     * @param pattern a subgraph that we are trying to identify inside this
     * @return the list of renamings R such that all R(pattern) are included in this.
     */
    public List<Renaming> findPattern(Subgraph pattern){
        ArrayList<Renaming> finds = new ArrayList<>();
        Vertex patternFirst = pattern.vertices.values().toArray(new Vertex[0])[0];
        for(Vertex v : this.vertices.values()){
            Renaming r = new Renaming();
            if(this.twoVerticesConsistent(v, patternFirst, pattern, r)){
                finds.add(r.mirror());
            }
            //
        }
        return finds;
    }

    /**
     * Modifies the graph by applying a given PCGD on it.
     * @param pcgd the dynamic to apply on the graph
     */
    public void apply(PCGD pcgd){
        int pcgdSize = pcgd.size();
        int k = 0;
        while(k < pcgdSize){
            this.apply(pcgd.get(k));
            k++;
        }
    }

    /**
     * Modifies the graph by applying one cyclic permutation on it.
     * @param cycle the cyclic permutation to apply
     */
    protected void apply(CyclicPermutation cycle){
        int cycleSize = cycle.size();
        //Pre-calculate all matchings
        List<List<Renaming>> founds = new ArrayList<>();
        int k = 0;
        while(k<cycleSize){
            founds.add(this.findPattern(cycle.getPattern(k)));
            k++;
        }
        //
        k=0;
        while(k<cycleSize){
            for(Renaming r : founds.get(k)) {
                this.replace(cycle.getPattern(k), cycle.getPattern((k + 1) % cycleSize), cycle.getAttachment(k), r);
                try{
                    this.exportAsJSON("visualizer/graphs/graph-1.json");
                }catch(Exception osef){}
            }
            k++;
        }
    }

    /**
     * Modifies the graph by replacing one time (one renaming) a given pattern by its given image in the graph,
     * using the given attachment map.
     * @param pattern the pattern we previously searched, and that we will remove from the graph
     * @param image the image of this pattern, that we will insert into the graph
     * @param attachment an attachment map to insert the image properly
     * @param r the renaming
     */
    protected void replace(Subgraph pattern, Subgraph image, Map<SemiEdge, SemiEdge> attachment, Renaming r){
        //Remove vertices
        for(String v : pattern.vertices.keySet()){
            this.vertices.remove(r.get(v));
        }
        //Remove edges
        for(Edge e : pattern.edges.values()){
            Edge rem = this.edges.remove(r.apply(e).toString());
            if(rem == null){//If we did not removed anything, maybe the edge is reversed in this graph
                this.edges.remove(r.apply(new Edge(e.getName2(), e.getPort2(), e.getName1(), e.getPort1())).toString());
            }
        }
        Map<String, String> freshNames = new HashMap<>();
        //Add new vertices
        for(Vertex v : image.vertices.values()){
            Vertex n;
            try {
                n = r.apply(v);
            }catch (RuntimeException exep){
                if(v.getName().contains(FRESH_KEYWORD)){
                    freshNames.put(v.getName(), this.getFreshName());
                    n=new Vertex(freshNames.get(v.getName()), v.getState());
                }else {
                    throw exep;
                }
            }
            if(n!=null){this.vertices.put(n.getName(), n);}
        }
        //Transform semi edges
        List<Edge> newEdges = new ArrayList<>();//We can't modify the edges while we iterate on it,
        List<Edge> toDelete = new ArrayList<>();//so we keep the edges to add or remove for later
        for(SemiEdge s : pattern.semiEdges.values()){
            SemiEdge rS = r.apply(s);//Renamed S
            SemiEdge alphaRS;
            if(attachment.get(s).getName().contains(FRESH_KEYWORD)){
                alphaRS = new SemiEdge(freshNames.get(attachment.get(s).getName()), s.getPort());
            }else {
                alphaRS = r.apply(attachment.get(s));//Attachment alpha image of rS
            }
            for(Edge e : this.edges.values()){
                Edge newEdge = null;
                if(e.getName1().equals(rS.getName()) && e.getPort1() == rS.getPort()){
                    newEdge = new Edge(alphaRS.getName(), alphaRS.getPort(), e.getName2(), e.getPort2());
                }else if(e.getName2().equals(rS.getName()) && e.getPort2() == rS.getPort()){
                    newEdge = new Edge(e.getName1(), e.getPort1(), alphaRS.getName(), alphaRS.getPort());
                }
                if(newEdge != null) {
                    toDelete.add(e);
                    newEdges.add(newEdge);
                    break;//Each edge can be matched only once, so no need to continue to iterate
                }
            }
        }
        //Add new Edges
        for(Edge e : image.edges.values()){
            Edge n;
            try{
                n = r.apply(e);
            }catch(RuntimeException exep){
                String name1 = r.get(e.getName1());
                String name2 = r.get(e.getName2());
                if(name1 == null){
                    if(e.getName1().contains(FRESH_KEYWORD)){
                        name1 = freshNames.get(e.getName1());
                    }else{
                        throw exep;
                    }
                }
                if(name2 == null){
                    if(e.getName2().contains(FRESH_KEYWORD)){
                        name2 = freshNames.get(e.getName2());
                    }else{
                        throw exep;
                    }
                }
                n = new Edge(name1, e.getPort1(), name2, e.getPort2());
            }
            this.edges.put(n.toString(),n);
        }
        for(Edge e : toDelete){
            this.edges.remove(e.toString());
        }
        for(Edge e : newEdges){
            this.edges.put(e.toString(), e);
        }
    }

    /**
     * Return a name that is most probably fresh (not already used), using the prefix FRESH_PREFIXE and a unique integer.
     * To assure that the names are fresh, the FRESH_PREFIXE must not be used for any other purpose.
     * @return a freshName
     */
    protected String getFreshName(){
        this.freshNumber++;
        return this.FRESH_PREFIX+freshNumber;
    }
    
}
