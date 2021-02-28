package Common;
import Peer.peerProcess.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

    public class Logs {
        //SimpleDateFormat date_format = new SimpleDateFormat(" dd/MMM/yyyy HH:mm:ss");
        private DateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        private Date time = new Date();
        private BufferedWriter writer;

        public Logs(BufferedWriter writer){
            timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            this.writer = writer;
        }
        public void connectionTo(int id1,int id2){
            time = new Date();
            StringBuffer log = new StringBuffer();
            log.append(timeFormat.format(time));
            log.append(':');
            log.append(" Peer ");
            log.append(id1);
            log.append(" makes a connection to Peer ");
            log.append(id2);
            log.append('.');
            try{
                writer.write(log.toString());
                writer.newLine();
                writer.flush();
            }
            catch(Exception e){
//            e.printStackTrace();
            }
        }



    }

}
