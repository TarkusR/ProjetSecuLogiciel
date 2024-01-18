package fr.isep.projetseculogiciel;
// package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

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
        String textFieldValue = customTextField.getText();

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

            // Si la CheckBox est sélectionnée, effectuer l'action correspondante
            if (isSelected) {
                if (checkBox == f1) {
                    // TODO
//                    F1Controller bruteForce = new F1Controller(textFieldValue);
                    System.out.println("F1 selected. Perform action for F1.");
                } else if (checkBox == f2) {
                    // TODO
//                    F2Controller sqlInjection = new F2Controller(textFieldValue);
                    System.out.println("F2 selected. Perform action for F2.");
                } else if (checkBox == f3) {
                    F3Controller xssAttack = new F3Controller(textFieldValue);
                    xssAttack.performWebAutomation();
                    // Action spécifique pour f3
                    System.out.println("F3 selected. Perform action for F3.");
                } else if (checkBox == f4) {
                    // TODO
//                    F4Controller f4 = new F4Controller(textFieldValue);
                    System.out.println("F4 selected. Perform action for F4.");
                } else if (checkBox == f5) {
//                    F5Controller f5 = new F5Controller(textFieldValue);
                    System.out.println("F5 selected. Perform action for F5.");
                } else if (checkBox == f6) {
//                    F6Controller f6 = new F6Controller(textFieldValue);

                    System.out.println("F6 selected. Perform action for F6.");
                }
                // Ajoutez d'autres blocs else if si nécessaire pour les CheckBox supplémentaires
            }
        }
    }
}
