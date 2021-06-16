/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uam.azc.madsi.af.models.RegisterData;
import uam.azc.madsi.af.models.RegisterLog;

/**
 *
 * @author Vekz Light Breeze
 */
public class DataAnalyzer {
    
    // Temp por more performance in the API
    private Hashtable<String, String> ipCountry;
    
    // All registersData
    private ArrayList<RegisterData> registers;
    
    
    // Contructor
    public DataAnalyzer(){
        this.ipCountry = new Hashtable<>();
        this.registers = new ArrayList<>();
    }
 
    
    public ArrayList<RegisterData> getRegisters(){ return registers; }
    
    
    public void analizeLog(ArrayList<RegisterLog> registersLog){
        System.out.print("Dando Formato a los Datos...");
        for(RegisterLog it: registersLog){
            RegisterData _regData = new RegisterData();
            
            Date _regDate = generateDate(it.getDate());
            String _data[] = analizeDescription(it.getDesciption());
            String ipLook[] = getIpCountry(_data[0]).split(":");

            _regData.setRegDate( _regDate );
            _regData.setSysId( it.getSysId() );
            _regData.setProtocol( it.getProtocol() );
            _regData.setServer( it.getServer() );

            _regData.setCountry( ipLook[0] );
            _regData.setLatitude(ipLook[1]);
            _regData.setLongitude(ipLook[2]);
            
            _regData.setIp( _data[0] );
            _regData.setUser( _data[1] );
            _regData.setPort( (_data[2].equalsIgnoreCase("null"))? -1:Integer.parseInt(_data[2])  );
            registers.add( _regData );
        }
        System.out.println("Listo!");
    }
       
    public String getIpCountry(String ip){
        String country = "null";
        if(!ip.equalsIgnoreCase("null")){
            if(ipCountry.containsKey(ip)){
                country = ipCountry.get(ip);
            } else{
                String xmlResponse = getXmlAPI(ip);
                String latitude = ":Unknown";
                String longitude = ":Unknown";
                
                String xmlCut = xmlResponse.split("countryname")[1];
                country = xmlCut.substring(1, xmlCut.length() - 2);
                
                xmlCut = xmlResponse.split("latitude")[1];
                country += ":"+xmlCut.substring(1, xmlCut.length() - 2);
                
                xmlCut = xmlResponse.split("longitude")[1];
                country += ":"+xmlCut.substring(1, xmlCut.length() - 2);
                
                
                ipCountry.put(ip, country);
            }
        }
        return country;
    }
    private String getXmlAPI(String ip){
        StringBuilder xmlResponse = new StringBuilder();
        try {
            URL url = new URL("http://api.geoiplookup.net/?query="+ip);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml");

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response;
            while ((response = rd.readLine()) != null) {
                if(response.matches("^<results>.*"))
                    xmlResponse.append(response);
                System.out.println(xmlResponse);
            }
            
            rd.close();
        } catch (IOException ex) {
            return "<countryname>Unknown</countryname>";
        }
        
        return xmlResponse.toString();
    }    
    
    public String[] analizeDescription(String _description){
        String description[] = {"null","null","null"};
        String _descriptionSplit[] = _description.split(" ");

        Pattern pat_ip = Pattern.compile(".*(\\d{1,3}\\.){3}\\d{1,3}$");        
        Pattern pat_user = Pattern.compile("^user$|^for$|^user=.*$|^invalid$");
        Pattern pat_port = Pattern.compile("^\\d{4,5}$|^\\d{4,5}:.*$");
        
        boolean flagUser = false;
        boolean flagNext = false;
        for(String it: _descriptionSplit){
            Matcher match_ip = pat_ip.matcher(it);
            Matcher match_port = pat_port.matcher(it);
            Matcher match_user = pat_user.matcher(it);
            
            if(match_ip.matches()) {
                if(it.matches("^.*=.*$")) description[0] = it.split("=")[1];
                else description[0] = match_ip.group();
            }
            if(match_port.matches()) {
                if(it.matches("^\\d{4,5}:.*$")) description[2] = it.split(":")[0];
                else description[2] = match_port.group();
            }
            if(match_user.matches()) {
                if(it.matches("^user=.*$")){
                    description[1] = it.split("=")[1];
                    flagNext = false;
                    break;
                } else if(it.matches("^user$")) {
                    flagNext = false;
                    flagUser = true;
                }
                else {flagNext = true;}
            } else if(flagNext){
                description[1] = it;
                flagNext = false;
            } else if(flagUser){
                description[1] = it;
                flagUser = false;
            }
        }

        return description;
    }
    
    
    public Date generateDate(String _date){
        DateFormat dateFormat = new SimpleDateFormat("MMM dd kk:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(_date);
        } catch (ParseException ex) {
            System.out.println("No se pudo convertir la hora");
        }
        
        return date;
    }
    
    @Override
    public String toString() {
        return "DataAnalyzer{" + "registers=" + registers + '}';
    }
    
}
