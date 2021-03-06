package Peer;
import java.io.*;
public class peerProcess {
    private static int peerID;
    private static File logFile;
    private static Logs logs;


    private static File dir;
    public static void main(String[] args){
        // step 1: create sub directory or check if already exists
            peerID = Integer.parseInt(args[0]);
            try {
                dir = new File("peer_" + peerID);
                if (dir.exists() == false){
                    dir.mkdir();
                }
                // create log files for every new peer
                logFile = new File(System.getProperty("user.dir") + "/" + "log_peer_" + peerID + ".log");
                if (logFile.exists() == false)
                    logFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFile.getAbsolutePath(), true));
                writer.flush();
                logFile = new Logs(writer); // This wont work right now

            }








    }

}
