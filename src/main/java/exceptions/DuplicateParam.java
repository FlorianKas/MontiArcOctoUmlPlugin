package exceptions;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.Node;
import plugin.MontiCoreException;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;


public class DuplicateParam implements MontiCoreException {
  private Pane tmpPane; 
  private MontiArcPlugInErrors.ExceptionType type = MontiArcPlugInErrors.ExceptionType.DUPLICATE_PARAM;
  
  public DuplicateParam()
  {
    setPane();
  }
  @Override
  public String getContentMessage() {
    return "Duplicated Configuration Parameter";
  }

  @Override
  public Pane getContentPane() {
    return this.tmpPane;
  }

  @Override
  public AbstractNode getNode() {
    return null;
  }
  
  public void setNode(AbstractNode pTmpNode) {
    
  }
  

  @Override
  public void handleActionClickOnPane() {
    
  }
  public MontiArcPlugInErrors.ExceptionType getType() {
    return type;
  }
  public void setType(MontiArcPlugInErrors.ExceptionType type) {
    this.type = type;
  }
  
  private void setPane() {
    this.tmpPane = new Pane();
    Label label = new Label(getContentMessage());
    this.tmpPane.getChildren().add(label);
    this.tmpPane.setOnMouseClicked(new EventHandler() {
      @Override
      public void handle(Event arg0) {
        DuplicateParam.this.handleActionClickOnPane();
      }
    });
  }
  
}
