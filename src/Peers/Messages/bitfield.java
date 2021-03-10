package Peers.Messages;

import java.nio.ByteBuffer;

public class bitfield {
    private static int total_pieces;
    //int data_len = data.length;
    public static byte[] msg_format; // bitfield format has 4 bytes of msg_length, 1 byte of msg_type and an unknown bytes of the payload
    private static byte[] msg_length = new byte[4]; // first part of the format
    private static byte msg_type = 5; // second part of the format
    private static byte[] msg_payload; // third part of the format


    public static void bitfield_set(int tp, byte[] data){
        total_pieces = tp;
        int msg_payload_length = (int) Math.ceil((double)total_pieces/8);
        msg_length = ByteBuffer.allocate(4).putInt(msg_payload.length).array();
        int remaining_pieces = total_pieces % 8;
        msg_payload = new byte[msg_payload_length];
        msg_format = new byte[msg_payload_length + 5];
        int i=0;
        for(; i<msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // adding the length to the format
            //System.out.println(msg_format[i]);
        }
        msg_format[i] = msg_type;






    }
}
