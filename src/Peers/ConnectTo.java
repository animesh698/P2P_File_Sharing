import java.net.*;
import java.io.*;


import java.util.ArrayList;
import java.util.ListIterator;

public class ConnectTo extends Thread {
    String Hostname;
    int port_no;
    int PeerID;
    int totalPieces;
    boolean have_All_Pieces;
    long piece_Size;
    long file_Size;

    ConnectTo(int PeerID,int port_no,boolean have_All_Pieces,long file_Size,long piece_Size){
        PeerID = PeerID;
        this.port_no = port_no;
        this.have_All_Pieces = have_All_Pieces;
        this.file_Size = file_Size;
        this.piece_Size = piece_Size;
    }
    @Override
    public void run(){

        //array for header information

        byte[] buff = new byte[32];
        PeerCfg peer_connect = new PeerCfg(PeerID);
        ArrayList<String[]> client_side_connection ;
        client_side_connection = peer_connect.getPeerDetails();

        ListIterator<String[]> obj = client_side_connection.listIterator();
        while(obj.hasNext()){

            String[] Info = obj.next();

            Hostname = Info[1];
            port_no = Integer.parseInt(Info[2]);

            try{
                //create a new socket
                Socket soc = new Socket(Hostname,port_no);
                //create handshake msg
                HandShake_Message Msg_send = new HandShake_Message(PeerID);
                //send handshake message
                Send_HandShake(soc,Msg_send.Msg);

                //receive handshake msg
                byte[] get_handshake = Receive_HandShake(soc);
                byte[] peer_ID = new byte[4];
                byte[] header_info = new byte[28];
                int m=0;
                //split peerid and other header information
                for(int i=0;i<31;i++){
                    if (i >= 28){
                        for(int j = 28;j<31;j++){
                            peer_ID[m] = get_handshake[j];
                            m++;
                        }

                    }
                    else{
                        header_info[i] = get_handshake[i];
                    }

                    String toString = new String(peer_ID);
                    int p_ID = Integer.parseInt(toString);




                }

            }catch(IOException e){}



        }







    }

    private void Send_HandShake(Socket soc, byte[] Msg){
        //function to send the handshake
        try{
            ObjectOutputStream Op = new ObjectOutputStream(soc.getOutputStream());
            Op.writeObject(Msg);

        }catch(IOException e){}

    }

    byte[] Receive_HandShake(Socket soc){
        //Function to receive the handshake
        byte[] Hand_Shake = null;
        try{
            ObjectInputStream Input_Stream = new ObjectInputStream(soc.getInputStream());
            Hand_Shake = (byte[]) Input_Stream.readObject();
        }catch(IOException e){}
        catch (ClassNotFoundException e){}return Hand_Shake;

    }




}
