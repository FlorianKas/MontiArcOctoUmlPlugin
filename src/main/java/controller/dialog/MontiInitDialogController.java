package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.nodes.Infos;

/**
 * Created by chalmers on 2016-08-22.
 */
public class MontiInitDialogController {
  private Stage dialogStage;
  
  private boolean okClicked = false;
  private Infos in;
  
  @FXML
  public TextField nameTextField, arcParameterTextField, genericsTextField, packageTextField, importTextField;
  
  @FXML
  private Button okButton;
  @FXML
  private Button cancelButton;
  @FXML
  public void initialize() {
    
  }
  
  
  /**
   * Called when the user clicks ok.
   */
  @FXML
  public void handleOk() {
    if (isInputValid()) {
      okClicked = true;
      dialogStage.close();
    }
  }
  
  /**
   * Called when the user clicks cancel.
   */
  @FXML
  public void handleCancel() {
    dialogStage.close();
  }
  
  public void set(Infos i) {
    this.in = i;
    nameTextField.setText(i.getName());
    arcParameterTextField.setText(i.getArcParam());
    genericsTextField.setText(i.getGenerics());
    packageTextField.setText(i.getPackageName());
    importTextField.setText(i.getImports());
  }
  
  public Infos get() {
    return this.in;
  }
  public Button getOkButton() {
    return okButton;
  }
  
  public Button getCancelButton() {
    return cancelButton;
  }
  
  /**
   * Called when the user clicks ok.
   */
  

  // TODO
  private boolean isInputValid() {
    return true;
  }
  
  public String getName() {
    return nameTextField.getText();
  }
  
  public String getPackageName() {
    return packageTextField.getText();
  }
  
  public String getImport() {
    return importTextField.getText();
  }
  
  public String getGeneric() {
    return genericsTextField.getText();
  }
  
  public String getArcParam() {
    return arcParameterTextField.getText();
  }
  
  
  /**
   * Returns true if the user clicked OK, false otherwise.
   * 
   * @return
   */
  public boolean isOkClicked() {
    return okClicked;
    
  }
  
  public boolean hasNameChanged() {
    System.out.println("this.in " + this.in.toString());
    if (this.in.getName() == null) {
      return nameTextField.getText() != null;
    }
    else {
      return !this.in.getName().equals(nameTextField.getText());
    }
  }
  
  public boolean hasPackageChanged() {
    if (this.in.getPackageName() == null) {
      return packageTextField.getText() != null;
    }
    else {
      return !this.in.getPackageName().equals(packageTextField.getText());
    }
  }
  public boolean hasImportChanged() {
    if (this.in.getImports() == null) {
      return importTextField.getText() != null;
    }
    else {
      return !this.in.getImports().equals(importTextField.getText());
    }
  }
  public boolean hasGenericChanged() {
    if (this.in.getImports() == null) {
      return importTextField.getText() != null;
    }
    else {
      return !this.in.getImports().equals(importTextField.getText());
    }
  }
  public boolean hasArcParamChanged() {
    if (this.in.getArcParam() == null) {
      return arcParameterTextField.getText() != null;
    }
    else {
      return !this.in.getArcParam().equals(arcParameterTextField.getText());
    }
  }

  public void setDialogStage(Stage dialogStage) {
    // TODO Auto-generated method stub
    this.dialogStage = dialogStage;
  }
  
  
  
  
}
