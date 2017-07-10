import java.io.IOException;

/**
 * Created by Pipjak on 06/04/2017.
 */
public interface Reading<T> {

    /**
     * function that reads the positions of cities, returns the matrix:
     * i,j --> val, means the distance (of yur choice) from city i to j is val
     * */
    T[][] readPositions(String nameOfFile, int numberOfCities) throws IOException;

    /**
     * reads number of cities from the file
     * returns T
     */
    T readNumberOfCities(String nameOfFile) throws IOException;

}
