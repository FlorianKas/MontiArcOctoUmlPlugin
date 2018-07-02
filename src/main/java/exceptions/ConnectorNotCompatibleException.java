package exceptions;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.edges.AbstractEdge;
import model.edges.Edge;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.Node;
import plugin.MontiCoreException;
import view.edges.AbstractEdgeView;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;


public class ConnectorNotCompatibleException implements MontiCoreException {
  private Pane tmpPane; 
  private MontiArcPlugInErrors.ExceptionType type = MontiArcPlugInErrors.ExceptionType.CONNECTOR_TYPE_NOT_COMPATIBEL;
  private AbstractEdge tmpEdge;
  private AbstractEdgeView tmpEdgeView;
  
  public ConnectorNotCompatibleException(Edge edge, AbstractEdgeView edgeView)
  {
    this.tmpEdge = (AbstractEdge) edge;
    this.tmpEdgeView = edgeView;
    setPane();
  }
  @Override
  public String getContentMessage() {
    return "Connector Types are not compatible";
  }

  @Override
  public Pane getContentPane() {
    return this.tmpPane;
  }

  public AbstractEdge getEdge() {
    return this.tmpEdge;
  }
  
  public void setEdge(AbstractEdge pTmpEdge) {
    this.tmpEdge = pTmpEdge;
  }
  

  @Override
  public void handleActionClickOnPane() {
    this.tmpEdgeView.setSelected(true);
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
        ConnectorNotCompatibleException.this.handleActionClickOnPane();
      }
    });
  }
  @Override
  public AbstractNode getNode() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
