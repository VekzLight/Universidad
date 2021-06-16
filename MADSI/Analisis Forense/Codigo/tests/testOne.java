/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.tests;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uam.azc.madsi.af.models.Attack;
import uam.azc.madsi.af.io.Reader;
import uam.azc.madsi.af.managers.AttackAnalyzer;
import uam.azc.madsi.af.managers.DataAnalyzer;
import uam.azc.madsi.af.managers.RegisterManager;
import uam.azc.madsi.af.managers.Visualizer;
import uam.azc.madsi.af.models.RegisterData;
import uam.azc.madsi.af.models.RegisterLog;

/**
 *
 * @author Vekz Light Breeze
 */
public class testOne {

    public static void main(String args[]) throws FileNotFoundException, IOException{
        RegisterManager regManager = new RegisterManager();
        regManager.setBufferSize(1000);
        regManager.loadRegisters("auth.log");
        
        DataAnalyzer dataAnalyzer = new DataAnalyzer();
        dataAnalyzer.analizeLog(regManager.getRegisters());
        
        AttackAnalyzer attack = AttackAnalyzer.getInstance();
        attack.setRegistersData(dataAnalyzer.getRegisters());
        attack.setTimeAttack(60000);
        attack.analyzeAttacks();
       
        Visualizer visual = new Visualizer();
        Hashtable<String, Integer> content = visual.gcAllIPs();
        ArrayList<String> ips = new ArrayList<>();
        for(String it:content.keySet()){
            ips.add(it + ":" +content.get(it));
        }
        
        Gson gson = new Gson();
        String respuesta = gson.toJson(ips);
        System.out.println(respuesta);
    }
/*
    public static void probeWM(String args[]){
        ServerConfig wm = new ServerConfig();
        wm.initWM(args);
    
        BufferedReader buffReader = null;
        
        String dataurl= "data:text/plain;base64,aG9sYSBtdW5kbwptdW5kbyAxMgpjdWFyZW50YQ==";
        String dat = dataurl.split(",")[1];
        byte[] decoded = Base64.getDecoder().decode(dat);
        String datos = new String(decoded);
        System.out.println("Leyendo archivo...");
        System.out.println(datos);
        
        ArrayList<String> contenido = new ArrayList<>();
        String[] datosSep = datos.split("\n");
        for(String it: datosSep)
            contenido.add(it);
    }
  */  
    public static void probeAttackAnalize(){
        RegisterManager regManager = new RegisterManager();
        regManager.setBufferSize(8000);
        regManager.loadRegisters("auth.log");
        
        DataAnalyzer dataAnalyzer = new DataAnalyzer();
        dataAnalyzer.analizeLog(regManager.getRegisters());
        
        AttackAnalyzer attack = AttackAnalyzer.getInstance();
        attack.setRegistersData(dataAnalyzer.getRegisters());
        attack.setTimeAttack(60000);
        attack.analyzeAttacks();
       
        Visualizer visualizer = new Visualizer();
        visualizer.initVizualize();

    }
    
    public static void probeAnalisys(){
        RegisterManager regManager = new RegisterManager();
        regManager.setBufferSize(500);
        regManager.loadRegisters("auth.log");
        
        DataAnalyzer dataAnalizer = new DataAnalyzer();
        dataAnalizer.analizeLog(regManager.getRegisters());
        ArrayList<RegisterData> registers = dataAnalizer.getRegisters();
        for(RegisterData it: registers)
            System.out.println(it.toString());
    }
    
    public static void probeReader(String route){
        ArrayList<String> contenido = Reader.getInstance().read(route);
        
        for(String it:contenido){
            System.out.println(it);
        }
    }
    
    public static void probeManageReader(){
        RegisterManager regManager = new RegisterManager();
        regManager.loadRegisters("auth.log");
        
        for(RegisterLog it: regManager.getRegisters()){
            System.out.println(it.toString());
        }
    }


}
