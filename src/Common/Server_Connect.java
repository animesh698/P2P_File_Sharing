package Common;

import java.net.*;
import java.io.*;

public class Server_Connect extends Thread {
    Socket soc = null;
    ServerSocket soc_server = null;
    DataInputStream Input = null;
    int current_peerID;
    int have_all_pieces;
    int port_no;
    int listener_port;

    public Server_Connect(int peerID, int listener_port){
        current_peerID = peerID;
        //this.port_no = port_no;
        this.listener_port = listener_port;

    }

    void Send_HandShake_server(Socket soc, byte[] handShake){
        try{
            ObjectOutputStream Op = new ObjectOutputStream(soc.getOutputStream());
            Op.writeObject(handShake);

        }catch(IOException e){}
    }

    byte[] Receive_handshake_server(Socket soc){
        byte[] rec_handshake = null;
        try{
            ObjectInputStream IP = new ObjectInputStream(soc.getInputStream());
            rec_handshake = (byte[]) IP.readObject();
        }catch(IOException e){}catch(ClassNotFoundException e){}return rec_handshake;

    }

    @Override
    public void run() {
        try {

            soc_server = new ServerSocket(listener_port);

            System.out.println("Server is up and running");
            while (true) {
                soc = soc_server.accept();

                byte[] get_HandShake_server = Receive_handshake_server(soc);


                HandShake_Message server_Msg = new HandShake_Message(current_peerID);
                Send_HandShake_server(soc, server_Msg.Msg);

                byte[] header_info = new byte[28];
                byte[] peer_ID = new byte[4];
                int m = 0;

                for (int i = 0; i < 31; i++) {
                    if (i >= 28) {

                        peer_ID[m] = get_HandShake_server[i];
                        m++;
                    } else {
                        header_info[i] = get_HandShake_server[i];
                    }
                }


            }


        }catch (IOException e) {
        }

    }






}


