package pcgd.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import pcgd.Pair;
import pcgd.dynamics.CyclicPermutation;
import pcgd.dynamics.PCGD;
import pcgd.dynamics.PCGDException;
import pcgd.graphs.*;
import pcgd.graphs.Exceptions.EdgePortException;
import pcgd.graphs.Exceptions.VertexNameException;
//import dynamics.*;

public class Parser {

    private static final String GRAPH_BEGIN = "graph:";
    private static final String GRAPH_END = ":endgraph";
    private static final String SUBGRAPH_BEGIN = "subgraph:";
    private static final String SUBGRAPH_END = ":endsubgraph";
    private static final String DYNAMIC_BEGIN = "dynamic:";
    private static final String DYNAMIC_END = ":enddynamic";
    private static final String CYCLE_BEGIN = "cycle:";
    private static final String CYCLE_END= ":endcycle";
    private static final String PARTS_BEGIN = "parts:";
    private static final String PARTS_END= ":endparts";
    private static final String ATTACHMENT_BEGIN = "attachments:";
    private static final String ATTACHMENT_END= ":endattachments";
    private static final String MAP_BEGIN = "map:";
    private static final String MAP_END= ":endmap";

    private static final String COMMENT_MARK = "//";
    private static int iline;

    private static String removeWhites(String str){
        return str.replaceAll("\\s","");
    }
    private static String ignoreComments(String str){ int index = str.indexOf(COMMENT_MARK); return (index!=-1?str.substring(0,index):str);}
    private static String nextNonEmpty(BufferedReader br)throws IOException, ParseException{
        String line;
        do {
            line = ignoreComments(removeWhites(br.readLine()));
            iline++;
            if(line==null){throw new ParseException("Unexpected end of the file");}
        }while(line.equals(""));
        return line;
    }

    private static void testExpected(String line, String expected) throws ParseException {
        if (!line.equals(expected)) {
            throw new ParseException("Expected \"" + expected + "\" not found at line "+iline+".");
        }
    }

    public static Graph parseGraph(String filename) throws IOException, ParseException{
        Graph g;
        iline = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = nextNonEmpty(br);
            testExpected(line, GRAPH_BEGIN);
            g = parseGraph0(br);
        }
        return g;
    }

    public static Subgraph parseSubgraph(String filename) throws IOException, ParseException {
        Subgraph g;
        iline = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = nextNonEmpty(br);
            testExpected(line, SUBGRAPH_BEGIN);
            g=parseSubgraph0(br);
        }
        return g;
    }

    public static PCGD parsePCGD(String filename)throws IOException, ParseException{
        PCGD p;
        iline = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = nextNonEmpty(br);
            testExpected(line, DYNAMIC_BEGIN);
            p = parsePCGD0(br);
        }
        return p;
    }

    private static Graph parseGraph0 (BufferedReader br) throws IOException, ParseException {
        Graph g = new Graph();
        String line;
        line= nextNonEmpty(br);
        while(! line.equals(GRAPH_END)){
            if(line.startsWith("v:")) {
                try {
                    g.addVertex(parseVertex(line.substring(2)));
                } catch (VertexNameException vne) {
                    throw new ParseException("Error at line "+iline+" : "+vne.getMessage());
                }
            }else if(line.startsWith("e:")){
                try {
                    g.addEdge(parseEdge(line.substring(2)));
                }catch (EdgePortException | VertexNameException e){
                    throw new ParseException("Error at line "+iline+" : "+e.getMessage());
                }
            }else{
                throw new ParseException("Unexpected graph line at line "+iline+".");
            }
            line = nextNonEmpty(br);
        }
        return g;
    }

    private static Subgraph parseSubgraph0 (BufferedReader br) throws IOException, ParseException {
        Subgraph g = new Subgraph();
        String line;
        line = nextNonEmpty(br);
        while(! line.equals(SUBGRAPH_END)){
            if(line.startsWith("v:")){
                try{
                    g.addVertex(parseVertex(line.substring(2)));
                }catch (VertexNameException vne){
                    throw new ParseException("Error at line "+iline+" : "+vne.getMessage());
                }
            }else if (line.startsWith("e:")){
                try{
                    g.addEdge(parseEdge(line.substring(2)));
                }catch (VertexNameException | EdgePortException e){
                    throw new ParseException("Error at line "+iline+" : "+e.getMessage());
                }
            }else if (line.startsWith("s:")){
                try{
                    g.addSemiEdge(parseSemiEdge(line.substring(2)));
                }catch (VertexNameException | EdgePortException e){
                    throw new ParseException("Error at line "+iline+" : "+e.getMessage());
                }
            }else{
                throw new ParseException("Unexpected graph line at line "+iline+".");
            }
            line = nextNonEmpty(br);
        }
        return g;
    }

    private static PCGD parsePCGD0(BufferedReader br) throws IOException, ParseException{
        PCGD p = new PCGD();
        String line;
        line = nextNonEmpty(br);
        while(! line.equals(DYNAMIC_END)){
            int startLine = iline;
            testExpected(line, CYCLE_BEGIN);
            CyclicPermutation cycle = parseCyclicPermutation0(br);
            try {
                p.addPermutation(cycle);
            }catch(PCGDException pe){
                throw new ParseException("Error at line "+startLine+" : "+pe.getMessage());
            }
            line = nextNonEmpty(br);
        }
        return p;
    }

    private static CyclicPermutation parseCyclicPermutation0(BufferedReader br) throws IOException, ParseException{
        CyclicPermutation p = new CyclicPermutation();
        String line = nextNonEmpty(br);
        testExpected(line, PARTS_BEGIN);
        //
        line = nextNonEmpty(br);
        while(! line.equals(PARTS_END)){
            testExpected(line, SUBGRAPH_BEGIN);
            int sgrBegin = iline;
            Subgraph sgr = parseSubgraph0(br);
            try{
                p.addPart(sgr);
            }catch(PCGDException pe){
                throw new ParseException("Error at line "+sgrBegin+" : "+pe.getMessage());
            }
            line = nextNonEmpty(br);
        }
        //
        line = nextNonEmpty(br);
        testExpected(line, ATTACHMENT_BEGIN);
        //
        line = nextNonEmpty(br);
        while(! line.equals(ATTACHMENT_END)){
            testExpected(line,MAP_BEGIN);
            int attBegin = iline;
            Map<SemiEdge, SemiEdge> attachment = parseAttachementMap0(br);
            try{
                p.addAttachment(attachment);
            }catch(PCGDException pe){
                throw new ParseException("Error at line "+attBegin+" : "+pe.getMessage());
            }
            line = nextNonEmpty(br);
        }
        //
        line = nextNonEmpty(br);
        testExpected(line, CYCLE_END);
        return p;
    }

    private static Map<SemiEdge, SemiEdge> parseAttachementMap0 (BufferedReader br) throws IOException, ParseException{
        Map<SemiEdge, SemiEdge> m = new HashMap<>();
        String line = nextNonEmpty(br);
        while(! line.equals(MAP_END)){
            Pair<SemiEdge> rel = parseAttachementRel(line);
            SemiEdge oldValue = m.put(rel.getFirst(), rel.getSecond());
            if(oldValue != null){
                throw new ParseException("Error at line "+iline+" : "+oldValue+" image already defined.");
            }
            line = nextNonEmpty(br);
        }
        return m;
    }

    private static Vertex parseVertex(String str) throws ParseException{
        //X(0)
        try{
            String name =str.substring(0,str.indexOf('('));
            int state = Integer.parseInt(str.substring(str.indexOf('(')+1,str.indexOf(')')));
            return new Vertex(name, state);
        }catch (Exception e){
            throw new ParseException("Line "+iline+" of graph definition malformed.");
        }
    }

    private static Edge parseEdge(String str)throws ParseException{
        //a:0,b:1
        try{
            String srcName = str.substring(0,str.indexOf(':'));
            int srcPort = Integer.parseInt(str.substring(str.indexOf(':')+1,str.indexOf(',')));
            String dstName = str.substring(str.indexOf(',')+1, str.lastIndexOf(':'));
            int dstPort = Integer.parseInt(str.substring(str.lastIndexOf(':')+1,str.length()));
            return new Edge(srcName,srcPort,dstName,dstPort);
        }catch (Exception e){
            throw new ParseException("Line "+iline+" of graph definition malformed.");
        }
    }

    private static SemiEdge parseSemiEdge(String str)throws ParseException{
        //a:0
        try{
            String name = str.substring(0, str.indexOf(":"));
            int port = Integer.parseInt(str.substring(str.indexOf(":")+1, str.length()));
            return new SemiEdge(name, port);
        }catch (Exception e){
            throw new ParseException("Line "+iline+" of graph definition malformed.");
        }
    }

    private static Pair<SemiEdge> parseAttachementRel (String str)throws ParseException{
        //(a:0)(b:1)
        try {
            String n1 = str.substring(1, str.indexOf(":"));
            int p1 = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.indexOf(")")));
            String n2 = str.substring(str.lastIndexOf("(") + 1, str.lastIndexOf(":"));
            int p2 = Integer.parseInt(str.substring(str.lastIndexOf(":") + 1, str.lastIndexOf(")")));
            return new Pair<>(new SemiEdge(n1, p1), new SemiEdge(n2, p2));
        }catch (Exception e){
            throw new ParseException("Line "+iline+" of attachment definition malformed.");
        }
    }
}
