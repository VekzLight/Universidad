/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.models;

/**
 *
 * @author Vekz Light Breeze
 */
public class RegisterLog {
    
    private int sysId;
    private String date;
    private String server;
    private String protocol;
    private String desciption;

    public int getSysId() {return sysId;}
    public void setSysId(int sysId) {this.sysId = sysId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getServer() { return server; }
    public void setServer(String server) { this.server = server; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }


    public String getDesciption() { return desciption; }
    public void setDesciption(String desciption) { this.desciption = desciption; }

    @Override
    public String toString() {
        return "RegisterLog{" + "date=" + date + ", protocol=" + protocol + ", server=" + server + ", sysId=" + sysId + ", desciption=" + desciption + '}';
    }

    
}
