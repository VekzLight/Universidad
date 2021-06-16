/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import uam.azc.madsi.af.managers.AttackAnalyzer;
import uam.azc.madsi.af.managers.DataAnalyzer;
import uam.azc.madsi.af.managers.RegisterManager;
import uam.azc.madsi.af.managers.Visualizer;
import uam.azc.madsi.af.models.Attack;

/**
 * REST Web Service
 *
 * @author vekz
 */
@Path("/analizar")
public class ControllersResource {

    @Context
    private UriInfo context;


    public ControllersResource() {
    }


    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response getJson() {
        Gson gson = new Gson();
        JsonObject respuesta = gson.fromJson("{\"status\":\"Corrected\"}", JsonObject.class);
        
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }

    @Path("/archivo")
    @POST
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response postJson(String dataUrl) {
        System.out.println("##########################################################");
        String datosBase64 = dataUrl.split(",")[1];
        String datos = new String(Base64.getDecoder().decode(datosBase64));
        
        ArrayList<String> contenido = new ArrayList<>();
        String[] datosSep = datos.split("\n");
        for(String it: datosSep)
            contenido.add(it);
        
        RegisterManager regManager = new RegisterManager();
        regManager.setBufferSize(8000);
        regManager.loadRegisters(contenido);
        
        DataAnalyzer dataAnalyzer = new DataAnalyzer();
        dataAnalyzer.analizeLog(regManager.getRegisters());
        
        AttackAnalyzer attack = AttackAnalyzer.getInstance();
        attack.setRegistersData(dataAnalyzer.getRegisters());
        attack.setTimeAttack(60000);
        attack.analyzeAttacks();
        
        Visualizer visual = new Visualizer();
        ArrayList<String> content = visual.getGeneralStatistics();
        System.out.println("##########################################################");
        
        
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    // Graphics all Attacks counters
    @Path("/AllIPs")
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response gcAllIPs(){
        Visualizer visual = new Visualizer();
        Hashtable<String, Integer> content = visual.gcAllIPs();
        ArrayList<String> ips = new ArrayList<>();
        for(String it:content.keySet()){
            ips.add(it + ":" +content.get(it));
            System.out.println(it);
        }
        
        Gson gson = new Gson();
        String respuesta = gson.toJson(ips);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AllCountries")
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response gcAllCountries(){
        Visualizer visual = new Visualizer();
        Hashtable<String, Integer> content = visual.gcAllCountries();
        ArrayList<String> countries = new ArrayList<>();
        for(String it:content.keySet()){
            countries.add(it +":" +content.get(it));
            System.out.println(it);
        }
        
        Gson gson = new Gson();
        String respuesta = gson.toJson(countries);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AllUsers")
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response gcAllUsers(){
        Visualizer visual = new Visualizer();
        Hashtable<String, Integer> content = visual.gcAllUsers();
        ArrayList<String> countries = new ArrayList<>();
        for(String it:content.keySet()){
            countries.add(it + ":"+content.get(it));
            System.out.println(it);
        }

        Gson gson = new Gson();
        String respuesta = gson.toJson(countries);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }

}
