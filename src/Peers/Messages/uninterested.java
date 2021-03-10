package Peers.Messages;

import java.nio.ByteBuffer;
/*uninterested is used by peers to get the uninterested message format
it returns a byte array of format */

public class uninterested {
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
