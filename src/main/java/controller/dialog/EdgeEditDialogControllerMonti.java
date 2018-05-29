package controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;
import model.nodes.ComponentNode;

/**
 * Dialog to edit Edge settings.
 *
 * @author Marco Jakob
 */
public class EdgeEditDialogControllerMonti extends EdgeEditDialogController{
  @FXML
  private TextField StereoType;
  @FXML
  private Button okButton;
  @FXML
  private Button cancelButton;
  
  private AbstractEdge edge;
  
  private boolean okClicked = false;
  
  /**
   * Initializes the controller class. This method is automatically called after
   * the classDiagramView.fxml file has been loaded.
   */
  @FXML
  private void initialize() {
    
  }
  
  public String getStereoType() {
    return StereoType.getText();
  }
  
  public Button getOkButton() {
    return okButton;
  }
  
  public Button getCancelButton() {
    return cancelButton;
  }
  
  
  public void setEdge(AbstractEdge edge) {
    this.edge = edge;
    // TODO Hardcoded values. Where to put them?
    StereoType.setText(((ConnectorEdge)edge).getStereoType());
  }
  
  /**
   * Returns true if the user clicked OK, false otherwise.
   * 
   * @return
   */
  public boolean isOkClicked() {
    return okClicked;
  }
  
  /**
   * Validates the user input in the text fields.
   *
   * @return true if the input is valid
   */
  private boolean isInputValid() {
    return true; // Use if we want to
  }
  
  public boolean hasStereoTypeChanged() {
    if (((ConnectorEdge)this.edge).getStereoType() == null) {
      return StereoType.getText() != null;
    }
    else {
      return !((ConnectorEdge)this.edge).getStereoType().equals(StereoType.getText());
    }
  }
}