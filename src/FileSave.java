/**
 * Created by Pipjak on 28/05/2017.
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class FileSave {

    /* STATIC FIELDS*/
    private static String FILENAME;

    /* METHODS */
    static void FileSave(String newName, List<?> listToSave){

        /* erase the last map */
        FileSave.eraseLastMap();

        /* get the new filename with the path */
        FileSave.FILENAME = System.getProperty("user.dir") + "/" + newName + ".temp";

        /* saving the file */
        try {
            FileOutputStream fos = new FileOutputStream(newName + ".temp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(listToSave);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* PRIVATE METHODS */
    private static void eraseLastMap(){

        /* erasing the old matrix also from disc */
        try {
            FileDelete delete = new FileDelete(FileSave.FILENAME);
            // System.out.println("DELETE, filename is " + FileSave.FILENAME);
            delete.deleteFile();
        }
        catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }
    }
}
