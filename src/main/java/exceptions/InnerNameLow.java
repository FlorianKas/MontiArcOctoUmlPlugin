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


public class InnerNameLow implements MontiCoreException {
  private Pane tmpPane; 
  private MontiArcPlugInErrors.ExceptionType type = MontiArcPlugInErrors.ExceptionType.INNER_COMPONENT_LOW;
  private AbstractNode tmpNode;
  private AbstractNodeView tmpNodeView;
  
  public InnerNameLow(AbstractNode node, AbstractNodeView nodeView)
  {
    this.tmpNode = node;
    this.tmpNodeView = nodeView;
    setPane();
  }
  @Override
  public String getContentMessage() {
    return "Name of inner Component must start with capital letter";
  }

  @Override
  public Pane getContentPane() {
    return this.tmpPane;
  }

  @Override
  public AbstractNode getNode() {
    return this.tmpNode;
  }
  
  public void setNode(AbstractNode pTmpNode) {
    this.tmpNode = pTmpNode;
  }
  
  public void setNodeView(AbstractNodeView pTmpNodeView) {
    this.tmpNodeView = pTmpNodeView;
  }
  

  @Override
  public void handleActionClickOnPane() {
    this.tmpNodeView.setSelected(true);
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
        InnerNameLow.this.handleActionClickOnPane();
      }
    });
  }
  
}
