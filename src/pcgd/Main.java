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
        if(args.length < 3 || args.length > 4){
            System.out.println("3 or 4 Arguments Expected :\n" +
                    " - name of the file containing the input graph,\n" +
                    " - name of the file containing the PCGD description,\n" +
                    " - number of steps to run (positive),\n" +
                    " - name of the directory where to put the jsons. (OPTIONNAL)");
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
        String dir;
        if(args.length == 4) {
            dir = args[3];
            if(! dir.endsWith("/")){
                dir = dir+"/";
            }
        }else{
            dir = "graphs/";
        }
        //
        System.out.print("Step 0 : ");
        try{
            g.exportAsJSON(dir+"graph0.json");
            System.out.println(" ok");
        }catch(IOException e){
            System.out.println("I/O error on file \"graph0.json\" : "+e.getMessage());
        }

        //
        for(int i = 1; i<=nSteps; i++){
            System.out.print("Step "+i+" : ");
            g.apply(p);
            try{
                g.exportAsJSON(dir+"graph"+i+".json");
                System.out.println(" ok");
            }catch(IOException e){
                System.out.println("I/O error on file \"graph"+i+".json\" : "+e.getMessage());
            }
        }

    }
}
