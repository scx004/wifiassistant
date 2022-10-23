package org.daai.wifiassistant.network;

public class Data {

    private static int pcountnum = 5;
    private static float pwaittime = 0.2f;
      
    public static int getpc() {
        return pcountnum;
    }  
      
    public static void setCT(int pcountnum) {
        Data.pcountnum = pcountnum;
    }
    public static float getwt() {
        return pwaittime;
    }

    public static void setWT(float pwaittime) {
        Data.pwaittime = pwaittime;
    }
}  
