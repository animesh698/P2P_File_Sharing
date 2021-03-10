package Peers.Messages;

import java.nio.ByteBuffer;
/*choke is used by a peer to set the format of a choke message
     it returns a byte array of the format */

public class choke {

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
