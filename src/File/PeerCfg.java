package File;

//import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;

public class PeerCfg {
    private int peerID;
    private String hostname;
    private int port;
    private boolean hasFile;
    private int mypeerID;


    // 1 if complete file and 0 if not complete file, no in between
    private String fname = (new File(System.getProperty("user.dir")).getParent() + "/PeerInfo.cfg");


     public PeerCfg(int mypeerID) {
         this.mypeerID = mypeerID;

     }

    public void readPeerCfg() {
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(fname));
            String sp = null;
            int peerCount = 0;
            while ((sp = buffer.readLine()) != null) {
                peerCount++;
                System.out.println(sp);
                String split[] = sp.split(" ");
                if(mypeerID == Integer.parseInt(split[0])) {
                    peerID = Integer.parseInt(split[0]);
                    hostname = split[1];
                    port = Integer.parseInt(split[2]);

                    if(split[3].equals("1"))
                        hasFile = true;
                    else
                        hasFile = false;
                }

            }
            buffer.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("input output error");
        }


    }
    // This function returns an array of all the peer ids
    public ArrayList<Integer> getPeerIDs() {

        ArrayList<Integer> peer_ids = new ArrayList<Integer>();
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(fname));
            String sp = null;
            while ((sp = buffer.readLine()) != null) {
                String[] split = sp.split(" ");
                peer_ids.add(Integer.parseInt(split[0]));
            }

            buffer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("List of all peerIDs:");

        return peer_ids;
    }

    //This function returns all the Details we just saved
    public ArrayList<String[]> getPeerDetails() {

        ArrayList<String[]> peer_details = new ArrayList<String[]>();
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(fname));
            String sp = null;
            while ((sp = buffer.readLine()) != null) {
                String[] split = sp.split(" ");

                if(mypeerID != Integer.parseInt(split[0])) {
                    peer_details.add(split);
                }
                else
                    break;
            }

            buffer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return peer_details;
    }

    public int getpeerID() {
        return peerID;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean isHasFile() {
        return hasFile;
    }


}
