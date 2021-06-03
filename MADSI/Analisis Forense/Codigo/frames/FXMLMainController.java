/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.frames;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import uam.azc.madsi.af.managers.AttackAnalyzer;
import uam.azc.madsi.af.managers.ChangeWindow;
import uam.azc.madsi.af.managers.DataAnalyzer;
import uam.azc.madsi.af.managers.Visualizer;
import uam.azc.madsi.af.models.Attack;

/**
 * FXML Controller class
 *
 * @author vekz
 */
public class FXMLMainController implements Initializable {

    @FXML public Label lbAtaques;
    @FXML public Label lbIntentos;
    @FXML public Label lbIPs;
    @FXML public Label lbUsuarios;
    @FXML public Label lbPaises;
    
    @FXML public Label lbMostIP;
    @FXML public Label lbMostUsuario;
    @FXML public Label lbMostPais;
    
    @FXML public Label lbMostNIP;
    @FXML public Label lbMostNUsuario;
    @FXML public Label lbMostNPais;
    
    @FXML public RadioButton rbTipoAtaque;
    @FXML public RadioButton rbIdAtaque;
    
    @FXML public ChoiceBox cbTipoAtaque;
    @FXML public ChoiceBox cbIdAtaque;
    @FXML public ChoiceBox cbValueStude;
    
    @FXML public TextField tfTiempoHumano;
    @FXML public Slider slTiempoHumano;
    
    @FXML public TextField tfIp;
    @FXML public TextField tfUsuario;
    @FXML public TextField tfPais;
    
    @FXML public RadioButton rbIp;
    @FXML public RadioButton rbUsuario;
    @FXML public RadioButton rbPais;
    
    @FXML public TextArea taConsole;
   
    public ToggleGroup tgTypeSearch;
    public ToggleGroup tgSearch;
    
    private Visualizer visualizer;
    private ArrayList<Attack> _attacks;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        initLabels();
        initChoiceBox();
        initRadioButtons();
        
        _attacks = new ArrayList<>();
        visualizer = new Visualizer();
        
        slTiempoHumano.valueProperty().addListener( new ChangeListener<Number>(){
            public void changed(ObservableValue <? extends Number > observable, Number oldValue, Number newValue){
                tfTiempoHumano.setText(newValue+"");
            }
        });
    }    
    
    public void initRadioButtons(){
        tgSearch = new ToggleGroup();
        rbIp.setToggleGroup(tgSearch);
        rbIp.setUserData("ip");
        rbIp.setSelected(true);
        
        rbUsuario.setToggleGroup(tgSearch);
        rbUsuario.setUserData("user");
        
        rbPais.setToggleGroup(tgSearch);
        rbPais.setUserData("pais");
        
        tgTypeSearch = new ToggleGroup();
        rbTipoAtaque.setToggleGroup(tgTypeSearch);
        rbTipoAtaque.setUserData("tipoAtaque");
        rbTipoAtaque.setSelected(true);
        
        rbIdAtaque.setToggleGroup(tgTypeSearch);
        rbIdAtaque.setUserData("idAtaque");        
    }
    
    public void initChoiceBox(){
        AttackAnalyzer attackAnalyzer = AttackAnalyzer.getInstance();

            
        cbTipoAtaque.getItems().add("Todos");
        cbTipoAtaque.getItems().add("DDoS");
        cbTipoAtaque.getItems().add("BruteForce");
        cbTipoAtaque.getItems().add("Dictionary");
        cbTipoAtaque.getSelectionModel().select(1);
        
        for(Attack attack: attackAnalyzer.getAttacks())
            cbIdAtaque.getItems().add(attack.getAttackId());
        cbIdAtaque.getSelectionModel().select(0);
    
        cbValueStude.getItems().add("Todos");
        cbValueStude.getItems().add("IPs");
        cbValueStude.getItems().add("Usuarios");
        cbValueStude.getItems().add("Paises");
        cbValueStude.getSelectionModel().select(1);

    }
    
    
    public void initLabels(){
        Visualizer visualizer = new Visualizer();
        ArrayList<String> statistics = visualizer.getGeneralStatistics();
        
        lbAtaques.setText(statistics.get(0));
        lbIntentos.setText(statistics.get(1));
        lbIPs.setText(statistics.get(2));
        lbUsuarios.setText(statistics.get(3));
        lbPaises.setText(statistics.get(4));
        
        lbMostIP.setText(statistics.get(5));
        lbMostNIP.setText(statistics.get(6));
        lbMostUsuario.setText(statistics.get(7));
        lbMostNUsuario.setText(statistics.get(8));
        lbMostPais.setText(statistics.get(9));
        lbMostNPais.setText(statistics.get(10));
    }
    
    @FXML
    private void handleGenerarAction(ActionEvent event) throws IOException {
        _attacks.clear();
        
        StringBuilder sbAttack = new StringBuilder();
        String attackStude = (String) cbValueStude.getValue();

        String attackTypeSelection = (String)tgTypeSearch.getSelectedToggle().getUserData(); // tipoAtaque odAtaque
        if(attackTypeSelection.equalsIgnoreCase("tipoAtaque")){
            String attackType = (String) cbTipoAtaque.getValue();
            
            if(attackType.equalsIgnoreCase("Todos")){
                for(Attack it: AttackAnalyzer.getInstance().getAttacks()){
                    _attacks.add(it);
                    sbAttack.append(it.toString());
                }
            } else {
                ArrayList<Attack> attacks = AttackAnalyzer.getInstance().getAttacksOfType(attackType);
                for(Attack it: attacks){
                    _attacks.add(it);
                    sbAttack.append(it.toString());
                }
            }
            
        } else {
            int attackId = cbIdAtaque.getSelectionModel().getSelectedIndex();
            _attacks.add(AttackAnalyzer.getInstance().getAttack(attackId));
            sbAttack.append(_attacks.get(0).toString());
        }
        
        taConsole.clear();
        taConsole.setText(sbAttack.toString());
    }
    
    @FXML
    private void handleBuscarAction(ActionEvent event) throws IOException {
        
        String attackSearch = (String) tgSearch.getSelectedToggle().getUserData();
        String description = tfIp.getText();
        for(Attack it: _attacks){
            switch(attackSearch){
                case "ip": 
                    for(String _ip: it.getIPs())
                        if(_ip.equalsIgnoreCase(description))
                    break;
                case "user": break;
                case "pais": break;
            }
        }
    }
    
    @FXML
    private void handleGraficarAction(ActionEvent event) throws IOException {
                
        String attackStude = (String) cbValueStude.getValue();
        String attackSearch = (String) tgSearch.getSelectedToggle().getUserData();
        String attackTypeSelection = (String)tgTypeSearch.getSelectedToggle().getUserData(); // tipoAtaque odAtaque

        String attackType = (String) cbTipoAtaque.getValue();           
        if(attackType.equalsIgnoreCase("Todos")){
            if(attackStude.equalsIgnoreCase("Todos")){
                visualizer.graphicAllIPs();
                visualizer.graphicAllUsers();
                visualizer.graphicAllCountries();
            } else {
                switch(attackSearch){
                    case "IPs":      visualizer.graphicAllIPs();        break;
                    case "Usuarios": visualizer.graphicAllUsers();      break;
                    case "Paises":   visualizer.graphicAllCountries();  break;
                }
            }
        } else {
            if(attackStude.equalsIgnoreCase("Todos")){
                visualizer.graphicAllIPsType(attackType);
                visualizer.graphicAllUsersType(attackType);
                visualizer.graphicAllCountriesType(attackType);
            } else {
                switch(attackSearch){
                    case "IPs":      visualizer.graphicAllIPsType(attackType);       break;
                    case "Usuarios": visualizer.graphicAllUsersType(attackType);     break;
                    case "Paises":   visualizer.graphicAllCountriesType(attackType); break;
                }
            }
        }
    }
    
    @FXML
    private void handleRegresarAction(ActionEvent event) throws IOException {
        ChangeWindow changeWindow = new ChangeWindow();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        changeWindow.change("FXMLArchive.fxml", stage);  
    }
    
    @FXML
    private void handleSalirAction(ActionEvent event) throws IOException {
        Platform.exit();
        System.exit(0);
    }
}
