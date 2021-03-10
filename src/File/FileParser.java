package File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import Peers.Messages.piece;

public class FileParser {

    private int ID;
    private long PieceSize;
    private HashMap<Integer, piece> hash_map = new HashMap<Integer, piece>();
    private int pieceNum = 1;
    private String fName;

    public FileParser(int ID, long pieceSize, String fileName) {
        this.ID = ID;
        this.PieceSize = pieceSize;
        this.fName = fileName;
    }

    public HashMap<Integer, piece> readFile() {

        fName = (new File(System.getProperty("user.dir")).getParent() + "/peer_" + ID + "/" + fName);

        File file = new File(fName);

        try {

            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[(int)PieceSize];

            @SuppressWarnings("unused")
            int length;

            while((length = input.read(buf)) > 0) {

                hash_map.put(pieceNum, new piece(pieceNum, buf));
                pieceNum++;
            }

            input.close();

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }

        return hash_map;
    }

}
