package com.example.player;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/31 14:45
 * Modified By:
 * Description:
 */
public class RTSPProtocal {
    public static byte[] encodeOption(String address, String VERSION, int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("OPTIONS ");
        sb.append(address.substring(0, address.lastIndexOf("/")));
        sb.append(VERSION);
        sb.append("Cseq: ");
        sb.append(seq);
        sb.append("\r\n");
        sb.append("\r\n");
        System.out.println(sb.toString());
        //send(sb.toString().getBytes());
        return sb.toString().getBytes();
    }

    public static byte[] encodeDescribe(String address, String VERSION, int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("DESCRIBE ");
        sb.append(address);
        sb.append(VERSION);
        sb.append("Cseq: ");
        sb.append(seq);
        sb.append("\r\n");
        sb.append("\r\n");
        System.out.println(sb.toString());
        //send(sb.toString().getBytes());
        return sb.toString().getBytes();
    }

    public static byte[] encodeSetup(String address, String VERSION, String sessionid,
                                     int portOdd, int portEven, int seq, String trackInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("SETUP ");
        sb.append(address);
        sb.append("/");
        sb.append(trackInfo);
        sb.append(VERSION);
        sb.append("Cseq: ");
        sb.append(seq++);
        sb.append("\r\n");
        //"50002-50003"
        sb.append("Transport: RTP/AVP;UNICAST;client_port="+portEven+"-"+portOdd+";mode=play\r\n");
        sb.append("\r\n");
        System.out.println(sb.toString());
        System.out.println(sb.toString());
        //send(sb.toString().getBytes());
        return sb.toString().getBytes();
    }

    public static byte[] encodePlay(String address, String VERSION, String sessionid, int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("PLAY ");
        sb.append(address);
        sb.append(VERSION);
        sb.append("Session: ");
        sb.append(sessionid);
        sb.append("Cseq: ");
        sb.append(seq);
        sb.append("\r\n");
        sb.append("Range: npt=0.000-");
        sb.append("\r\n");
        sb.append("\r\n");
        System.out.println(sb.toString());
        //send(sb.toString().getBytes());
        return sb.toString().getBytes();
    }

    public static byte[] encodePause(String address, String VERSION, String sessionid, int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("PAUSE ");
        sb.append(address);
        sb.append("/");
        sb.append(VERSION);
        sb.append("Cseq: ");
        sb.append(seq);
        sb.append("\r\n");
        sb.append("Session: ");
        sb.append(sessionid);
        sb.append("\r\n");
        System.out.println(sb.toString());
        //send(sb.toString().getBytes());
        return sb.toString().getBytes();
    }

    public static byte[] encodeTeardown(String address, String VERSION, String sessionid, int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("TEARDOWN ");
        sb.append(address);
        sb.append("/");
        sb.append(VERSION);
        sb.append("Cseq: ");
        sb.append(seq);
        sb.append("\r\n");
        sb.append("User-Agent: LibVLC/2.2.1 (LIVE555 Streaming Media v2014.07.25)\r\n");
        sb.append("Session: ");
        sb.append(sessionid);
        sb.append("\r\n");
        System.out.println(sb.toString());
        return sb.toString().getBytes();
        //send(sb.toString().getBytes());
        //
    }
}
