package File;
import java.io.*;
import java.util.Properties;


public class CommonCfg {

    private int NumberOfPreferredNeighbors;
    private int UnchokingInterval;
    private int OptimisticUnchokingInterval;
    private String FileName;
    private long FileSize;
    private long PieceSize;

    public void readCommonCfg() {

        String fname = (new File(System.getProperty("user.dir")).getParent() + "/Common.cfg");
        System.out.println(fname);
        //Alternate method where we split file where there is space and store into strings
        /*try {
            BufferedReader buffer = new BufferedReader(new FileReader(new File(fname)));
            String separate;
            Map<String, String> Common_config_details= new HashMap<>();
            while((separate = buffer.readLine()) != null) {
                String s1[] = separate.split(" ");
                Common_config_details.put(s1[0], s1[1]);
            }

         */

        // We use 'properties' to read the persistent properties from Common.cfg
        Properties Common_config_details = new Properties();

        try {

            Common_config_details.load(new BufferedInputStream(new FileInputStream(fname)));
            NumberOfPreferredNeighbors = Integer.parseInt(Common_config_details.getProperty("NumberOfPreferredNeighbors"));
            UnchokingInterval = Integer.parseInt(Common_config_details.getProperty("UnchokingInterval"));
            OptimisticUnchokingInterval = Integer.parseInt(Common_config_details.getProperty("OptimisticUnchokingInterval"));
            FileName = Common_config_details.getProperty("FileName");
            FileSize = Long.parseLong(Common_config_details.getProperty("FileSize"));
            PieceSize = Long.parseLong(Common_config_details.getProperty("PieceSize"));

        } catch (FileNotFoundException e) {
            System.err.println("Common.cfg does not exist");
        } catch (IOException e) {
            System.err.println("IO Exception");
        }
    }

    public int getNumberOfPreferredNeighbors() {
        return NumberOfPreferredNeighbors;
    }

    public int getUnchokingInterval() {
        return UnchokingInterval;
    }

    public int getOptimisticUnchokingInterval() {
        return OptimisticUnchokingInterval;
    }

    public String getFileName() {
        return FileName;
    }

    public long getFileSize() {
        return FileSize;
    }

    public long getPieceSize() {
        return PieceSize;
    }

}
