/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.function.Consumer;
import uam.azc.madsi.af.models.Attack;
import uam.azc.madsi.af.models.RegisterData;

/**
 *
 * @author Vekz Light Breeze
 */
public class AttackAnalyzer {
    
    // For Singleton Pattern
    private static AttackAnalyzer instance;
    
    
    
    // Data analyzed and to analyze 
    private ArrayList<RegisterData> registers;
    private ArrayList<Attack> attacks;
    
    
    
    // Attributes for personalize analyze
    private long timeAttack;
    private int ipsAttack;
    private int usrAttack;
    private int attempsAttack;
    
    
    
    /**
     * Contructor
     */
    private AttackAnalyzer(){
        timeAttack = 10000;
        attempsAttack = 8;
        ipsAttack = 3;
        usrAttack = 3;
      
        attacks = new ArrayList<>(); 
    }
    
    
    
    /**
     * Return instance of this class
     * @return instance
     */
    public static AttackAnalyzer getInstance(){
        if(instance == null) instance = new AttackAnalyzer();
        return instance;
    }
    
    
    
    
    // Getters and Setters
    public int getIpsAttack() { return ipsAttack; }
    public void setIpsAttack(int ipsAttack) { this.ipsAttack = ipsAttack; }
    
    public long getTimeAttack() { return timeAttack; }
    public void setTimeAttack(long timeAttack) { this.timeAttack = timeAttack; }
    
    public int getAttempsAttack() { return attempsAttack; }
    public void setAttempsAttack(int attempsAttack) { this.attempsAttack = attempsAttack; }
    
    public ArrayList<Attack> getAttacks(){ return attacks; } 
    public void setRegistersData(ArrayList<RegisterData> registers){ this.registers = registers; }

    public Attack getAttack(int attackId){
        Attack attack = null;
        for(Attack it: attacks)
            if(it.getAttackId() == attackId)
                attack = it;
        return attack;
    }
    
    public ArrayList<RegisterData> getRegisterData(){ return registers; }
    
    // Initialize analysis
    public void analyzeAttacks(){
        Date dateNew;
        Date datePrev = registers.get(0).getRegDate();
        long diff = 0;
        int begin = 0;
        boolean flagOver = false;
        System.out.print("Analizando Ataques...");
        for(int i = 1; i < registers.size(); i++){
            dateNew = registers.get(i).getRegDate();
            diff = calculateDiferenceTime(datePrev, dateNew);
            if(diff <= timeAttack && flagOver){
                attacks.addAll(generateAttackReport(begin, i-1));
                begin = i;
            }
            if(diff <= timeAttack){
                flagOver = false;
            } else if(!flagOver){
                attacks.addAll(generateAttackReport(begin, i-1));
                begin = i;
                flagOver = true;
            }
            datePrev = dateNew; 
        }
        if(attacks.isEmpty()){
            attacks.addAll(generateAttackReport(begin, registers.size()));
        }
        System.out.println("Listo!");
    }
    
    
    
    // Getters of each attack type
    public ArrayList<Attack> getAttacksOfType(String type){
        ArrayList<Attack> _attack = new ArrayList<>();
        for(Attack it: attacks)
            if(it.getTypeAttack().contains(type))
                _attack.add(it);
        return _attack;
    }
    
    
    
    
    // Generate all Attack objects of RegistersData
    public ArrayList<Attack> generateAttackReport(int begin, int end){
        ArrayList<Attack> _attacks = new ArrayList<>();
        ArrayList<String> _ips = new ArrayList<>();
        ArrayList<String> _usr = new ArrayList<>();
        
        byte typeAttack[] = {0,0};
        int ipCount = 0;
        int usrCount = 0;
        
        for(int i = begin; i < end; i++){
            RegisterData it = registers.get(i);
            
            if(!_ips.contains(it.getIp()) && typeAttack[0] == 0) {
                //System.out.println(it.getIp() +"\t");
                ipCount++;
                if(ipCount >= ipsAttack) typeAttack[0] = 1;
                _ips.add(it.getIp());
            }
            
            if(!_usr.contains(it.getUser()) && typeAttack[1] == 0) {
                //System.out.println(it.getUser());
                usrCount++;
                if(usrCount >= usrAttack) typeAttack[1] = 1;
                _usr.add(it.getUser());
            }
            
            if(typeAttack[0] == 1 && typeAttack[1] == 1) break;
        }
        
        Attack attack  = new Attack();
        if(typeAttack[0] == 1) attack.addTypeAttack("DDoS");    
        if(typeAttack[1] == 1) attack.addTypeAttack("Dictionary");
        if(typeAttack[0] == 0 && typeAttack[1] == 0)attack.addTypeAttack("BruteForce");
        attack.setAttackId(attacks.size());
        attack.setBegin(begin);
        attack.setEnd(end);

        _attacks.add(attack);
        return _attacks;
    }
    
    
    
    // Calculate diference of two Dates, return time in milliseconds
    public long calculateDiferenceTime(Date datePrev, Date dateNew){
        long _datePrev = datePrev.getTime();
        long _dateNew = dateNew.getTime();
        long diff = _dateNew - _datePrev;

        return diff;
    }

    
    
    // Filter all data and count attemps
    public Hashtable<String, Integer> filterRepeatedIPs(int begin, int end){
        Hashtable<String, Integer> _ips = new Hashtable<>();
        end = (end==-1)?registers.size()-1:end;
        for(int i = begin; i < end; i++){
            RegisterData _data = registers.get(i);
            if(!_ips.containsKey(_data.getIp()+":"+_data.getLatitude()+":"+_data.getLongitude())) _ips.put(_data.getIp()+":"+_data.getLatitude()+":"+_data.getLongitude(), 1);
            else _ips.replace(_data.getIp()+":"+_data.getLatitude()+":"+_data.getLongitude(), (_ips.get(_data.getIp()+":"+_data.getLatitude()+":"+_data.getLongitude())+1) );
        }
        return _ips;
    }

    public Hashtable<String, Integer> filterRepeatedUsers(int begin, int end){
        Hashtable<String, Integer> __users = new Hashtable<>();
        end = (end==-1)?registers.size()-1:end;
        for(int i = begin; i < end; i++){
            RegisterData _data = registers.get(i);
            if(!__users.containsKey(_data.getUser())) __users.put(_data.getUser(), 1);
            else __users.replace(_data.getUser(), (__users.get(_data.getUser())+1) );
        }
        return __users;
    }
    
    public Hashtable<String, Integer> filterRepeatedCountries(int begin, int end){
        Hashtable<String, Integer> _ips = new Hashtable<>();
        end = (end==-1)?registers.size()-1:end;
        for(int i = begin; i < end; i++){
            //System.out.println(i);
            RegisterData _data = registers.get(i);
            if(!_ips.containsKey(_data.getCountry())) _ips.put(_data.getCountry(), 1);
            else _ips.replace(_data.getCountry(), (_ips.get(_data.getCountry())+1) );
            
        }
        return _ips;
    }
    
    public String filterMostRepeatedIP(int begin, int end){
        Hashtable<String, Integer> _ips = filterRepeatedIPs(begin, end);
        return findMostRepeated(_ips);
    }
    
    public String filterMostRepeatedCountry(int begin, int end){
        Hashtable<String, Integer> _countries = filterRepeatedCountries(begin, end);
        return findMostRepeated(_countries);
    }
    
    public String filterMostRepeatedUser(int begin, int end){
        Hashtable<String, Integer> _users = filterRepeatedUsers(begin, end);
        return findMostRepeated(_users);
    }
        
    public String findMostRepeated(Hashtable<String, Integer> collection){
        int position = 0;
        int max = 0;
        
        ArrayList<Integer> values = new ArrayList<>(collection.values());
        for(Integer value: values){
            if(value > max) max = value;
            position++;
        }
        
        for(int i = 0; i < position; i++){
            collection.keys().nextElement();
        }
        String ip = collection.keys().nextElement();
        return ip +":"+max;
    }
        
    // Return all dates betwen beind and end
    public ArrayList<Date> getDatesBetwen(int begin, int end){
        ArrayList<Date> dates = new ArrayList<>();
        for(int i = begin; i <= end; i++)
            dates.add(registers.get(i).getRegDate());
        return dates;
    }


    
    // Return all data diff betwen beind and end of an specific ip
    public int getTimesBetwenOf(int begin, int end, String ip){
        int times = 0;
        for(int i = begin; i <= end; i++){
            if(registers.get(i).getIp().equalsIgnoreCase(ip))
                times++;
        }
        return times;
    }
}
