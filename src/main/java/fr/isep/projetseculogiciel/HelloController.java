package fr.isep.projetseculogiciel;

// package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

//todo : test if the value of checkbox are correctly selected
public class HelloController {

    @FXML
    private CheckBox f1;

    @FXML
    private CheckBox f2;

    @FXML
    private CheckBox f3;

    @FXML
    private CheckBox f4;

    @FXML
    private CheckBox f5;

    @FXML
    private CheckBox f6;

    @FXML
    private TextField customTextField;

    @FXML
    private void startAttacks() {
        // Récupérer la valeur du TextField
        String url = customTextField.getText();

        // Créer une liste de CheckBox
        List<CheckBox> checkBoxList = new ArrayList<>();
        checkBoxList.add(f1);
        checkBoxList.add(f2);
        checkBoxList.add(f3);
        checkBoxList.add(f4);
        checkBoxList.add(f5);
        checkBoxList.add(f6);

        // Parcourir la liste des CheckBox
        for (CheckBox checkBox : checkBoxList) {
            // Récupérer l'état de la CheckBox
            boolean isSelected = checkBox.isSelected();

            //  TODO  : START A NE INSTANCE OF EACH FEATURE FOR EACH CHECKBOX SELECTED 
            if (isSelected) {
                // Instancier la classe F6 en lui passant l'URL (ou d'autres informations) en paramètre
                F6 f6Automation = new F6(url);
                f6Automation.performWebAutomation();
            }
        }
    }
}
