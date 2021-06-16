/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.models;

import java.util.Date;

/**
 *
 * @author Vekz Light Breeze
 */
public class RegisterData {
    private Date regDate;
    
    private String server;
    
    private String protocol;
    private int sysId;
   
    private String ip;
    private String latitude;
    private String longitude;
    private String country;
    private String user;
    private int port;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Date getRegDate() { return regDate; }
    public void setRegDate(Date regDate) { this.regDate = regDate; }

    public String getServer() { return server; }
    public void setServer(String server) { this.server = server; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public int getSysId() { return sysId; }
    public void setSysId(int sysId) { this.sysId = sysId; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    @Override
    public String toString() {
        return "RegisterData{" + "regDate=" + regDate + ", server=" + server + ", protocol=" + protocol + ", sysId=" + sysId + ", ip=" + ip + ", latitude=" + latitude + ", longitude=" + longitude + ", country=" + country + ", user=" + user + ", port=" + port + '}';
    }

    
}
