package Peers.Messages;

import java.nio.ByteBuffer;

public class piece {

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
