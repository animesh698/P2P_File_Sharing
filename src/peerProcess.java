import java.io.*;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.*;
import java.text.DateFormat;
import java.util.Date;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

class Peer_info{
    private int current_P_ID;
    private int P_ID;
    private boolean Interested;
    private Socket soc;
    private byte[] BitField;
    private boolean choked = true;
    private boolean optimisticallyUnchoked = false;
    private double downloadRate = 0;
    private boolean unchoked;

    public boolean isUnchoked() {
        choked = false;
        return choked;
    }

    public void setUnchoked() {
        this.choked = false;
    }

    public boolean isChoked() {
        return choked;
    }

    public void setChoked(boolean choked) {
        this.choked = choked;
    }

    public boolean isOptimisticallyUnchoked() {
        return optimisticallyUnchoked;
    }

    public void setOptimisticallyUnchoked(boolean x) {
        this.optimisticallyUnchoked = x;
    }

    public double getDownloadRate() {
        return downloadRate;
    }

    public void setDownloadRate(double downloadRate) {
        this.downloadRate = downloadRate;
    }

    public int get_current_P_ID(){
        return current_P_ID;
    }

    public void set_current_P_ID(int current_P_ID){
        this.current_P_ID = current_P_ID;
    }

    public int get_P_ID(){
        return P_ID;
    }
    public void set_P_ID(int P_ID){
        this.P_ID = P_ID;
    }

    public boolean get_isInterested(){
        return Interested;
    }
    public void set_is_Interested(boolean Interested){
        this.Interested = Interested;
    }

    public Socket get_soc(){
        return soc;
    }
    public void set_soc(Socket soc){
        this.soc = soc;
    }
    public byte[] get_BitField(){
        return BitField;
    }
    public void set_BitField(byte[] BitField){
        this.BitField = BitField;
    }

}

class FullFile{
    private Socket soc;
    private boolean DownloadedFullFile;

    public Socket Get_Soc(){
        return soc;
    }
    public void Set_Soc(Socket soc){
        this.soc = soc;
    }
    public boolean isDownloadedFullFile(){
        return DownloadedFullFile;
    }
    public void set_DownloadedFullFile(boolean  DownloadedFullFile){
        this.DownloadedFullFile = DownloadedFullFile;
    }


}

class Message{
    private Socket soc;
    private byte[] msg;

    public Socket Get_Soc(){
        return soc;
    }
    public void Set_Soc(Socket soc){
        this.soc = soc;
    }
    public byte[] Get_msg(){
        return msg;
    }
    public void Set_msg(byte[] msg){
        this.msg = msg;
    }
}

public class peerProcess {
    public static String FileName;
    private long FileSize;
    private long PieceSize;
    private int TotalPieces;
    private int peerID;
    private int port;
    private boolean hasCompleteFile;
    public static ArrayList<Peer_info> Hosts = new ArrayList<Peer_info>();
    public static LinkedList<Message> msg_body = new LinkedList<Message>();
    public static ArrayList<Peer_info> PeerIDs;
    public static HashMap<Integer, piece> hash_map;
    public static ArrayList<FullFile> hasDownloadedFullFile = new ArrayList<FullFile>();
    public static ConcurrentHashMap<Integer, Peer_info> peerConnections;
    public static ArrayList<Integer> peerid = new ArrayList<>();
    public static ArrayList<Integer> peerIDs;


    public static void main(String args[]) throws InterruptedException {
        //create object of the peerProcess class
        peerProcess peer_process = new peerProcess();

        //create object of the Common.cfg class
        CommonCfg common_cfg = new CommonCfg();
        System.out.println("Reading Common config....");
        common_cfg.readCommonCfg();
        FileName = common_cfg.getFileName();
        peer_process.FileSize = common_cfg.getFileSize();
        peer_process.PieceSize = common_cfg.getPieceSize();

        /* We are given that the number of pieces will be the file size divided by the total number of pieces
           and this has to be an int
         */
        peer_process.TotalPieces = (int) Math.ceil((double)peer_process.FileSize/peer_process.PieceSize);
        PeerCfg peer_details = new PeerCfg(Integer.parseInt(args[0]));
        System.out.println("Reading peerinfo config....");

        peer_details.readPeerCfg();
        peerIDs = peer_details.getPeerIDs();
        peer_process.peerID = peer_details.getpeerID();
        peer_process.port = peer_details.getPort();
        peer_process.hasCompleteFile = peer_details.isHasFile();

        System.out.println(peerIDs.toString());
        System.out.println();

        bitfield.bitfield_set(peer_process.TotalPieces, peer_process.hasCompleteFile);

        logGenerator.begin_logging(peer_process.peerID);
        //System.out.println("Creating log files....");

        if(!peer_process.hasCompleteFile){
            hash_map = new HashMap<Integer, piece>();
            Server_Connect peer_listener = new Server_Connect(peer_process.port, peer_process.peerID,peer_process.TotalPieces, peer_process.hasCompleteFile, peer_process.FileSize, peer_process.PieceSize);
            peer_listener.start();
            Client_Connect client = new Client_Connect(peer_process.peerID, peer_process.TotalPieces, peer_process.hasCompleteFile, peer_process.FileSize, peer_process.PieceSize);
            client.start();

        }
        else if(peer_process.hasCompleteFile){
            FileParser read = new FileParser(peer_process.peerID, peer_process.PieceSize, FileName);
            hash_map = read.readFile();
            Server_Connect peer_listener = new Server_Connect(peer_process.port, peer_process.peerID, peer_process.TotalPieces, peer_process.hasCompleteFile, peer_process.FileSize, peer_process.PieceSize);
            peer_listener.start();

        }




    }


}


class CommonCfg {

    private int NumberOfPreferredNeighbors;
    private int UnchokingInterval;
    private int OptimisticUnchokingInterval;
    private String FileName;
    private long FileSize;
    private long PieceSize;

    public void readCommonCfg() {
        String common = "/Common.cfg";

        String filename = (new File(System.getProperty("user.dir")).getParent() + "/Common.cfg");

        // We use 'properties' to read the persistent properties from Common.cfg
        Properties Common_config_details = new Properties();

        try {
            Common_config_details.load(new BufferedInputStream(new FileInputStream(filename)));
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

class PeerCfg {
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
                //System.out.println(sp);
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

class logGenerator {
    public static int peerID;
    private static int num_pieces =0;
    private static BufferedWriter output_file;
    private static File file;
    public static boolean f_Flag = false;
    private DateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date time = new Date();
    private static Logger logger = Logger.getLogger(logGenerator.class.getName());



    public static void begin_logging(int peerid) {
        peerID = peerid;
        String curr_directory = (new File(System.getProperty("user.dir")).getParent() + "/log_peer_" + peerid + ".log");

        file = new File(curr_directory);

        try {
            output_file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

        } catch (FileNotFoundException e){

        }
    }

    public static void TCP_connection_initiate(int peerid) throws IOException {
        Date date = new Date();
        String h = date.toString();
        output_file.append(h);
        output_file.append(':');
        output_file.append(" Peer "+peerID);
        output_file.append(" makes a connection to Peer "+peerid);
        output_file.append('.');
        try{
            output_file.write(output_file.toString());
            output_file.newLine();
            output_file.flush();
        }
        catch(Exception e){
//            e.printStackTrace();
        }
    }

    public static void TCP_connection_made(int peerid) throws IOException {
        Date date = new Date();
        String h = date.toString();
        output_file.append(h);
        output_file.append(':');
        output_file.append(" Peer "+peerID);
        output_file.append(" is connected to Peer "+peerid);
        output_file.append('.');
        try{
            output_file.write(output_file.toString());
            output_file.newLine();
            output_file.flush();
        }
        catch(Exception e){
//            e.printStackTrace();
        }
    }

    public static void change_Preferred_neighbours(int[] neighbours) {
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "has preferred neighbours" + neighbours;
        logger.info(s);


    }

    public static void change_OptimisticallyUnchoked_Neighbour(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "has optimistically unchoked neighbour"+peerid+".";
        logger.info(s);
    }

    public static void made_Unchoked(int peerid){
        Date date = new Date();
        String h = date.toString();
        String s = h + peerID + "is unchoked by"+peerid+".";
        logger.info(s);
    }

    public static void haveReceived(int peerid) throws IOException {
        Date date = new Date();
        String h = date.toString();
        output_file.append(h);
        output_file.append(':');
        output_file.append(" Peer "+peerID);
        output_file.append(" received 'have' from "+peerid);
        output_file.append('.');
        try{
            output_file.write(output_file.toString());
            output_file.newLine();
            output_file.flush();
        }
        catch(Exception e){
//            e.printStackTrace();
        }
    }

    public static void interestedReceived(int peerid) throws IOException {
        Date date = new Date();
        String h = date.toString();
        output_file.append(h);
        output_file.append(':');
        output_file.append(" Peer "+peerID);
        output_file.append(" received 'interested' from Peer "+peerid);
        output_file.append('.');
        try{
            output_file.write(output_file.toString());
            output_file.newLine();
            output_file.flush();
        }
        catch(Exception e){
//            e.printStackTrace();
        }
    }

    public static void notInterestedReceived(int peerid) throws IOException {
        Date date = new Date();
        String h = date.toString();
        output_file.append(h);
        output_file.append(':');
        output_file.append(" Peer "+peerID);
        output_file.append(" received 'not interested' from Peer "+peerid);
        output_file.append('.');
        try{
            output_file.write(output_file.toString());
            output_file.newLine();
            output_file.flush();
        }
        catch(Exception e){
//            e.printStackTrace();
        }

    }

    public static void downloadedPiece(int peerid,int pieceIndex) throws IOException {
        num_pieces = num_pieces + 1;
        Date date = new Date();
        String h = date.toString();
        output_file.append(h);
        output_file.append(':');
        output_file.append(" Peer "+peerID);
        output_file.append(" has downloaded piece"+pieceIndex+ "from "+peerid);
        output_file.append('.');
        output_file.newLine();
        output_file.append("Now it has" +num_pieces + "pieces");
        output_file.append('.');
        try{
            output_file.write(output_file.toString());
            output_file.newLine();
            output_file.flush();
        }
        catch(Exception e){
//            e.printStackTrace();
        }
    }

    public static void downloadComplete() throws IOException {
        if(f_Flag!=false){
            Date date = new Date();
            String h = date.toString();
            output_file.append(h);
            output_file.append(':');
            output_file.append(" Peer "+peerID);
            output_file.append(" has downloaded full file");
            output_file.append('.');
            try{
                output_file.write(output_file.toString());
                output_file.newLine();
                output_file.flush();
            }
            catch(Exception e){
//            e.printStackTrace();
            }

        }


    }
    public static void end_logging() throws IOException {
        output_file.close();
    }


}

class FileParser {

    private int ID;
    private long PieceSize;
    private HashMap<Integer, piece> hash_map = new HashMap<Integer, piece>();
    private int pieceNum = 1;
    private String fName;

    public FileParser(int ID, long pieceSize, String fileName) {
        this.ID = ID;
        this.PieceSize = pieceSize;
        this.fName = fileName;
    }

    public HashMap<Integer, piece> readFile() {

        fName = (new File(System.getProperty("user.dir")).getParent() + "/peer_" + ID + "/" + fName);

        File file = new File(fName);

        try {

            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[(int) PieceSize];

            @SuppressWarnings("unused")

            int length;

            while ((length = input.read(buf)) > 0) {

                hash_map.put(pieceNum, new piece(pieceNum, buf));
                pieceNum++;
            }

            input.close();

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }

        return hash_map;
    }
}

class mergeFile {
    int peerID;
    int totalPieces;
    long fileSize;
    long pieceSize;

//    mergeFile(int peerID,int totalPieces,long fileSize,long pieceSize){
//        this.peerID = peerID;
//        this.fileSize = fileSize;
//        this.pieceSize = pieceSize;
//        this.totalPieces = totalPieces;
//    }

    public void assemblePieces(int totalpieces, int PeerID, long fileSize, long pieceSize){
        String dir = (new File(System.getProperty("user.dir")).getParent() + "/peer_" + peerID);
        File theDir = new File(dir);

        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch(SecurityException e) {
                System.err.println(e);
            }

        }
        String fname = (dir + "/" + peerProcess.FileName);
        File file = new File(fname);
        try{
            OutputStream out = new FileOutputStream(file);

            int i=1;
            while (i<totalPieces-1){
                int num = i;
                piece piece = peerProcess.hash_map.get(num);
                byte[] temp = new byte[4];
                //System.arraycopy(piece.msg_format, 0, temp, 0, 4);
                int size = ByteBuffer.wrap(temp).getInt();
                size = size - 4;

                createBuffer(out, piece, size);

                i++;

            }
            Integer num = totalPieces;
            piece p = peerProcess.hash_map.get(num);

            int size = (int) (fileSize % pieceSize);

            createBuffer(out, p, size);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public File createFile(File theDir){
//
//        try {
//            theDir.mkdir();
//        } catch(SecurityException e) {
//            System.err.println(e);
//        }
//        return theDir;
//
//    }

    private void createBuffer(OutputStream out, piece piece, int size) throws IOException {
        byte[] buffer = new byte[size];

        for (int j = 0, z = 9; j < buffer.length && z < piece.msg_format.length; j++, z++) {
            buffer[j] = piece.msg_format[z];
        }
        out.write(buffer);
    }
}


class ReceivingMessage extends Thread {
    private Socket soc;
    private int remote_host;
    private long Piece_size;

    public ReceivingMessage(Socket soc, long pieceSize)
    {
        this.soc = soc;
        this.Piece_size = pieceSize;
        ListIterator val = peerProcess.Hosts.listIterator();

        //int i=0;
        while(val.hasNext()) {
            Peer_info peer = (Peer_info) val.next();
            if(peer.get_soc()==soc){
                remote_host = peer.get_P_ID();
            }

        }
    }

    private byte[] receiveMessage() {

        byte[] msg = null;
        try {
            ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
            msg = (byte[]) in.readObject();
        }

        catch (Exception e) {
            System.exit(0);
        }
        return msg;
    }

    public byte[] updateBitField(byte[] field, int pieceIndex) {
        int i = (pieceIndex - 1) / 8;
        int k = 7 - ((pieceIndex - 1) % 8);
        field[i + 5] = (byte) (field[i + 5] | (1<<k));
        return field;
    }

    public void interested() throws IOException {
        System.out.println("Interested message received from " + remote_host);
        System.out.println();
        logGenerator.interestedReceived(remote_host);
    }

    public void uninterested() throws IOException {
        System.out.println("Not Interested message received from " + remote_host);
        System.out.println();
        logGenerator.notInterestedReceived(remote_host);
    }

    public void have(byte[] message)
    {
        byte[] t = new byte[4];

        int x = 5, i=0;

        while(i<t.length) {
            t[i] = message[x];
            x++;
            i++;
        }

        int pieceNum = ByteBuffer.wrap(t).getInt();

        ListIterator val = peerProcess.Hosts.listIterator();

        while(val.hasNext()){
            Peer_info peer = (Peer_info) val.next();

            if(peer.get_soc()==soc){
                byte[] field = peer.get_BitField();
                try {
                    synchronized (field) {
                        field = updateBitField(field, pieceNum);
                        peer.set_BitField(field);
                    }
                }catch (Exception e){
                    System.err.println(e);
                }

            }

        }


        System.out.println("Have message received from " + remote_host + " for piece " + pieceNum);
        System.out.println();
        //logGenerator.haveReceived(remotePeerID, pieceNum);
        try {
            logGenerator.haveReceived(remote_host);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void request(byte[] message)
    {
        byte[] t = new byte[4];

        int x = 5;
        int j = 0;
        while(j<t.length) {
            t[j] = message[x];
            x++;
            j++;
        }
        int pieceNum = ByteBuffer.wrap(t).getInt();
        Integer i = pieceNum;

        //send piece
        piece piece = peerProcess.hash_map.get(i);

        //debugging start
        System.out.println("piece " + pieceNum + " requested from " + remote_host);
        System.out.println();
        //debugging end

        synchronized (peerProcess.msg_body) {
            Message m  = new Message();
            m.Set_Soc(soc);
            m.Set_msg(piece.msg_format);
            peerProcess.msg_body.add(m);
        }
    }

    public void piece(byte[] message)
    {
        byte index[] = new byte[4];

        int x = 5;
        int j = 0;
        while(j<index.length) {
            index[j] = message[x];
            x++;
            j++;
        }
        int pieceIndex = ByteBuffer.wrap(index).getInt();
        Integer num = pieceIndex;
        byte[] piece = new byte[message.length - 9];
        for (int i = 0; i < piece.length; i++) {
            piece[i] = message[x];
            x++;
        }

        if(piece.length == Piece_size && !peerProcess.hash_map.containsKey(num)) {

            piece p1 = new piece(pieceIndex, piece);

            try {
                synchronized(peerProcess.hash_map) {
                    peerProcess.hash_map.put(num, p1);
                    sleep(30);
                }
            } catch (Exception e) {
                System.err.println(e);
            }


            System.out.println("piece " + pieceIndex + " received from " + remote_host);
            System.out.println();
            try {
                logGenerator.downloadedPiece(remote_host, pieceIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                synchronized(bitfield.msg_format) {
                    //bitfield f = new bitfield();
                    bitfield.newBitField(pieceIndex);
                    sleep(20);
                }
            }
            catch (InterruptedException e) {
                System.err.println(e);
            }

            //send have to all peers
             have Have = new have(pieceIndex);

            ListIterator<Peer_info> val = peerProcess.Hosts.listIterator();

            while(val.hasNext()) {
                Peer_info peer = val.next();
                //Socket s = peer.getSocket();

                synchronized (peerProcess.msg_body) {
                    Message m = new Message();
                    m.Set_Soc(peer.get_soc());
                    m.Set_msg(Have.msg_format);
                    peerProcess.msg_body.add(m);
                }
            }
        }
    }

    @Override
    public void run() {

        while (true) {

            byte[] message = receiveMessage();
            int type = message[4];

            if (type == 0)
            {
                //call choke function
            }

            else if (type == 1)
            {
                //unchoke
            }

            else if (type == 2)
            {
                //call interested
                try {
                    interested();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if(type==3)
            {
                // call not interested
                try {
                    uninterested();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if(type==4)
            {
                // call have function
                have(message);
            }

            else if (type==5)
            {
                // what are we doing with bitfield?
            }

            else if(type==6)
            {
                // request message type functionality
                request(message);
            }

            else if (type== 7)
            {
                // piece functionality
                piece(message);

            }

        }
    }



}


class send_message extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (peerProcess.msg_body.isEmpty()) {
                System.out.println("Empty message body");
            } else {
                synchronized (peerProcess.msg_body) {
                    Message m = peerProcess.msg_body.poll();
                    Socket soc = m.Get_Soc();
                    byte[] msg = m.Get_msg();
                    SendMsg(soc, msg);

                }
            }
        }

    }

    public void SendMsg(Socket soc, byte[] msg) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(soc.getOutputStream());
            synchronized (soc) {
                output.writeObject(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class RequestPiece extends Thread {

        private int peer_ID;
        private int myPeer_ID;
        private int totalpieces;
        private boolean haveAllPieces;
        private long fileSize;
        private long pieceSize;
        private int flag = 0;
        Socket soc;

        public RequestPiece(int peer_ID, int totalPieces, boolean haveAllPieces, long fileSize, long pieceSize) {
            this.peer_ID = peer_ID;
            this.totalpieces = totalPieces;
            this.haveAllPieces = haveAllPieces;
            this.fileSize = fileSize;
            this.pieceSize = pieceSize;
        }


        @Override
        public void run() {

            if(haveAllPieces == false) {

                Peer_info p = null;
                byte[] field;
                int getPiece;

                synchronized(peerProcess.Hosts) {

                    ListIterator<Peer_info> it = peerProcess.Hosts.listIterator();

                    while(it.hasNext()) {
                        p = (Peer_info) it.next();

                        if(p.get_P_ID() == peer_ID) {
                            myPeer_ID = p.get_current_P_ID();
                            soc = p.get_soc();
                            break;
                        }
                    }

                }

                while(true) {

                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }

                    boolean completeFile = hasCompleteFile();

                    if(completeFile) {

                        if(!logGenerator.f_Flag) {
                            logGenerator.f_Flag = true;

                            System.out.println("Download complete");
                            try {
                                logGenerator.downloadComplete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            mergeFile assemble = new mergeFile();
                            assemble.assemblePieces(totalpieces, myPeer_ID, fileSize, pieceSize);

                            try {
                                sleep(20);
                            } catch (InterruptedException e) {
                                System.err.println(e);
                            }
                        }

                        break;
                    }

                    else {

                        if(p.get_isInterested()) {
                            field = p.get_BitField();
                            getPiece = getPieceInfo(field, bitfield.msg_format);
                            if(getPiece == 0) {
                                p.set_is_Interested(false);
                                 uninterested not = new uninterested();
                                synchronized (peerProcess.msg_body) {
                                    Message m = new Message();
                                    m.Set_Soc(soc);
                                    m.Set_msg(not.msg_format);
                                    peerProcess.msg_body.add(m);
                                }
                                flag = 1;
                            }

                            else {
                                request req = new request(getPiece);
                                synchronized (peerProcess.msg_body) {
                                    Message m = new Message();
                                    m.Set_Soc(soc);
                                    m.Set_msg(req.msg_format);
                                    peerProcess.msg_body.add(m);
                                }
                            }
                        }

                        else {

                            field = p.get_BitField();
                            getPiece = getPieceInfo(field, bitfield.msg_format);

                            if(getPiece == 0) {
                                if(flag == 0) {
                                    uninterested not = new uninterested();

                                    synchronized (peerProcess.msg_body) {
                                        Message m = new Message();
                                        m.Set_Soc(soc);
                                        m.Set_msg(not.msg_format);
                                        peerProcess.msg_body.add(m);
                                    }
                                }
                            }

                            else {
                                p.set_is_Interested(true);
                                flag = 0;

                                interested interested = new interested();

                                synchronized (peerProcess.msg_body) {
                                    Message m = new Message();
                                    m.Set_Soc(soc);
                                    m.Set_msg(interested.msg_format);
                                    peerProcess.msg_body.add(m);
                                }
                                request req = new request(getPiece);
                                synchronized (peerProcess.msg_body) {
                                    Message m = new Message();
                                    m.Set_Soc(soc);
                                    m.Set_msg(req.msg_format);
                                    peerProcess.msg_body.add(m);
                                }
                            }

                        }
                    }
                }
            }

            byte[] downLoadedCompleteFile = new byte[5];

            for (int i = 0; i < downLoadedCompleteFile.length - 1; i++) {
                downLoadedCompleteFile[i] = 0;
            }
            downLoadedCompleteFile[4] = 8;

            sendHasDownloadedCompleteFile(downLoadedCompleteFile);

            while(true) {
                boolean check = checkAllPeerFileDownloaded();

                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }

                if(check == true && peerProcess.msg_body.isEmpty())
                    break;
            }

            if(!logGenerator.f_Flag)
            {
                logGenerator.f_Flag = true;

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }

                try {
                    logGenerator.end_logging();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.exit(0);

        }


        private boolean checkAllPeerFileDownloaded() {

            boolean flagAll = true;

            ListIterator<FullFile> it = peerProcess.hasDownloadedFullFile.listIterator();

            while(it.hasNext()) {

                FullFile peer = (FullFile) it.next();
                if(peer.isDownloadedFullFile()) {
                    flagAll = false;
                    break;
                }
            }

            return flagAll;
        }


        private void sendHasDownloadedCompleteFile(byte[] downLoadedCompleteFile) {

            ListIterator<FullFile> it = peerProcess.hasDownloadedFullFile.listIterator();

            while(it.hasNext()) {

                FullFile peer = (FullFile) it.next();

                synchronized (peerProcess.msg_body) {
                    Message m = new Message();
                    m.Set_Soc(peer.Get_Soc());
                    m.Set_msg(downLoadedCompleteFile);
                    peerProcess.msg_body.add(m);
                }
            }

        }


        private boolean hasCompleteFile() {

            int flagComp = 1;

            byte[] field = bitfield.msg_format;

            for (int i = 5; i < field.length - 1; i++) {
                if(field[i] != -1) {
                    flagComp = 0;
                    break;
                }
            }

            if(flagComp == 1) {

                int remaining = totalpieces % 8;
                int a = field[field.length - 1];
                String a1 = Integer.toBinaryString(a & 255 | 256).substring(1);
                char[] a2 = a1.toCharArray();
                int[] a3 = new int[8];

                for (int j = 0; j < a2.length; j++) {
                    a3[j] = a2[j] - 48;
                }

                for (int j = 0; j < remaining; j++) {
                    if(a3[j] == 0) {
                        flagComp = 0;
                        break;
                    }
                }
            }

            return (flagComp == 1);
        }




        private int getPieceInfo(byte[] field, byte[] bitfield) {

            int[] temp = new int[totalpieces];
            int k = 0;
            int total_missing_pieces = 0;
            int remaining  = totalpieces % 8;

            for (int i = 5; i < bitfield.length; i++) {

                int a = bitfield[i];
                int b = field[i];

                String a1 = Integer.toBinaryString(a & 255 | 256).substring(1);
                char[] a2 = a1.toCharArray();
                int[] a3 = new int[8];

                for (int j = 0; j < a2.length; j++) {
                    a3[j] = a2[j] - 48;
                }

                String b1 = Integer.toBinaryString(b & 255 | 256).substring(1);
                char[] b2 = b1.toCharArray();
                int[] b3 = new int[8];

                for (int j = 0; j < b2.length; j++) {
                    b3[j] = b2[j] - 48;
                }


                if(i < bitfield.length - 1) {

                    for (int j = 0; j < b3.length; j++) {
                        if(a3[j] == 0 && b3[j] == 1) {
                            temp[k] = 0;
                            k++;
                            total_missing_pieces++;
                        }

                        if(a3[j] == 0 && b3[j] == 0) {
                            temp[k] = 1;
                            k++;
                        }

                        if(a3[j] == 1) {
                            temp[k] = 1;
                            k++;
                        }
                    }
                }

                else {
                    for (int j = 0; j < remaining; j++) {
                        if(a3[j] == 0 && b3[j] == 1) {
                            temp[k] = 0;
                            k++;
                            total_missing_pieces++;
                        }

                        if(a3[j] == 0 && b3[j] == 0) {
                            temp[k] = 1;
                            k++;
                        }

                        if(a3[j] == 1) {
                            temp[k] = 1;
                            k++;
                        }
                    }
                }

            }


            try {
                sleep(1);
            } catch (InterruptedException e) {
                System.err.println(e);
            }


            if(total_missing_pieces == 0)
                return 0;

            int[] selectFrom = new int[total_missing_pieces];

            int x = 0;
            for (int l = 0; l < temp.length; l++) {
                if(temp[l] == 0) {
                    selectFrom[x] = l;
                    x++;
                }
            }

            int index = select_random_piece(total_missing_pieces);
            int piece = selectFrom[index];


            return (piece + 1);
        }

        private int select_random_piece(int total_missing_pieces) {

            Random rand = new Random();
            int randomNum = rand.nextInt(total_missing_pieces);

            return randomNum;

        }

}


class Client_Connect extends Thread {
    String Hostname;
    int port_no;
    int current_peerID;
    int totalPieces;
    boolean have_All_Pieces;
    long piece_Size;
    long file_Size;

    public Client_Connect(int PeerID, int totalPieces, boolean have_All_Pieces, long file_Size, long piece_Size) {
        current_peerID = PeerID;
        this.port_no = port_no;
        this.have_All_Pieces = have_All_Pieces;
        this.file_Size = file_Size;
        this.piece_Size = piece_Size;
    }

    private void Send_HandShake(Socket soc, byte[] Msg) {
        //function to send the handshake
        try {
            ObjectOutputStream Op = new ObjectOutputStream(soc.getOutputStream());
            Op.writeObject(Msg);
            System.out.println("handshake sent");

        } catch (IOException e) {
        }

    }

    byte[] Receive_HandShake(Socket soc) {
        //Function to receive the handshake
        byte[] Hand_Shake = null;
        try {
            ObjectInputStream Input_Stream = new ObjectInputStream(soc.getInputStream());
            Hand_Shake = (byte[]) Input_Stream.readObject();
            System.out.println("handshake received");
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
        return Hand_Shake;

    }

    public byte[] Get_BitField(Socket soc){
        byte[] BitField = null;
        try {
            ObjectInputStream input = new ObjectInputStream(soc.getInputStream());
            BitField = (byte[]) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return BitField;
    }

    public void Set_BitField(Socket soc) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(soc.getOutputStream());
        output.writeObject(bitfield.msg_format);
    }




    @Override
    public void run() {

        //array for header information

        //byte[] buff = new byte[32];
        PeerCfg peer_connect = new PeerCfg(current_peerID);
        ArrayList<String[]> client_side_connection;
        client_side_connection = peer_connect.getPeerDetails();

        ListIterator<String[]> obj = client_side_connection.listIterator();
        System.out.println("connection sent! ");
        while (obj.hasNext()) {

            String[] Info = obj.next();

            Hostname = Info[1];
            port_no = Integer.parseInt(Info[2]);

            try {
                //create a new socket
                Socket soc = new Socket(Hostname, port_no);
                //create handshake msg
                HandShake_Message Msg_send = new HandShake_Message(current_peerID);
                System.out.println("socket created");
                //HandShake_Message Msg_send = new HandShake_Message(current_peerID);
                //send handshake message
                Send_HandShake(soc, Msg_send.Msg);

                //receive handshake msg
                byte[] get_handshake = Receive_HandShake(soc);
                byte[] header_info = new byte[28];
                int i = 0;
                //split peerid and other header information
                while (i < 28) {
                    header_info[i] = get_handshake[i];
                    i++;
                }
                String head = new String(header_info);

                int k = 0;
                byte[] peer_ID_t = new byte[4];
                i = 28;
                while (i < 32) {
                    peer_ID_t[i] = get_handshake[i];
                    k++;
                    i++;
                }
                String new_s = new String(peer_ID_t);
                int peer_ID = Integer.parseInt(new_s);
                if (head == "P2PFILESHARINGPROJ0000000000") {
                    boolean checker = false;
                    ListIterator<Integer> it = peerProcess.peerIDs.listIterator();
                    while (it.hasNext()) {
                        int avail_int = it.next().intValue();
                        if (avail_int == current_peerID)
                            break;
                        else if (avail_int != current_peerID) {
                            if (avail_int == peer_ID) {
                                checker = true;
                                break;
                            }

                        }
                    }
                    if (checker == true) {
                        Peer_info peer = new Peer_info();
                        peer.set_current_P_ID(current_peerID);
                        peer.set_soc(soc);
                        peer.set_P_ID(Integer.parseInt(Info[0]));
                        byte[] bit_f = Get_BitField(soc);
                        peer.set_BitField(bit_f);

                        Set_BitField(soc);
                        peer.set_is_Interested(false);
                        synchronized (peerProcess.Hosts) {
                            peerProcess.Hosts.add(peer);
                            try {
                                sleep(2);
                            } catch (InterruptedException e) {
                                System.err.println(getStackTrace());

                            }
                        }
                        FullFile complete_file = new FullFile();
                        complete_file.Set_Soc(soc);
                        complete_file.set_DownloadedFullFile(false);
                        peerProcess.hasDownloadedFullFile.add(complete_file);
                        System.out.println("Connection req sent to " + Integer.parseInt(Info[0]));
                        System.out.println();
                        logGenerator.TCP_connection_initiate(Integer.parseInt(Info[0]));
                        send_message snd_msg = new send_message();
                        snd_msg.start();
                        RequestPiece req_msg = new RequestPiece(Integer.parseInt(Info[0]), totalPieces, have_All_Pieces, file_Size, piece_Size);
                        req_msg.start();
                        ReceivingMessage rcv_msg = new ReceivingMessage(soc, piece_Size);
                        rcv_msg.start();
                    }
                    else System.out.println("wrong peer connection");
                }


            } catch (IOException e) {
            }


        }
    }
}
class Server_Connect extends Thread {
        //Socket soc = null;
        int current_peerID;
        int totalPieces;
        boolean have_all_pieces;
        int port_no;
        int listener_port;
        boolean allPieces;
        long fileSize;
        long peiceSize;

public Server_Connect(int listener_port, int peerID,  int totalPieces,boolean allPieces, long fileSize, long peiceSize){
        current_peerID = peerID;
        this.totalPieces = totalPieces;
        this.allPieces = allPieces;
        //this.have_all_pieces = have_all_pieces;
        this.fileSize = fileSize;
        this.peiceSize = peiceSize;
        this.listener_port = listener_port;

        }

        void Send_HandShake_server(Socket soc,byte[] handShake){
        try{
        //soc_server = new ServerSocket(listener_port);

        ObjectOutputStream Op = new ObjectOutputStream(soc.getOutputStream());
        //Op.flush();
        Op.writeObject(handShake);
        //Op.flush();

        }catch(IOException e){}
        }

        byte[] Receive_handshake_server(Socket soc){
        byte[] rec_handshake = null;
        System.out.println("receive the handshake");

        try{

        //soc = soc_server.accept();
        ObjectInputStream IP = new ObjectInputStream(soc.getInputStream());
        rec_handshake = (byte[]) IP.readObject();
        }catch(IOException | ClassNotFoundException e){}
        return rec_handshake;

        }

@Override
public void run() {
        try {

        ServerSocket soc_server = new ServerSocket(listener_port);

        System.out.println("Server is up and running!");

        while (true) {
        logGenerator.TCP_connection_initiate(current_peerID);
        //System.out.println("i am inside while");
        Socket soc;
        soc = soc_server.accept();
        System.out.println("socket created");



        byte[] get_HandShake_server = Receive_handshake_server(soc);


        HandShake_Message server_Msg = new HandShake_Message(current_peerID);
        //System.out.println("yayy");
        //System.out.println(current_peerID);
        Send_HandShake_server(soc,server_Msg.Msg);

        byte[] header_info = new byte[28];
        byte[] peer_ID_temp = new byte[4];
        method(get_HandShake_server, header_info, peer_ID_temp);
        String s = new String(peer_ID_temp);
        int peerID;
        peerID = Integer.parseInt(s);

        String head = new String(header_info);
        System.out.println(head);

        if(head=="P2PFILESHARINGPROJ"){
        System.out.println("header matches");
        int flg = 0;
        int length = peerProcess.PeerIDs.size();
        int j=0;
        while(j<=peerProcess.peerid.size()){
        if(peerProcess.peerid.get(j)!=current_peerID){
        j++;
        //continue;
        }
        else{
        break;
        }
        }

        while(j<=peerProcess.peerid.size()){
        if(peerProcess.peerid.get(j)==peerID){
        flg = 1;
        }
        j++;
        }
//                    for(int j: peerProcess.peerid){
//                        if(j!=current_peerID){
//                            continue;
//                        }
//                        else {
//                            break;
//                        }
//                    }
//
//                    for(int i:peerProcess.peerid){
//                        if(i==peerID){
//                            flg = 1;
//                        }
//                    }
        if(flg==0){
        System.out.println("error");
        }

        else if(flg==1){
        Peer_info peer = new Peer_info();
        peer.set_P_ID(peerID);
        peer.set_soc(soc);
        peer.set_is_Interested(false);
        byte[] receiveBitfield = receiveBitfield(soc);
        peer.set_BitField(receiveBitfield);
        peerProcess.PeerIDs.add(peer);
        peerProcess.peerConnections.put(peerID,peer);
        sendBitfield(soc);

        FullFile file = new FullFile();
        file.Set_Soc(soc);
        file.set_DownloadedFullFile(false);
        peerProcess.hasDownloadedFullFile.add(file);

        logGenerator.TCP_connection_made(peerID);

        send_message sndmsg = new send_message();
        sndmsg.start();

        ReceivingMessage rec  = new ReceivingMessage(soc,peiceSize);
        rec.start();

        RequestPiece req_msg = new RequestPiece(peerID, totalPieces, allPieces, fileSize, peiceSize);
        req_msg.start();


        }

        }




        }


        }catch (IOException e) {
        }

        }

static void method(byte[] get_HandShake_server, byte[] header_info, byte[] peer_ID_temp) {
        int m = 0;


        for (int i = 0; i < 32; i++) {
        if (i >= 28) {

        peer_ID_temp[m] = get_HandShake_server[i];
        m++;
        } else {
        header_info[i] = get_HandShake_server[i];
        }
        }
        //String s = new String(peer_ID_temp);
        }

        void sendBitfield(Socket soc){
        try{
        ObjectOutputStream OP = new ObjectOutputStream(soc.getOutputStream());
        OP.writeObject(bitfield.msg_format);
        } catch (IOException e) {
        e.printStackTrace();
        }
        }

        byte[] receiveBitfield(Socket soc){
        byte[] bitfield = null;
        try {
        ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
        bitfield = (byte[]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
        System.err.println(e);
        }

        return bitfield;
        }

}

class HandShake_Message {
    public byte[] Msg = new byte[32];
    String Header = "P2PFILESHARINGPROJ";
    String Zero_Bits = "0000000000";
    int Peer_ID;

    HandShake_Message(int Peer_ID){
        this.Peer_ID = Peer_ID;
        String Header_Info = Header+Zero_Bits+Integer.toString(Peer_ID);
        Msg = Header_Info.getBytes();


    }

}


//Messages

class bitfield {
    private static int total_pieces;
    private static boolean hasFullFile = false;
    //int data_len = data.length;
    public static byte[] msg_format; // bitfield format has 4 bytes of msg_length, 1 byte of msg_type and an unknown bytes of the payload
    private static byte[] msg_length = new byte[4]; // first part of the format
    private static byte msg_type = 5; // second part of the format
    public static byte[] msg_payload; // third part of the format


    public static void bitfield_set(int tp, boolean hasfile){
        hasFullFile = hasfile;
        total_pieces = tp;
        int msg_payload_length = (int) Math.ceil((double)total_pieces/8);
        int remaining_pieces = total_pieces % 8;
        msg_length = ByteBuffer.allocate(4).putInt(msg_payload_length).array();
        msg_payload = new byte[msg_payload_length];
        msg_format = new byte[msg_payload_length + 5];
        int i=0;
        for(; i<msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // adding the length to the format
            //System.out.println(msg_format[i]);
        }
        msg_format[i] = msg_type;
        if(hasFullFile ==false) {
            for (int fill = 0; fill < msg_payload.length; fill++) {
                i++;
                msg_format[i] = 0;


            }
        }
        else{
            for(int j=0; j< msg_payload.length - 1; j++){
                i++;
                for(int k = 0;k <8; k++){
                    msg_format[i] = (byte) (msg_format[i] | (1<<k));
                }
            }
            i++;
            for(int j = 0; j< remaining_pieces; j++)
                msg_format[i] = (byte) (msg_format[i]| (1<<(7-j)));

        }

    }
    public static void newBitField(int index){
        int a = (index -1)/8;
        int b = 7 - ((index - 1)%8);
        msg_format[a+5] = (byte) (msg_format[a+5]|(1<<b));
    }

}

class choke {

    public byte[] msg_format = new byte[5]; //choke has 5 bytes - 4 for msg_len and 1 for the type of msg
    private byte[] msg_length = new byte[4]; // first part of the message is the msg_length
    private byte msg_type = 0; //second part of the msg

    public choke(){
        msg_length = ByteBuffer.allocate(4).putInt(0).array();
        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i];// adding the msg_length part to choke
            //System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type;// adding the type part to choke
        //System.out.println(msg_format[i]);

    }


}

class have {
    public byte[] msg_format = new byte[9]; // have format has 4 bytes of msg_length, 1 byte of msg_type and 4 bytes of the piece_index as payload
    private byte[] msg_length = new byte[4]; // first part of the msg_format
    private byte[] msg_payload = new byte[4]; // third and last part of the format
    private byte msg_type = 4; // second part of the format
    public have(int index){
        msg_length = ByteBuffer.allocate(4).putInt(4).array();
        msg_payload = ByteBuffer.allocate(4).putInt(index).array();
        int i=0;
        for(i=0; i< msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // store the msg_length in the format
            //System.out.println(msg_format[i]);
        }
        msg_format[i]=msg_type; // store the type in the format
        //System.out.println(msg_format[i]);

        for (int j=0; j< msg_payload.length; j++)
        {
            i++;
            msg_format[i] = msg_payload[j]; // store the payload in the format
            //System.out.println(msg_format[i]);
        }
    }
}

class interested {
    public byte[] msg_format = new byte[5]; //interested has 5 bytes - 4 for msg_len and 1 for the type of msg
    private byte[] msg_length = new byte[4];  // first part of the message is the msg_length
    private byte msg_type = 2; //second part of the msg is the msg_type

    public interested(){
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i];// adding the msg_length part to the format
            //System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type; //adding the msg_type to the format
        //System.out.println(msg_format[i]);
    }
}

class piece {

    public byte[] msg_format;
    private byte[] msg_length = new byte[4]; // first part of the msg format
    private byte[] pieceIndex = new byte[4]; //third part of the msg format
    // byte[] data = data;// fourth part of the msg format
    byte msg_type = 7; // second part of the msg format
    private byte[] message;
    public piece(int index, byte[] data){

        message = data;
        int data_len = data.length;
        pieceIndex = ByteBuffer.allocate(4).putInt(index).array();
        msg_length = ByteBuffer.allocate(4).putInt(4 + data_len).array();
        msg_format = new byte[9 + data_len]; // piece msg format ha 4 bytes of msg_length, 1 byte of msg_type
        // 4 byte of piece index and a variable data

        int i = 0;
        //System.out.println(" Message_length.length is" + msg_length.length);
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i]; // storing second part in format
            //System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type; // storing first part in format
        //System.out.println(msg_format[i]);
        //System.out.println("Pieceindex.length = "+ pieceIndex.length);
        for (int j = 0; j < pieceIndex.length; j++)
        {
            i++;
            msg_format[i] = pieceIndex[j]; // storing third part in format
            //System.out.println(msg_format[i]);
        }
        //System.out.println("Actual message.length = "+ data.length);
        for (int j = 0; j < message.length; j++)
        {
            i++;
            msg_format[i] = message[j]; // storing fourth part in format
            // System.out.println(msg_format[i]);
        }



    }
}

class request {
    public byte[] msg_format = new byte[9]; // request msg format has first 4 bytes as msg_length next byte as type and last 4 bytes as piece index (stored as payload)
    private byte[] msg_length = new byte[4]; // first part of the request msg format
    private byte[] msg_payload = new byte[4]; // third part of the msg format
    private byte msg_type = 6; // second part of the msg format
    public request(int index){
        msg_length = ByteBuffer.allocate(4).putInt(4).array();
        msg_payload = ByteBuffer.allocate(4).putInt(index).array();
        int i=0;
        for(i=0; i<msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // storing the first part in format
            //System.out.println(msg_format[i]);
        }
        msg_format[i] = msg_type; // storing the second part in format
        //System.out.println(msg_format[i]);

        for(int j=0; j<msg_payload.length; j++)
        {
            i++;
            msg_format[i] = msg_payload[j]; // storing the third part in format
            //System.out.println(msg_format[i]);
        }


    }

}

class unchoke {

    public byte[] msg_format = new byte[5]; //unchoke has 5 bytes - 4 for msg_len and 1 for the type of msg
    private byte[] msg_length = new byte[4];  // first part of the message is the msg_length
    private byte msg_type = 1; //second part of the msg
    public unchoke(){
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i]; // adding the msg_length part to unchoke
            //System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type; // adding the type part to unchoke
        //System.out.println(msg_format[i]);

    }

}

class uninterested {
    public byte[] msg_format = new byte[5]; //uninterested has 5 bytes - 4 for msg_len and 1 for the type of msg
    private byte[] msg_length = new byte[4]; // first part of the message is the msg_length
    private byte msg_type = 2; //second part of the msg is the msg_type
    public uninterested(){
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i]; // adding the msg_length part to the format
            //System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type;  //adding the msg_type to the format
        //System.out.println(msg_format[i]);
    }
}











