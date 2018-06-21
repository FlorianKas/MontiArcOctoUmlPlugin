package exceptions;

import de.se_rwth.commons.logging.Finding;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.nodes.AbstractNode;
import model.nodes.Node;
import plugin.MontiCoreException;
import view.nodes.AbstractNodeView;


public class genTypeWrongParam implements MontiCoreException {
  private Pane tmpPane; 
  private MontiArcPlugInErrors.ExceptionType type = MontiArcPlugInErrors.ExceptionType.GEN_TYPE_WRONG_PARAM_EXCEPTION;
  private String content ="";
  private String contentMessage;
  
  public genTypeWrongParam(String string)
  {
    this.content = string;
    this.contentMessage =string;
    setPane();
  }
  @Override
  public String getContentMessage() {
    return "Param " + contentMessage;
  }

  @Override
  public Pane getContentPane() {
    return this.tmpPane;
  }

  @Override
  public AbstractNode getNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void handleActionClickOnPane() {
    // TODO Auto-generated method stub
    
  }
  public MontiArcPlugInErrors.ExceptionType getType() {
    return type;
  }
  public void setType(MontiArcPlugInErrors.ExceptionType type) {
    this.type = type;
  }
  
  private void setPane() {
    this.tmpPane = new Pane();
    Label label = new Label(this.getContentMessage());
    this.tmpPane.getChildren().add(label);
    this.tmpPane.setOnMouseClicked(new EventHandler() {
      @Override
      public void handle(Event arg0) {
        genTypeWrongParam.this.handleActionClickOnPane();
      }
    });
  }
  
}
