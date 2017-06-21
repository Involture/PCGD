package pcgd;

import pcgd.dynamics.PCGD;
import pcgd.dynamics.Renaming;
import pcgd.graphs.Subgraph;
import pcgd.parser.Parser;
import pcgd.graphs.Graph;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nono on 13/06/17.
 */
public class Tests {

    public static void main(String[] args) throws Exception{
        System.out.println("DEBUT");

        Graph g = Parser.parseGraph("res/B1.gr");
        PCGD p = Parser.parsePCGD("res/B1.pcgd");
        g.exportAsJSON("res/graph0.json");
        g.apply(p);
        g.exportAsJSON("res/graph1.json");
        System.out.println("FIN");
    }
}
