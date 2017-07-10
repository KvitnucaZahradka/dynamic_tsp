import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pipjak on 06/04/2017.
 */
public class Calculations {

    /* STATIC FIELDS */
    private static String  FILE = "tsp.txt";


    /* MAIN FUNCTION */
    public static void main(String args[]) throws IOException, BrokenBarrierException, InterruptedException, ExecutionException {
        Graph graph = new Graph(Calculations.FILE);

        long startTime = System.currentTimeMillis();

        /* calculations */
        int distance = graph.calculateTSPdistance();

        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println("the time elapsed in miliseconds: " + estimatedTime);
        System.out.println("the best TSP distance is: " + distance);
        System.out.println("note, the distance rounded down to the closest integer ");

        /* saving the answer */
        PrintStream out = new PrintStream(new FileOutputStream("tsp_out.txt"));
        System.setOut(out);

        /* printing the line into the out */
        out.println("THE SMALLEST TSP DISTANCE IS: ");
        out.println(distance);
        out.println("NOTE: the distance was rounded down to the next integer");
        out.close();
    }
}
