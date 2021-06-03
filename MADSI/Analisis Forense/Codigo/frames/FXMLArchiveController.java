/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.frames;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uam.azc.madsi.af.managers.AttackAnalyzer;
import uam.azc.madsi.af.managers.ChangeWindow;
import uam.azc.madsi.af.managers.DataAnalyzer;
import uam.azc.madsi.af.managers.RegisterManager;
import uam.azc.madsi.af.managers.ServerConfig;

/**
 *
 * @author vekz
 */
public class FXMLArchiveController implements Initializable {
    
    @FXML private TextField tfRoute;
    @FXML private Label lbError;
    @FXML private Button btnProc;
    
    @FXML
    private void handleProccessAction(ActionEvent event) throws IOException {
        String route = tfRoute.getText();
        
        RegisterManager regManager = new RegisterManager();
        regManager.setBufferSize(8000);
        regManager.loadRegisters(route);
        
        if(regManager.success()){
            lbError.setStyle("color: rgb(255,0,0)");
            lbError.setText("Cargando registros...");
            
            DataAnalyzer dataAnalyzer = new DataAnalyzer();
            dataAnalyzer.analizeLog(regManager.getRegisters());
            
            AttackAnalyzer attack = AttackAnalyzer.getInstance();
            attack.setRegistersData(dataAnalyzer.getRegisters());
            attack.setTimeAttack(60000);
            attack.analyzeAttacks();
            
            ChangeWindow changeWindow = new ChangeWindow();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            changeWindow.change("FXMLMain.fxml", stage);
        } else{
            lbError.setText("No existe el archivo o esta corrupto.");
            lbError.setVisible(true);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
