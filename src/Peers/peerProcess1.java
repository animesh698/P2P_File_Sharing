package Peers;
import File.CommonCfg;
import File.PeerCfg;
import java.util.ArrayList;
public class peerProcess1 {
    public static String FileName;
    private long FileSize;
    private long PieceSize;
    private int TotalPieces;
    private int peerID;
    private int port;
    private boolean hasFile;
    public static ArrayList<Integer> PeerIDs;

    public static void main(String args[]){
        //create object of the peerProcess class
        peerProcess1 peer_process = new peerProcess1();

        //create object of the Common.cfg class
        CommonCfg common_cfg = new CommonCfg();
        common_cfg.readCommonCfg();
        FileName = common_cfg.getFileName();
        peer_process.FileSize = common_cfg.getFileSize();
        peer_process.PieceSize = common_cfg.getPieceSize();

        /* We are given that the number of pieces will be the file size divided by thet otal number of pieces
           and this has to be an int
         */
        peer_process.TotalPieces = (int) Math.ceil((double)peer_process.FileSize/peer_process.PieceSize);
        PeerCfg peer_details = new PeerCfg(Integer.parseInt(args[0]));
        peer_details.readPeerCfg();
        PeerIDs = peer_details.getPeerIDs();
        peer_process.peerID = peer_details.getPort();
        peer_process.port = peer_details.getPort();
        peer_process.hasFile = peer_details.isHasFile();

        System.out.println(PeerIDs.toString());









    }




}
