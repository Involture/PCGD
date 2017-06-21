package pcgd;

import pcgd.dynamics.PCGD;
import pcgd.graphs.Graph;
import pcgd.parser.ParseException;
import pcgd.parser.Parser;

import java.io.IOException;

/**
 * Created by nono on 20/06/17.
 */
public class Main {

    public static void main (String[] args){
        if(args.length != 3){
            System.out.println("3 Arguments Expected :\n" +
                    " - name of the file containing the input graph,\n" +
                    " - name of the file containing the PCGD description,\n" +
                    " - number of steps to run (positive).");
            return;
        }
        Graph g; PCGD p; int nSteps;
        try{
            g = Parser.parseGraph(args[0]);
        }catch (ParseException e){
            System.out.println("Error during parsing of the input graph :\n" +
                    e.getMessage());
            return;
        }catch (IOException e){
            System.out.println("I/O error on file \""+args[0]+"\" : "+e.getMessage());
            return;
        }
        //
        try{
            p=Parser.parsePCGD(args[1]);
        }catch(ParseException e){
            System.out.println("Error during parsing of the PCGD :\n" +
                    e.getMessage());
            return;
        }catch (IOException e){
            System.out.println("I/O error on file \""+args[0]+"\" : "+e.getMessage());
            return;
        }
        //
        try {
            nSteps = Integer.parseInt(args[2]);
            if(nSteps < 0){
                throw new NumberFormatException();
            }
        }catch (NumberFormatException e){
            System.out.println("Requested number of steps can not be parsed or is invalid.");
            return;
        }
        //
        System.out.print("Step 0 : ");
        try{
            g.exportAsJSON("graphs/graph0.json");
            System.out.println(" ok");
        }catch(IOException e){
            System.out.println("I/O error on file \"graph0.json\" : "+e.getMessage());
        }

        //
        for(int i = 1; i<=nSteps; i++){
            System.out.print("Step "+i+" : ");
            g.apply(p);
            try{
                g.exportAsJSON("graphs/graph"+i+".json");
                System.out.println(" ok");
            }catch(IOException e){
                System.out.println("I/O error on file \"graph"+i+".json\" : "+e.getMessage());
            }
        }

    }
}
