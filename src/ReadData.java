import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pipjak on 06/04/2017.
 */
class ReadData implements Reading {


    @Override
    public Double[][] readPositions(String nameOfFile, int numberOfCities) throws IOException {
        Double[][] result;
        Set<Double[]> cityPositions = new HashSet<>();

        String line;
        String[] arr;

        /**
         * in this part we are reading in the data and converting to a matrix with distances
         */
        try {
            File file = new File(nameOfFile);
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            /* skip one line */
            line = bufferedReader.readLine();


            while ((line = bufferedReader.readLine()) != null ) {

                arr = line.split("\\s+");

                /* putting the coordinates to a set */
                Double[] coordinates = new Double[2];

                coordinates[0] = Double.parseDouble(arr[0]);
                coordinates[1] = Double.parseDouble(arr[1]);

                cityPositions.add(coordinates);
            }

            // closing the fileReader:
            fileReader.close();
        }
        catch (IOException e ){
            e.printStackTrace();
        }

        result = calculateGraphWeights(cityPositions, numberOfCities);

        return result;
    }

    public Integer[][] readPositionsInteger(String nameOfFile, int numberOfCities) throws IOException {
        Double[][] tempResult;
        Integer[][] result;

        Set<Double[]> cityPositions = new HashSet<>();

        String line;
        String[] arr;

        /**
         * in this part we are reading in the data and converting to a matrix with distances
         */
        try {
            File file = new File(nameOfFile);
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            /* skip one line */
            line = bufferedReader.readLine();


            while ((line = bufferedReader.readLine()) != null ) {

                arr = line.split("\\s+");

                /* putting the coordinates to a set */
                Double[] coordinates = new Double[2];

                coordinates[0] = Double.parseDouble(arr[0]);
                coordinates[1] = Double.parseDouble(arr[1]);

                cityPositions.add(coordinates);
            }

            // closing the fileReader:
            fileReader.close();
        }
        catch (IOException e ){
            e.printStackTrace();
        }

        tempResult = calculateGraphWeights(cityPositions, numberOfCities);

        result = this.turnInteger(tempResult);
        return result;
    }

    public Float[][] readPositionsFloat(String nameOfFile, int numberOfCities) throws IOException {
        Double[][] tempResult;
        Float[][] result;

        Set<Double[]> cityPositions = new HashSet<>();

        String line;
        String[] arr;

        /**
         * in this part we are reading in the data and converting to a matrix with distances
         */
        try {
            File file = new File(nameOfFile);
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            /* skip one line */
            line = bufferedReader.readLine();


            while ((line = bufferedReader.readLine()) != null ) {

                arr = line.split("\\s+");

                /* putting the coordinates to a set */
                Double[] coordinates = new Double[2];

                coordinates[0] = Double.parseDouble(arr[0]);
                coordinates[1] = Double.parseDouble(arr[1]);

                cityPositions.add(coordinates);
            }

            // closing the fileReader:
            fileReader.close();
        }
        catch (IOException e ){
            e.printStackTrace();
        }

        tempResult = calculateGraphWeights(cityPositions, numberOfCities);

        result = this.turnFloat(tempResult);
        return result;
    }



    @Override
    public Integer readNumberOfCities(String nameOfFile) throws IOException {
        String line;
        String[] arr = new String[1];

        try{

            File file = new File(nameOfFile);
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            /* skip one line */
            line = bufferedReader.readLine();
            arr = line.split("\\s+");

            /* closing the fileReader */
            fileReader.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(arr[0]);
    }


    /* PRIVATE METHODS TO CALCULATE DISTANCES */

    /**
    * private and static method that calculate distances using metric of your choice
    * */
    private static double calculateDistance(Double[] posA, Double[] posB){
        double result;

        result = Math.sqrt(Math.pow(posA[0]-posB[0], 2.0) + Math.pow(posA[1]-posB[1], 2.0));

        return result;
    }

    /**
     * private method that calculate the all graph weights
     */
    private Double[][] calculateGraphWeights(Set<Double[]> cityPositions, int numberCities){
        Double[][] result = new Double[numberCities][numberCities];
        Object[] posit = cityPositions.toArray();

        /* posA = position of city A; posB = position of city B */
        Double[] posA, posB;

        for(int ix = 0; ix<numberCities; ix++){
            posA = (Double[]) posit[ix];

            for(int iy = ix; iy<numberCities; iy++){
                posB = (Double[]) posit[iy];

                result[ix][iy] = result[iy][ix] = calculateDistance(posA, posB);
            }
        }
        return result;
    }

    /**
     * Turn into the integer method:
     * */
    private Integer[][] turnInteger(Double[][] tempResult){
        Integer[][] result = new Integer[tempResult.length][tempResult[0].length];

        for(int i = 0; i<tempResult.length; i++){
            for(int j = 0; j<tempResult[0].length; j++){
                result[i][j] = (int) Math.floor(tempResult[i][j]*Graph.MULTI);
            }
        }
        return result;
    }

    /**
     * Turn into the float method:
     * */

    private Float[][] turnFloat(Double[][] tempResult){
        Float[][] result = new Float[tempResult.length][tempResult[0].length];

        for(int i = 0; i<tempResult.length; i++){
            for(int j = 0; j<tempResult[0].length; j++){
                result[i][j] = (float) (Math.floor(tempResult[i][j]* (double) Graph.MULTI)/(double) Graph.MULTI);
            }
        }
        return result;
    }

}
