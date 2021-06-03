/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Vekz Light Breeze
 */
public class Reader {
    
    private static Reader instance;
    
    public static Reader getInstance(){
        if(instance == null) instance = new Reader();
        return instance;
    }
    
    public boolean probeFile(String route){
        return (new File (route)).exists();
    }
    
    public ArrayList<String> read(String route){
        ArrayList<String> content = new ArrayList<>();
        
        File file = null;
        FileReader fileReader = null;
        BufferedReader buffReader = null;
        
        try {
            System.out.print("Leyendo archivo...");
            file = new File(route);
            fileReader = new FileReader(file);
            buffReader = new BufferedReader(fileReader);
            
            String linea;
            while((linea = buffReader.readLine())!=null)
               content.add(linea);
            
        } catch (FileNotFoundException ex) {
            System.out.println("Error al ABRIR el archivo.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error al LEER el archivo.");
            ex.printStackTrace();
        } finally {
            if( fileReader != null ) try {
                fileReader.close();
            } catch (IOException ex) {
                System.out.println("Error al CERRAR el archivo.");
                ex.printStackTrace();
            }
        }
        System.out.print("Listo!");
        return content;       
    }
    
    public ArrayList<String> read(String route, int begin, int end){
        ArrayList<String> content = new ArrayList<>();
        
        File file = null;
        FileReader fileReader = null;
        BufferedReader buffReader = null;
        System.out.print("Leyendo archivo...");
        try {
            file = new File(route);
            fileReader = new FileReader(file);
            buffReader = new BufferedReader(fileReader);
            
            String linea;
            int i = 0;
            if(begin != 0)
                while( (buffReader.readLine() != null) && (i < begin))
                    i++;
            
            while( ((linea = buffReader.readLine())!= null) && (i <= end)){
               content.add(linea);
               i++;
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("Error al ABRIR el archivo.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error al LEER el archivo.");
            ex.printStackTrace();
        } finally {
            if( fileReader != null ) try {
                fileReader.close();
            } catch (IOException ex) {
                System.out.println("Error al CERRAR el archivo.");
                ex.printStackTrace();
            }
        }
        System.out.println("Listo!");
        return content;  
    }
}
