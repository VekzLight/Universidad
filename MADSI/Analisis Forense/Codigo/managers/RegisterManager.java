/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.managers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uam.azc.madsi.af.io.Reader;
import uam.azc.madsi.af.models.RegisterLog;

/**
 *
 * @author Vekz Light Breeze
 */
public class RegisterManager {
    
    ArrayList<RegisterLog> registers;
    int bufferSize; //  0 == all
    int section;    // -1 == Only one, 0+ == All file in cycles
    
    boolean success;
    
    public RegisterManager(){
        this.registers = new ArrayList<>();
        this.bufferSize = 0;
        this.section = 0;
        this.success = false;
    }
    
    public boolean success(){ return success; }
    
    public void loadRegisters(String route){
        ArrayList<String> content = null;
        
        this.success = Reader.getInstance().probeFile(route);
        if(this.success){
        
            if(bufferSize == 0) content = Reader.getInstance().read(route);
            else content = Reader.getInstance().read(route, section, section + bufferSize);

            System.out.print("Separando Informacion Util...");
            for(String it: content){
                String _it_split[] = it.split(" ");

                Pattern pat_corchetes = Pattern.compile(".*[\\[\\d*\\]].*");
                Matcher match_corchetes = pat_corchetes.matcher(_it_split[4]);

                Pattern pat_FPass = Pattern.compile(".*Failed\\spassword.*");
                Matcher match_FPass = pat_FPass.matcher(it);

                Pattern pat_sudo = Pattern.compile("^sudo.*");
                Matcher match_sudo = pat_sudo.matcher(_it_split[4]);

                if (!match_sudo.matches() && match_FPass.matches()) {
                    RegisterLog _reglog = new RegisterLog();

                    String date = _it_split[0] + " " + _it_split[1] + " " + _it_split[2];
                    String server = _it_split[3];
                    String protocol = "null";
                    String description = "null";
                    int sysId = -1;       

                    if(match_corchetes.matches()){
                        String protocol_split[] = _it_split[4].split("\\[");
                        protocol = protocol_split[0];
                        sysId = Integer.parseInt(protocol_split[1].substring(0, protocol_split[1].length() - 2));
                    } else {
                        protocol = _it_split[4].substring(0, _it_split[4].length() - 1);
                    }

                    int begin_description = it.split(":\\s")[0].length()+2;
                    description = it.substring(begin_description);

                    _reglog.setServer(server);
                    _reglog.setDate(date);
                    _reglog.setDesciption(description);
                    _reglog.setSysId(sysId);
                    _reglog.setProtocol(protocol);
                    registers.add(_reglog);
                }
            }
            System.out.println("Listo!");
        } else {
            System.out.println("No existe el archivo o esta corrupto.");
        }
    }

    public ArrayList<RegisterLog> getRegisters() { return registers; }
    public RegisterLog getRegister(int index){ return registers.get(index); }

    public int getBufferSize() { return bufferSize; }
    public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }

    public int getSection() { return section; }
    public void setSection(int section) { this.section = section; }

    @Override
    public String toString() {
        return "RegisterManager{" + "registers=" + registers + ", bufferSize=" + bufferSize + ", section=" + section + '}';
    }
    
}
