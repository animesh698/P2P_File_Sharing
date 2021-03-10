package File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.*;
import java.util.Date;

public class logGenerator {
    public static int peerID;
    private static Logger logger = Logger.getLogger(logGenerator.class.getName());



    public static void begin_logging(int peerid) {
        peerID = peerid;
        try {
            String curr_directory = System.getProperty("user.dir");
            String k = curr_directory + "\\log_peer_" + peerid + ".log";
            FileHandler handle = new FileHandler(k);
            logger.addHandler(handle);
        } catch (IOException e) {
        }
    }

    void TCP_connection_initiate(int peerid) {
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + " : Peer " + " makes a connection to peer" + peerid + ".";
        logger.info(s);
    }

    void TCP_connection_made(int peerid) {
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "is connected to peer" + peerid + ".";
        logger.info(s);
    }

    void change_Preferred_neighbours(int[] neighbours) {
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "has preferred neighbours" + neighbours;
        logger.info(s);


    }

    void change_OptimisticallyUnchoked_Neighbour(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "has optimistically unchoked neighbour"+peerid+".";
        logger.info(s);
    }

    void made_Unchoked(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "is unchoked by"+peerid+".";
        logger.info(s);
    }

    void haveReceived(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "received the have message from" + peerid+".";
        logger.info(s);
    }

    void interestedReceived(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "received the interested message from" + peerid+ ".";
        logger.info(s);
    }

    void notInterestedReceived(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "received the not interested message from" + peerid+ ".";
        logger.info(s);

    }

    void downloadedPiece(int peerid,int pieceIndex){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "downloaded the"+pieceIndex +"from"+ peerid+ ".";
        logger.info(s);
    }

    void downloadComplete(){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "downloaded the complete file"+ ".";
        logger.info(s);
    }


    /*public static void main(String[] args) {
        logGenerator hand = new logGenerator();
        hand.begin_logging(1001);

    }


     */
}









