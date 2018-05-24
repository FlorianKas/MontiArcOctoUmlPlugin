package controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.nodes.AbstractNode;
import model.nodes.ClassNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;

/**
 * Dialog to edit details of a node.
 *
 * @author Marco Jakob
 */
public class NodeEditDialogControllerMonti extends NodeEditDialogController{
  
  @FXML
  private TextField titleField;
  @FXML
  private TextField typeField;
  @FXML
  private TextField nameField;
  @FXML
  private TextField stereotypeField;
  @FXML
  private TextField dataTypeField;
  @FXML
  private TextArea attributesArea;
  @FXML
  private TextArea operationsArea;
  @FXML
  private Button okButton;
  @FXML
  private Button cancelButton;
  
  private AbstractNode node;
  private boolean okClicked = false;
  
  /**
   * Initializes the controller class. This method is automatically called after
   * the classDiagramView.fxml file has been loaded.
   */
  @FXML
  private void initialize() {
    
  }
  
  /**
   * Sets the node to be edited in the dialog.
   *
   * @param node
   */
  public void setNode(AbstractNode node) {
    if (node instanceof ClassNode) {
      this.node = (ClassNode) node;
    
      titleField.setText(this.node.getTitle());
      attributesArea.setText(((ClassNode)this.node).getAttributes());
      operationsArea.setText(((ClassNode)this.node).getOperations());
    }
    else if (node instanceof ComponentNode) {
      this.node = (ComponentNode) node;
      nameField.setText(((ComponentNode)this.node).getTitle());
      typeField.setText(((ComponentNode)this.node).getComponentType());
      stereotypeField.setText(((ComponentNode)this.node).getStereotype());
    }
    else if (node instanceof PortNode) {
      this.node = (PortNode) node;
      titleField.setText(this.node.getTitle());
      System.out.println("Typ of PortNode "+ ((PortNode)this.node).getPortType());
      System.out.println("dataTypeField " + dataTypeField.getText());
      
      dataTypeField.setText(((PortNode)this.node).getPortType());
      }
  }
  
  public Button getOkButton() {
    return okButton;
  }
  
  public Button getCancelButton() {
    return cancelButton;
  }
  
  public String getTitle() {
    return titleField.getText();
  }
  
  public String getType() {
    return typeField.getText();
  }
  
  public String getName() {
    return nameField.getText();
  }
  
  public String getStereotype() {
    return stereotypeField.getText();
  }
  
  public String getAttributes() {
    return attributesArea.getText();
  }
  
  public String getOperations() {
    return operationsArea.getText();
  }
  
  public String getDataType() {
    return dataTypeField.getText();
  }
  public boolean hasTitledChanged() {
    if (this.node.getTitle() == null) {
      return titleField.getText() != null;
    }
    else {
      return !this.node.getTitle().equals(titleField.getText());
    }
  }
  
  public boolean hasAttributesChanged() {
    if (((ClassNode)this.node).getAttributes() == null) {
      return attributesArea.getText() != null;
    }
    else {
      return !((ClassNode)this.node).getAttributes().equals(attributesArea.getText());
    }
  }
  
  public boolean hasOperationsChanged() {
    if (((ClassNode)this.node).getOperations() == null) {
      return operationsArea.getText() != null;
    }
    else {
      return !((ClassNode)this.node).getOperations().equals(operationsArea.getText());
    }
  }
  
  public boolean hasDataTypeChanged() {
    if (((PortNode)this.node).getPortType() == null) {
      return dataTypeField.getText() != null;
    }
    else {
      return !((PortNode)this.node).getPortType().equals(dataTypeField.getText());
    }
  }
  
  public boolean hasNameChanged() {
    if (((ComponentNode)this.node).getTitle() == null) {
      return nameField.getText() != null;
    }
    else {
      return !((ComponentNode)this.node).getTitle().equals(nameField.getText());
    }
  }
  
  public boolean hasTypeChanged() {
    if (((ComponentNode)this.node).getComponentType() == null) {
      return typeField.getText() != null;
    }
    else {
      return !((ComponentNode)this.node).getComponentType().equals(typeField.getText());
    }
  }
  
  public boolean hasStereotypeChanged() {
    if (((ComponentNode)this.node).getStereotype() == null) {
      return stereotypeField.getText() != null;
    }
    else {
      return !((ComponentNode)this.node).getStereotype().equals(stereotypeField.getText());
    }
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
   * Called when the user clicks ok.
   */
  private void handleOk() {
    if (isInputValid()) {
      okClicked = true;
    }
  }
  
  /**
   * Called when the user clicks cancel.
   */
  
  private void handleCancel() {
  }
  
  /**
   * Validates the user input in the text fields.
   *
   * @return true if the input is valid
   */
  private boolean isInputValid() {
    return true; // Use if we want to
  }
}