import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * Created by Pipjak on 28/05/2017.
 */
public class FileOpen<T> {
    static Integer FILENUMBER;

    /* STATIC METHODS */
    public static boolean isFileIn(int numberOfNodes){
        File file;
        String name;
        String path = System.getProperty("user.dir");

        for(int i = 1; i<=numberOfNodes; i++){
            name = "/" + (new Integer(i)).toString() + ".temp";
            file = new File(path + name);

            if(file.exists() && !file.isDirectory()){
                FileOpen.FILENUMBER = i;
                return true;
            }
        }

        return false;
    }

    /* opening the file */
    public List<T> open(){
        List<T> list;
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/"
                    + FileOpen.FILENUMBER.toString() + ".temp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<T>) ois.readObject();
            ois.close();
        }
        catch (java.lang.ClassNotFoundException | java.io.IOException e){
            e.printStackTrace();
            list = null;
        }
        return list;
    }

}
