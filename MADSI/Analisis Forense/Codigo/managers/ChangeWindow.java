/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uam.azc.madsi.af.managers;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author vekz
 */
public class ChangeWindow {
    
    public void change(String route, Stage stage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/uam/azc/madsi/af/frames/"+route)); 
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    
}
