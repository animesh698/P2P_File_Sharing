package Common;

public class HandShake_Message {
    byte[] Msg = new byte[32];
    String Header = "P2PFILESHARINGPROJ";
    String Zero_Bits = "0000000000";
    int Peer_ID;

    HandShake_Message(int Peer_ID){
        this.Peer_ID = Peer_ID;
        String Header_Info = Header+Zero_Bits+Integer.toString(Peer_ID);
        Msg = Header_Info.getBytes();


    }

}
