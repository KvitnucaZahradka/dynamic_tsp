import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Pipjak on 28/05/2017.
 */
public class FindMinimum extends NotifyingThread implements Runnable {

    private GenerateStatistics combi;
    private Float[][] graph;
    private ArrayList<ConcurrentHashMap<Integer,Float>> aMatrix, newAmatrix;
    private Index ind, newIndex;


    FindMinimum(GenerateStatistics combi, Float[][] graph, Index ind, Index newIndex,
                ArrayList<ConcurrentHashMap<Integer,Float>> aMatrix,
                ArrayList<ConcurrentHashMap<Integer, Float>> newAmatrix){



        synchronized (this) {
            this.combi = combi;
            this.graph = graph;
            this.aMatrix = aMatrix;
            this.newAmatrix = newAmatrix;
            this.ind = ind;
            this.newIndex = newIndex;
        }
    }


    @Override
    public void doRun() {
        while (true) {
            if (combi.lengthOfCombination == 1 && combi.lastIndex == -1) {
                break;
            }

            int[] nextCombination = combi.getNextPermutation().clone();

            if (combi.lastIndex == -1) {
                break;
            }

            /* loop through all except 1 in a given combination */
            for (int jIndex = 0; jIndex < nextCombination.length; jIndex++) {
                if (nextCombination[jIndex] == 1)
                    continue;

                int jValue = nextCombination[jIndex];
                float minimum = this.findMinimumFloat(nextCombination, jValue);

                /* calculate the index of the set S_{1j}, the S_{1j} is a set that contains 1 and j
                *  */
                int indexSj = this.index(nextCombination, jValue);
                this.newAmatrix.get(this.newIndex.modIndex(indexSj)).put(indexSj, minimum);
            }
        }
    }

    public void Run(){
        this.doRun();
    }


    /* PRIVATE METHODS */

    private int index(int[] combination, int k){
        int kIndex = (int) Math.pow(2, k - 1) + (k-1)*Graph.MAX;

        return GenerateStatistics.index(combination) + kIndex;
    }


    private int index(int[] combination, int j, int k){
        int jIndex = (int) Math.pow(2, j - 1);
        int kIndex = (int) Math.pow(2, k - 1) + (k-1)*Graph.MAX;

        return GenerateStatistics.index(combination) + kIndex - jIndex;
    }




    /**
     * III.
     * function that calculates the minimum in the last loop of the algorithm
     * */

    private float findMinimumFloat(int[] nextCombination, int j){
        int index, aNextCombination;
        float aSjKplusC, bestMinimum = Graph.INFINITY;

        for (int i = 0; i <= nextCombination.length; i++){

            if(i==0)
                aNextCombination = 1;
            else
                aNextCombination = nextCombination[i - 1];

            if (aNextCombination == j)
                continue;

            index = this.index(nextCombination, j, aNextCombination);

            try {
                aSjKplusC = this.findValueFloat(index) + this.graph[aNextCombination - 1][j - 1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e){
                System.out.println("index was " + index);
                System.out.println("a next combination is " + aNextCombination);
                System.out.println("j is " + j);
                System.out.println("combination was " );
                GenerateStatistics.printArray(nextCombination);
                System.out.println("why is this index behaving weirdly? " + this.index(nextCombination, j,
                        aNextCombination));

                System.out.println("---------");
                System.out.println("next combination piece " + GenerateStatistics.index(nextCombination));
                System.out.println("kindex is " + ((int) Math.pow(2, aNextCombination - 1) +
                        (aNextCombination-1)*Graph.MAX));
                System.out.println("jindex " + (int) Math.pow(2, j - 1));

                aSjKplusC = 23;
                System.exit(-1);
            }

            if (aSjKplusC < bestMinimum) {
                bestMinimum = aSjKplusC;
            }
        }
        return bestMinimum;
    }

    private float findValueFloat(int index){
        return this.aMatrix.get(this.ind.modIndex(index)).getOrDefault(index, Graph.INFINITY);
    }


}
