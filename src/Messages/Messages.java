import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.nio.ByteBuffer;
public class Messages
{
    //choke function is used by a peer to set the format of a choke message
    // it returns a byte array of the format
    public byte[] choke()
    {
        byte[] msg_format = new byte[5]; //choke has 5 bytes - 4 for msg_len and 1 for the type of msg
        byte[] msg_length = new byte[4]; // first part of the message is the msg_length
        byte msg_type = 0; //second part of the msg
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i];// adding the msg_length part to choke
            System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type;// adding the type part to choke
        System.out.println(msg_format[i]);
        return msg_format; //returning byte array of choke
    }


    //unchoke function is used by peers to get the unchoke message format
    //it returns a byte array of format
    public byte[] unchoke()
    {
        byte[] msg_format = new byte[5]; //unchoke has 5 bytes - 4 for msg_len and 1 for the type of msg
        byte[] msg_length = new byte[4];  // first part of the message is the msg_length
        byte msg_type = 1; //second part of the msg
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i]; // adding the msg_length part to unchoke
            System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type; // adding the type part to unchoke
        System.out.println(msg_format[i]);
        return msg_format; //returning byte array of choke
    }

    //interested function is used by peers to get the interested message format
    //it returns a byte array of format
    public byte[] interested()
    {
        byte[] msg_format = new byte[5]; //interested has 5 bytes - 4 for msg_len and 1 for the type of msg
        byte[] msg_length = new byte[4];  // first part of the message is the msg_length
        byte msg_type = 2; //second part of the msg is the msg_type
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i];// adding the msg_length part to the format
            System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type; //adding the msg_type to the format
        System.out.println(msg_format[i]);
        return msg_format;
    }

    //uninterested function is used by peers to get the uninterested message format
    //it returns a byte array of format
    public byte[] uninterested()
    {
        byte[] msg_format = new byte[5]; //uninterested has 5 bytes - 4 for msg_len and 1 for the type of msg
        byte[] msg_length = new byte[4]; // first part of the message is the msg_length
        byte msg_type = 2; //second part of the msg is the msg_type
        msg_length = ByteBuffer.allocate(4).putInt(0).array();

        int i = 0;
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i]; // adding the msg_length part to the format
            System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type;  //adding the msg_type to the format
        System.out.println(msg_format[i]);
        return msg_format;
    }

    //have function is used by peers to get the have message format
    //it returns a byte array of format
    public byte[] have(int index)
    {
        byte[] msg_format = new byte[9]; // have format has 4 bytes of msg_length, 1 byte of msg_type and 4 bytes of the piece_index as payload
        byte[] msg_length = new byte[4]; // first part of the msg_format
        byte[] msg_payload = new byte[4]; // third and last part of the format
        byte msg_type = 4; // second part of the format

        msg_length = ByteBuffer.allocate(4).putInt(4).array();
        msg_payload = ByteBuffer.allocate(4).putInt(index).array();
        int i=0;
        for(i=0; i< msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // store the msg_length in the format
            System.out.println(msg_format[i]);
        }
        msg_format[i]=msg_type; // store the type in the format
        System.out.println(msg_format[i]);

        for (int j=0; j< msg_payload.length; j++)
        {
            i++;
            msg_format[i] = msg_payload[j]; // store the payload in the format
            System.out.println(msg_format[i]);
        }
        return msg_format; // returning the format byte array
    }

    //bitfield function is used by peers to get the bitfield message format
    //it returns a byte array of format
    public byte[] bitfield(int index, byte @NotNull [] data )
    {
        int data_len = data.length;
        byte[] msg_length = new byte[4]; // first part of the format
        byte msg_type = 5; // second part of the format
        byte[] msg_payload = new byte[data_len]; // third part of the format
        byte[] msg_format = new byte[9 + data_len]; // bitfield format has 4 bytes of msg_length, 1 byte of msg_type and an unknown bytes of the payload
        msg_length = ByteBuffer.allocate(4).putInt(data_len).array();

        int i=0;
        for(i=0; i<msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // adding the length to the format
            System.out.println(msg_format[i]);
        }
        msg_format[i] = msg_type; // adding the msg type to the format
        System.out.println(msg_format[i]);

        for (int j = 0; j < data.length; j++)
        {
            i++;
            msg_format[i] = msg_payload[j]; // adding the msg payload to the format
            System.out.println(msg_format[i]);
        }

        return msg_format; // returning the byte array of format

    }

    //request function is used by peers to get the request message format
    //it returns a byte array of format
    public byte[] request(int index)
    {
        byte[] msg_format = new byte[9]; // request msg format has first 4 bytes as msg_length next byte as type and last 4 bytes as piece index (stored as payload)
        byte[] msg_length = new byte[4]; // first part of the request msg format
        byte[] msg_payload = new byte[4]; // third part of the msg format
        byte msg_type = 6; // second part of the msg format

        msg_length = ByteBuffer.allocate(4).putInt(4).array();
        msg_payload = ByteBuffer.allocate(4).putInt(index).array();
        int i=0;
        for(i=0; i<msg_length.length; i++)
        {
            msg_format[i]=msg_length[i]; // storing the first part in format
            System.out.println(msg_format[i]);
        }
        msg_format[i] = msg_type; // storing the second part in format
        System.out.println(msg_format[i]);

        for(int j=0; j<msg_payload.length; j++)
        {
            i++;
            msg_format[i] = msg_payload[j]; // storing the third part in format
            System.out.println(msg_format[i]);
        }
        return msg_format; // returning the byte array of msg format
    }


    public byte[] piece(int index, byte[] data)
    {
        byte[] msg_length = new byte[4]; // first part of the msg format
        byte[] pieceIndex = new byte[4]; //third part of the msg format
        // byte[] data = data;// fourth part of the msg format
        byte msg_type = 7; // second part of the msg format

        int data_len = data.length;
        pieceIndex = ByteBuffer.allocate(4).putInt(index).array();
        msg_length = ByteBuffer.allocate(4).putInt(4 + data_len).array();
        byte[] msg_format = new byte[9 + data_len]; // piece msg format ha 4 bytes of msg_length, 1 byte of msg_type
        // 4 byte of piece index and a variable data

        int i = 0;
        System.out.println(" Message_length.length is" + msg_length.length);
        for (i = 0; i < msg_length.length; i++)
        {
            msg_format[i] = msg_length[i]; // storing second part in format
            System.out.println(msg_format[i]);
        }

        msg_format[i] = msg_type; // storing first part in format
        System.out.println(msg_format[i]);
        System.out.println("Pieceindex.length = "+ pieceIndex.length);
        for (int j = 0; j < pieceIndex.length; j++)
        {
            i++;
            msg_format[i] = pieceIndex[j]; // storing third part in format
            System.out.println(msg_format[i]);
        }
        System.out.println("Actual message.length = "+ data.length);
        for (int j = 0; j < data.length; j++)
        {
            i++;
            msg_format[i] = data[j]; // storing fourth part in format
            System.out.println(msg_format[i]);
        }

        return msg_format; // returning the byte array of msg format
    }

    public static void main(String[] args)
    {
        Messages m1 = new Messages();
        byte[] data = new byte[]{1, 0, 1, 0, 1, 1, 0, 0};

        byte[] a = m1.choke();
        a = m1.unchoke();
        a = m1.interested();
        a = m1.uninterested();
        a = m1.have(5);
        a = m1.bitfield(5,data);
        a = m1.request(5);
        a = m1.piece(5, data);

    }

}
