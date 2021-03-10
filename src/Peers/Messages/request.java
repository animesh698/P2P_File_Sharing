package Peers.Messages;

/* request function is used by peers to get the request message format
it returns a byte array of format */

import java.nio.ByteBuffer;

public class request {
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
