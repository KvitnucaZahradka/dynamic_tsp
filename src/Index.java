/**
 * Created by Pipjak on 25/04/2017.
 */

public class Index {

    /*PUBLIC FIELDS*/
    public int mod;

    public Index(int numberOfElements, int threshold){
        this.mod = this.calculateMod(numberOfElements, threshold);
    }

    /* PUBLIC METHODS */
    public int modIndex(int index){
        if(this.mod == 1){
            return 0;
        }
        else{
            return index % this.mod;
        }
    }

    /* PRIVATE METHODS */
    private int calculateMod(int numberOfElements, int threshold){
        if(numberOfElements<=threshold){
            return 1;
        }
        else{
         return (int) Math.ceil((double) numberOfElements / (double) threshold);
        }
    }
}
