/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.models;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import uam.azc.madsi.af.managers.AttackAnalyzer;

/**
 *
 * @author Vekz Light Breeze
 */
public class Attack {
    
    private int attackId;
    
    // Attributes
    private ArrayList<String> typeAttack;

    private int begin;
    private int end;
    
    
    
    // Getters And Setters
    public int getAttackId(){ return attackId; }
    public void setAttackId(int attackId){ this.attackId = attackId; }
    
    public int getBegin() { return begin; }
    public void setBegin(int begin) { this.begin = begin; }

    public int getEnd() { return end; }
    public void setEnd(int end) { this.end = end; }
    
    public ArrayList<String> getTypeAttack() { return typeAttack; }
    
    public void addTypeAttack(String _typeAttack){
        if(this.typeAttack == null) this.typeAttack = new ArrayList<>();
        this.typeAttack.add(_typeAttack);
    }

    
    
    // Get Keys of an specific data
    public ArrayList<String> getIPs() {
        return new ArrayList<>(AttackAnalyzer.getInstance().filterRepeatedIPs(begin, end).keySet());
    }

    public ArrayList<String> getCountries() {
        return new ArrayList<>(AttackAnalyzer.getInstance().filterRepeatedCountries(begin, end).keySet());
    }

    public ArrayList<String> getUsers(){
        return new ArrayList<>(AttackAnalyzer.getInstance().filterRepeatedUsers(begin, end).keySet());
    }
    
    public ArrayList<Date> getDates() {
        return AttackAnalyzer.getInstance().getDatesBetwen(begin, end);
    }
    
    
    
    // Counts of diferents data
    public int getCountIPs() {
        return AttackAnalyzer.getInstance().filterRepeatedIPs(begin, end).size();
    }

    public int getCountCountries() {
        return AttackAnalyzer.getInstance().filterRepeatedCountries(begin, end).size();
    }

    public int getCountUsers(){
        return AttackAnalyzer.getInstance().filterRepeatedUsers(begin, end).size();
    }
    

        
        
    
    // Return the String whit the most repeated specificdata and count
    // datainfo_value
    public String getMostRepeatedIP() {
        Hashtable<String, Integer> _ips = AttackAnalyzer.getInstance().filterRepeatedIPs(begin, end);
        List<String> __ips = new ArrayList<>(_ips.keySet());
        
        String ipMax = "";
        int max = 0;
        for(String it: __ips)
            if(max < _ips.get(it).intValue()) {
                ipMax = it;
                max = _ips.get(it).intValue();
            }

        return ipMax + "_" + max;
    }

    public String getMostRepeatedUser(){
        Hashtable<String, Integer> _users = AttackAnalyzer.getInstance().filterRepeatedUsers(begin, end);
        List<String> __users = new ArrayList<>(_users.keySet());
        
        String countryMax = "";
        int max = 0;
        for(String it: __users)
            if(max < _users.get(it).intValue()) {
                countryMax = it;
                max = _users.get(it).intValue();
            }

        return countryMax + "_" + max;
    }
    
    public String getMostRepeatedCountry() {
        Hashtable<String, Integer> _countries = AttackAnalyzer.getInstance().filterRepeatedCountries(begin, end);
        List<String> __countries = new ArrayList<>(_countries.keySet());

        String countryMax = "";
        int max = 0;
        for(String it: __countries)
            if(max < _countries.get(it).intValue()) {
                countryMax = it;
                max = _countries.get(it).intValue();
            }

        return countryMax + "_" + max;
    }

    
    
    // Return count of specific data
    public int getCountIp(String ip) {
        Hashtable<String, Integer> _ips = AttackAnalyzer.getInstance().filterRepeatedIPs(begin, end);
        return _ips.containsKey(ip) ? _ips.get(ip) : 0;
    }

    public int getCountCountry(String country) {
        Hashtable<String, Integer> _countries = AttackAnalyzer.getInstance().filterRepeatedCountries(begin, end);
        return _countries.containsKey(country) ? _countries.get(country) : 0;

    }
    
    public int getCountUser(String user) {
        Hashtable<String, Integer> _users = AttackAnalyzer.getInstance().filterRepeatedUsers(begin, end);
        return _users.containsKey(user) ? _users.get(user) : 0;

    }

    
    
    // Calculate duration of attack
    public Duration getDurationOfAttack(){
        ArrayList<Date> dates = AttackAnalyzer.getInstance().getDatesBetwen(begin, end);
        long newes = dates.get(dates.size()-1).getTime();
        long olders = dates.get(0).getTime();
        
        return Duration.ofMillis( ((newes - olders)) );
    }
 
    
    
    // Calculate the number of attacks in a second
    public float getFrecuency() {
        long diff = getDurationOfAttack().getSeconds();
        return (float)(end== begin? 0 :(diff/(end-begin)));
    }

    
    
    // Calculate if the attacks come from human or computer
    public boolean isAnHuman() {
        return (getFrecuency() >= 10);
    }
    
    
    
    
    @Override
    public String toString() {
        AttackAnalyzer.getInstance().getDatesBetwen(begin, begin);
        StringBuilder string = new StringBuilder();
        string.append("\n##########################################\n");
        string.append("#AttackId: " + attackId + "\n");
        string.append("#TypeAttack: " + typeAttack + "\n");
        string.append("#Registers: [" + begin + "," + end + "]\n");

        long minutes = getDurationOfAttack().toMinutes();
        long seconds = getDurationOfAttack().minusMinutes(minutes).getSeconds();
        
        string.append("#################Statistics###############\n");
        string.append("# Time Of Attack: "+ minutes +" minutes "+ seconds+" seconds\n");
        string.append("# Begin: " + AttackAnalyzer.getInstance().getDatesBetwen(begin, begin) + "\n");
        string.append("# End:   " + AttackAnalyzer.getInstance().getDatesBetwen(end, end) + "\n");
        string.append("# Attemps : " + (end+1-begin) +" attacks\n");
        string.append("# Frecueny: 1 attack each " + getFrecuency() +" seconds\n");
        string.append("# IPs Count: " + getCountIPs() +"\n");
        
        String mostRepeatedIP[] = getMostRepeatedIP().split("_");
        String mostRepeatedUser[] = getMostRepeatedUser().split("_");
        String mostRepeatedCountry[] = getMostRepeatedCountry().split("_");
        string.append("# Most Repeated IP : " + mostRepeatedIP[0] + "[" + mostRepeatedIP[1] +"]\n");
        string.append("# Countries Count: " + getCountCountries()+ "\n");
        string.append("# Most Repeated Country: " + mostRepeatedCountry[0] + "["+ mostRepeatedCountry[1] +"]\n");
        string.append("# Users Count: " + getCountUsers()+ "\n");
        string.append("# Most Repeated User: " + mostRepeatedUser[0] + "["+ mostRepeatedUser[1] +"]\n");
        string.append("##########################################\n");
        
        return string.toString();
    }

}
