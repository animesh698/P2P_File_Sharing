package Peers.Messages;

import java.nio.ByteBuffer;

/* have is used by peers to get the have message format
 it returns a byte array of format */
public class have {
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
