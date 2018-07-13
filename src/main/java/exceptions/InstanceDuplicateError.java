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


public class InstanceDuplicateError implements MontiCoreException {
  private Pane tmpPane; 
  private MontiArcPlugInErrors.ExceptionType type = MontiArcPlugInErrors.ExceptionType.INSTANCES_DUPLICATE;
  private AbstractNode tmpNode;
  private AbstractNodeView tmpNodeView;
  private AbstractNode tmpNode1;
  private AbstractNodeView tmpNodeView1;
  
  public InstanceDuplicateError(AbstractNode node, AbstractNodeView nodeView, AbstractNode node1, AbstractNodeView nodeView1)
  {
    this.tmpNode = node;
    this.tmpNodeView = nodeView;
    this.tmpNode1 = node1;
    this.tmpNodeView1 = nodeView1;
 
    setPane();
  }
  @Override
  public String getContentMessage() {
    return "There are two identical instance names";
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
    this.tmpNodeView1.setSelected(true);
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
        InstanceDuplicateError.this.handleActionClickOnPane();
      }
    });
  }
  
}
