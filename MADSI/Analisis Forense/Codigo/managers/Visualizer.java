/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.managers;

import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import uam.azc.madsi.af.models.Attack;


import java.util.Hashtable;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Vekz Light Breeze
 */
public class Visualizer {
    
    public String getSpecificStatistics(String type){
        switch(type){
            case "ip": break;
            case "usuario": break;
            case "pais": break;
        }
        return "";
    }
    
    public void printGeneralStatistic(){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacks();
        StringBuilder message = new StringBuilder();
        

        message.append("\n### Estadisticas Generales ###");
        message.append("\n#Attacks ID Counter: " + attacks.size());
        message.append("\n#Attemps: " + attackAnalyzer.getRegisterData().size());
        message.append("\n#IPs Counter: " + attackAnalyzer.filterRepeatedIPs(0, -1).size());
        message.append("\n#Users Counter: " + attackAnalyzer.filterRepeatedUsers(0, -1).size());
        message.append("\n#Countries Counter: " + attackAnalyzer.filterRepeatedCountries(0, -1).size());
        message.append("\n###############################");
        
        
        System.out.println(message.toString());
    }
    
    
    
    public void initVizualize(){
        Scanner keyboard = new Scanner(System.in);
        int response;
        boolean flagCycle;
        do{
            flagCycle = true;
            System.out.println("\n## ¿QUE DESEA IMPRIMIR? ##");
            System.out.println("[0] #Estadisticas Generales");
            System.out.println("[1] #Estadisticas Por Ataque");
            System.out.println("[2] #Salir");
            System.out.print("#Elija una opcion: ");            
            response = keyboard.nextInt();
            
            switch(response){
                case 0:
                    System.out.println("\n## ESTADISTICAS GENERALES ##");
                    System.out.println("[0] #Imprimir Reporte");
                    System.out.println("[1] #Mostrar Graficas");
                    System.out.print("#Elija una opcion: ");            
                    response = keyboard.nextInt();
                    
                    if(response == 0) printGeneralStatistic();
                    else if(response == 1){
                     //   graphicAllIPs();
                     //   graphicAllCountries();
                     //   graphicAllUsers();
                    } else System.out.println("No es una opcion valida");
                    break;
                case 1:
                    System.out.println("\n## ESTADISTICAS POR ATAQUE ##");
                    System.out.println("[0] #Imprimir Reporte De Todos Los Ataques");
                    System.out.println("[1] #Imprimir Reporte De Ataques DDos");
                    System.out.println("[2] #Imprimir Reporte De Ataques BruteForce");
                    System.out.println("[3] #Imprimir Reporte De Ataques Dictionary");
                    System.out.println("[4] #Mostrar Graficas DDoS");
                    System.out.println("[5] #Mostrar Graficas BruteForce");
                    System.out.println("[6] #Mostrar Graficas Dictionary");
                    System.out.print("#Elija una opcion: ");            
                    response = keyboard.nextInt();
                    
                    if(response == 0) {
                        for(Attack it: AttackAnalyzer.getInstance().getAttacks())
                            System.out.println(it.toString());
                    } else if(response >= 1 && response <= 3){
                        String type = "DDoS";
                        if(response == 2) type = "BruteForce";
                        if(response == 3) type = "Dictionary";
                        for(Attack it: AttackAnalyzer.getInstance().getAttacksOfType(type))
                            System.out.println(it.toString());
                    } else if(response >= 4 && response <= 6){
                        String type = "DDoS";
                        if(response == 5) type = "BruteForce";
                        if(response == 6) type = "Dictionary";
                      //  graphicAllIPsType(type);
                      //  graphicAllCountriesType(type);
                      //  graphicAllUsersType(type);
                    }else System.out.println("No es una opcion valida");
                    break;
                case 2: 
                    flagCycle = false;
                    break;
                default: 
                    System.out.println(response + " no es una opcion elegible. Intente de nuevo.\n\n");
            }
        } while(flagCycle);
    }

    
    
    
    public ArrayList<String> getGeneralStatistics(){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<String> statistics = new ArrayList<>();
                
        statistics.add(attackAnalyzer.getAttacks().size()+"");
        statistics.add(attackAnalyzer.getRegisterData().size()+"");
        statistics.add(attackAnalyzer.filterRepeatedIPs(0, -1).size()+"");
        statistics.add(attackAnalyzer.filterRepeatedUsers(0, -1).size()+"");
        statistics.add(attackAnalyzer.filterRepeatedCountries(0, -1).size()+"");
        
        String mostIp[] = attackAnalyzer.filterMostRepeatedIP(0, -1).split(":");
        String mostUser[] = attackAnalyzer.filterMostRepeatedUser(0, -1).split(":");
        String mostCountry[] = attackAnalyzer.filterMostRepeatedCountry(0, -1).split(":");
        statistics.add(mostIp[0]);
        statistics.add(mostIp[1]);
        statistics.add(mostUser[0]);
        statistics.add(mostUser[1]);
        statistics.add(mostCountry[0]);
        statistics.add(mostCountry[1]);
        
        return statistics;
    }
    
    public Hashtable<Integer, Integer> gcAttacksIPType(String type){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<Integer, Integer> content = new Hashtable<>();
        
        for(Attack it: attacks)
            content.put(it.getCountIPs(), it.getAttackId());
        
        
        return content;
    }
 
    public Hashtable<Integer, Integer> gcAttacksCountryType(String type){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<Integer, Integer> content = new Hashtable<>();
        
        for(Attack it: attacks)
            content.put(it.getCountCountries(), it.getAttackId());
        
        
        return content;
    }
    
    public Hashtable<Integer, Integer> gcAttacksUsersType(String type){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<Integer, Integer> content = new Hashtable<>();

        
        for(Attack it: attacks)
            content.put(it.getAttackId(), it.getCountUsers());
        
        return content;
    }


    
    // Graphics of Diff Specific counter data
    public Hashtable<String, Integer> gcDiffIPTypeOf(String type, int attackIdRelative){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Attack attack = attacks.get(attackIdRelative);
        ArrayList<String> ips = attack.getIPs();
        Hashtable<String, Integer> content = new Hashtable<>();
        
 
        for(String it: ips)
            content.put(it, attack.getCountIp(it));
        
        
        return content;
    }
    
    public Hashtable<String, Integer> gcDiffCountryTypeOf(String type, int attackIdRelative){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Attack attack = attacks.get(attackIdRelative);
        ArrayList<String> countries = attack.getCountries();
        Hashtable<String, Integer> content = new Hashtable<>();
        
        for(String it: countries)
            content.put(it, attack.getCountCountry(it));
        
        
        return content;
    }
    
    public Hashtable<String, Integer> gcDiffUsersTypeOf(String type, int attackIdRelative){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<String, Integer> content = new Hashtable<>();
        
        Attack attack = attacks.get(attackIdRelative);
        ArrayList<String> users = attack.getUsers();
        
        
        for(String it: users)
            content.put(it, attack.getCountUser(it));
        
          
        
        return content;
    }


    
    // Graphics of all Diff Specific counter data
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Hashtable<String, Integer> gcAllIPsType(String type){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<String, Integer> content = new Hashtable<>();

        ArrayList<String> ips = new ArrayList<>();
        for(Attack it: attacks)
            ips.addAll(it.getIPs());
        
        
        for(String it: ips){
            int countIPs = 0;
            for(Attack _it: attacks)
                countIPs += _it.getCountIp(it);
            content.put(it, countIPs);
        }

        
        return content;
    }
    
    public Hashtable<String, Integer> gcAllCountriesType(String type){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<String, Integer> content = new Hashtable<>();
        
        ArrayList<String> countries = new ArrayList<>();
        for(Attack it: attacks)
            countries.addAll(it.getCountries());
        
        
        for(String it: countries){
            int countCountries = 0;
            for(Attack _it: attacks)
                countCountries += _it.getCountCountry(it);
            content.put(it, countCountries);
        }

        
        return content;
    }
    
    public Hashtable<String, Integer> gcAllUsersType(String type){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacksOfType(type);
        Hashtable<String, Integer> content = new Hashtable<>();
        
        ArrayList<String> users = new ArrayList<>();
        for(Attack it: attacks)
            users.addAll(it.getUsers());
        
        
        for(String it: users){
            int countUsers = 0;
            for(Attack _it: attacks)
                countUsers += _it.getCountUser(it);
            content.put(it, countUsers);
        }

        
        return content;
    }
    
    
    
    // Graphics all Attacks counters
    public Hashtable<String, Integer> gcAllIPs(){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacks();
        Hashtable<String, Integer> content = new Hashtable<>();
        
        ArrayList<String> ips = new ArrayList<>();
        for(Attack it: attacks)
            ips.addAll(it.getIPs());
        
        
        for(String it: ips){
            int countIPs = 0;
            for(Attack _it: attacks)
                countIPs += _it.getCountIp(it);
            content.put(it, countIPs);
        }

        
        return content;
    }
    
    public Hashtable<String, Integer> gcAllCountries(){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacks();
        Hashtable<String, Integer> content = new Hashtable<>();
        
        ArrayList<String> countries = new ArrayList<>();
        for(Attack it: attacks)
            countries.addAll(it.getCountries());
        
        
        for(String it: countries){
            int countCountries = 0;
            for(Attack _it: attacks)
                countCountries += _it.getCountCountry(it);
            content.put(it, countCountries);
        }

        
        return content;
    }
    
    public Hashtable<String, Integer> gcAllUsers(){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();
        ArrayList<Attack> attacks = attackAnalyzer.getAttacks();
        Hashtable<String, Integer> content = new Hashtable<>();
        
        ArrayList<String> users = new ArrayList<>();
        for(Attack it: attacks)
            users.addAll(it.getUsers());
        

        for(String it: users){
            int countUsers = 0;
            for(Attack _it: attacks)
                countUsers += _it.getCountUser(it);
            
            content.put(it, countUsers);
        }

        return content;
    }
}
