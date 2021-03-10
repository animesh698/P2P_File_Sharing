package Peers;
import Common.Client_Connect;
import Common.Server_Connect;
import File.CommonCfg;
import File.PeerCfg;
import ;
import java.util.ArrayList;
import Peers.Messages.bitfield;
import Peers.Messages.piece;

import java.util.HashMap;


public class peerProcess {
    public static String FileName;
    private long FileSize;
    private long PieceSize;
    private int TotalPieces;
    private int peerID;
    private int port;
    private boolean hasFile;
    public static ArrayList<Integer> PeerIDs;
    public static HashMap<Integer, piece> hash_map;

    public static void main(String args[]){
        //create object of the peerProcess class
        peerProcess peer_process = new peerProcess();

        //create object of the Common.cfg class
        CommonCfg common_cfg = new CommonCfg();
        common_cfg.readCommonCfg();
        FileName = common_cfg.getFileName();
        peer_process.FileSize = common_cfg.getFileSize();
        peer_process.PieceSize = common_cfg.getPieceSize();

        /* We are given that the number of pieces will be the file size divided by the total number of pieces
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
        System.out.println();

        //bitfield.bitfield_set(peer_process.hasFile,peer_process.TotalPieces);

        logGenerator.startLogger(peer_process.peerID);



        if(!peer_process.hasFile) {

            hash_map = new HashMap<Integer, piece>();

            Server_Connect peer_listener = new Server_Connect(peer_process.peerID, peer_process.port);
            peer_listener.start();

            Client_Connect connect = new Client_Connect(peer_process.peerID, peer_process.port);
            connect.start();
        }

        else if(peer_process.hasFile) {
            File.FileParser reader = new File.FileParser(peer_process.peerID, peer_process.PieceSize, FileName);
            hash_map = reader.readFile();

            Server_Connect peer_listener = new Server_Connect(peer_process.peerID, peer_process.port);
            peer_listener.start();
        }










    }





}
