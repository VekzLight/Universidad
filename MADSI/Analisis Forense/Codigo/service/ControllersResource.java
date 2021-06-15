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
    
    
    @Path("/AttacksIPType")
    @POST
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<Integer, Integer> gcAttacksIPType(String type){
    public Response gcAttacksIPType(String type){
        Hashtable<Integer, Integer> content = new Visualizer().gcAttacksIPType(type);
          
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
/* 
    @Path("/AttacksCountryType")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<Integer, Integer> gcAttacksCountryType(String type){
    public Response gcAttacksCountryType(String type){
        
        Hashtable<Integer, Integer> content = new Visualizer().gcAttacksCountryType(type);
 
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AttacksUsersType")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<Integer, Integer> gcAttacksUsersType(String type){
    public Response gcAttacksUsersType(String type){
        Hashtable<Integer, Integer> content = new Visualizer(). gcAttacksUsersType(type);

        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }


    
    // Graphics of Diff Specific counter data
    @Path("/DiffIPTypeOf")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcDiffIPTypeOf(String type, int attackIdRelative){
    public Response gcDiffIPTypeOf(String type, int attackIdRelative){
        Hashtable<String, Integer> content = new Visualizer().gcDiffIPTypeOf(type, attackIdRelative);
        
 
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    
    @Path("/DiffCountryTypeOf")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcDiffCountryTypeOf(String type, int attackIdRelative){
    public Response gcDiffCountryTypeOf(String type, int attackIdRelative){
        Hashtable<String, Integer> content = new Visualizer().gcDiffCountryTypeOf(type, attackIdRelative);
       
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/DiffUsersTypeOf")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcDiffUsersTypeOf(String type, int attackIdRelative){
    public Response gcDiffUsersTypeOf(String type, int attackIdRelative){
        Hashtable<String, Integer> content = new Visualizer().gcDiffUsersTypeOf(type, attackIdRelative);
        
                  
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }


    
    // Graphics of all Diff Specific counter data
    @Path("/AllIPsType")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcAllIPsType(String type){
    public Response gcAllIPsType(String type){
        Hashtable<String, Integer> content = new Visualizer().gcAllIPsType(type);

        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AllCountriesType")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcAllCountriesType(String type){
    public Response gcAllCountriesType(String type){
        Hashtable<String, Integer> content = new Visualizer().gcAllCountriesType(type);
        
       
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AllUsersType")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcAllUsersType(String type){
    public Response gcAllUsersType(String type){
        Hashtable<String, Integer> content = new Visualizer().gcAllUsersType(type);
        
        
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
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcAllIPs(){
    public Response gcAllIPs(){
        Hashtable<String, Integer> content = new Visualizer().gcAllIPs();
        
        
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AllCountries")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcAllCountries(){
    public Response gcAllCountries(){
        Hashtable<String, Integer> content = new Visualizer().gcAllCountries();
        
        
        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @Path("/AllUsers")
    @GET
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    //public Hashtable<String, Integer> gcAllUsers(){
    public Response gcAllUsers(){
        Hashtable<String, Integer> content = new Visualizer().gcAllUsers();
        
        

        Gson gson = new Gson();
        String respuesta = gson.toJson(content);
        return Response.ok(respuesta.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methos", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
*/
}
