package controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by chalmers on 2016-08-22.
 */
public class AddGenericsController {
  
  private Stage dialogStage;
  private boolean okClicked = false;
  
  @FXML
  public TextField genericsTextField;
  
  
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
  
  // TODO
  private boolean isInputValid() {
    return true;
  }
  
  /**
   * Sets the stage of this dialog.
   * 
   * @param dialogStage
   */
  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }
  
  /**
   * Returns true if the user clicked OK, false otherwise.
   * 
   * @return
   */
  public boolean isOkClicked() {
    return okClicked;
  }
  
  
  
  
}
