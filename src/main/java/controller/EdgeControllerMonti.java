package controller;

import controller.dialog.EdgeEditDialogController;
import controller.dialog.MessageEditDialogController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.edges.*;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import util.commands.DirectionChangeEdgeCommand;
import util.commands.MoveMessageCommand;
import util.commands.ReplaceEdgeCommand;
import view.edges.AbstractEdgeView;
import view.edges.MessageEdgeView;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PortNodeView;
import view.nodes.SequenceObjectView;

import java.io.IOException;

/**
 * Controller class for Edges.
 */
public class EdgeControllerMonti extends EdgeController {
  private double dragStartX, dragStartY;
  private Line dragLine;
  private Pane aDrawPane;
  private AbstractDiagramController diagramController;
  private AbstractNodeView startNodeView;
  private AbstractNodeView endNodeView;
  
  public EdgeControllerMonti(Pane pDrawPane, MontiArcController diagramController) {
    super(pDrawPane,diagramController);
    aDrawPane = pDrawPane;
    dragLine = new Line();
    dragLine.setStroke(Color.DARKGRAY);
    dragLine.setStrokeWidth(2);
    this.diagramController = diagramController;
  }
  
  public void onMousePressedOnNode(MouseEvent event) {
    double tmp = event.getX() + ((AbstractNodeView) event.getSource()).getTranslateX() + 1.5*((ComponentNodeView) event.getSource()).getPortNodeViews().get(0).getPortWidth();
    
    ComponentNodeView tmpView = (ComponentNodeView) event.getSource();
    System.out.println("Start at position x" + tmpView.getTranslateX());
    System.out.println("event get x" + event.getX());
    
    for (PortNodeView pView : tmpView.getPortNodeViews()) {
      System.out.println("port getX" + pView.getPortX());
      if (event.getX() + tmpView.getTranslateX() > pView.getPortX() + 0.5*pView.getPortWidth() 
          && event.getX() + tmpView.getTranslateX()  < pView.getPortX() + 1.5*pView.getPortWidth()) {
        System.out.println("Test");
        dragStartX = pView.getX() + 1.5*pView.getPortWidth();
        dragStartY = pView.getY() + 0.5*pView.getPortHeight(); 
        startNodeView = pView;
      }
    }
    aDrawPane.getChildren().add(dragLine);
  }
  
//  public void onMousePressedOnCanvas(MouseEvent event) {
//    dragStartX = event.getX();
//    dragStartY = event.getY();
//    if (diagramController instanceof SequenceDiagramController) {
//      for (AbstractNodeView node : diagramController.getAllNodeViews()) {
//        if (node instanceof PortNodeView) {
//          double middleOfNode = (node.getX() + (node.getX() + node.getWidth())) / 2;
//          if (event.getX() > middleOfNode - 20 && event.getX() < middleOfNode + 20 && event.getY() > node.getY()) {
//            startNodeView = node;
//          }
//        }
//      }
//    }
//    aDrawPane.getChildren().add(dragLine);
//  }
    
  public void onMouseDragged(MouseEvent event) {
    dragLine.setStartX(dragStartX);
    dragLine.setStartY(dragStartY);
    
    if (event.getSource() instanceof AbstractNodeView) {
      dragLine.setEndX(event.getX() + ((AbstractNodeView) event.getSource()).getTranslateX());
      dragLine.setEndY(event.getY() + ((AbstractNodeView) event.getSource()).getTranslateY());
      System.out.println("dargLine X " + dragLine.getEndX());
      System.out.println("dargLine Y " + dragLine.getEndY());
    }
    else {
      dragLine.setEndX(event.getX());
      dragLine.setEndY(event.getY());
    }
  }
  
  
  /*
   * Used for MA diagrams
   */
  public void onMouseReleasedRelation() {
    for (AbstractNodeView nodeView : diagramController.getAllNodeViews()) {
      System.out.println("Endpoint " + getEndPoint());
      System.out.println("nodeView " + nodeView.getTranslateX() + nodeView.getTranslateY());
      if(nodeView.contains(getEndPoint())){
        endNodeView = nodeView;
        System.out.println("Found a nodeView");
        System.out.println("endNodeView " + endNodeView.getTranslateX() + endNodeView.getTranslateY());
      }
    }
    PortNode endNode = new PortNode();
    PortNode startNode = new PortNode();
    ComponentNode endCompNode = new ComponentNode();
    ComponentNode startCompNode = new ComponentNode();
    if (endNodeView != null && endNodeView instanceof PortNodeView) {
      endNode = (PortNode) diagramController.getNodeMap().get(endNodeView);
      endCompNode = endNode.getComponentNode();
      startNode = (PortNode) diagramController.getNodeMap().get(startNodeView);
      startCompNode = startNode.getComponentNode();
      ConnectorEdge edge = new ConnectorEdge(startNode, endNode);
      System.out.println("Edge " + edge.toString());
      diagramController.createEdgeView(edge, startNodeView, endNodeView);
    } 
    else if (endNodeView != null) {
      System.out.println("We have a ComponentNodeView as recognized");
      System.out.println("endNodeView " + endNodeView.getX() + endNodeView.getY() + endNodeView.getWidth() + endNodeView.getHeight());
      for (PortNodeView pView: ((ComponentNodeView) endNodeView).getPortNodeViews()) {
        System.out.println("pView " + pView.getX() + pView.getY() + pView.getWidth() + pView.getHeight());
        
        if(pView.getPortX() <= getEndPoint().getX() && pView.getPortX() + pView.getPortWidth() >= getEndPoint().getX()
            && pView.getPortY() <= getEndPoint().getY() && pView.getPortY() + pView.getPortHeight() >= getEndPoint().getY()) {
          endNode = (PortNode)diagramController.getNodeMap().get(pView);
          endCompNode = endNode.getComponentNode();
          startNode = (PortNode) diagramController.getNodeMap().get(startNodeView);
          startCompNode = startNode.getComponentNode();
          ConnectorEdge edge = new ConnectorEdge(startNode, endNode);
          diagramController.createEdgeView(edge, startNodeView, endNodeView);
          System.out.println("Found a endNode");
        }
      }
    }
    else {
      System.out.println("There is no EndPoint");
    }
    finishCreateEdge();
  }
  
  private void finishCreateEdge() {
    dragLine.setStartX(0);
    dragLine.setStartY(0);
    dragLine.setEndX(0);
    dragLine.setEndY(0);
    aDrawPane.getChildren().remove(dragLine);
    startNodeView = null;
    endNodeView = null;
  }
  
  private double previousEdgeStartY;
  private double dragStart;
  
  protected void onMousePressDragEdge(MouseEvent event) {
    diagramController.mode = AbstractDiagramController.Mode.DRAGGING_EDGE;
    previousEdgeStartY = event.getY();
    dragStartY = event.getY();
  }
  
  protected void onMouseDragEdge(MouseEvent event) {
    double offsetY = (event.getY() - previousEdgeStartY) * (1 / diagramController.drawPane.getScaleY());
    previousEdgeStartY = event.getY();
    for (AbstractEdgeView edgeView : diagramController.selectedEdges) {
      if (edgeView instanceof MessageEdgeView) {
        MessageEdge edge = (MessageEdge) edgeView.getRefEdge();
        edge.setStartY(edge.getStartY() + offsetY);
      }
    }
  }
  
  protected void onMouseReleaseDragEdge(MouseEvent event) {
    diagramController.mode = AbstractDiagramController.Mode.NO_MODE;
    for (AbstractEdgeView edgeView : diagramController.selectedEdges) {
      diagramController.undoManager.add(new MoveMessageCommand((MessageEdge) edgeView.getRefEdge(), 0, edgeView.getStartY() - dragStartY));
    }
    previousEdgeStartY = 0;
  }
  
  public Point2D getStartPoint() {
    return new Point2D(dragStartX, dragStartY);
  }
  
  public Point2D getEndPoint() {
    return new Point2D(dragLine.getEndX(), dragLine.getEndY());
  }
  
  public boolean showEdgeEditDialog(AbstractEdge edge) {
    System.out.println("We are here");
    System.out.println("edge is of type connectoredge" + (edge instanceof ConnectorEdge));
    if (!(edge instanceof ConnectorEdge)) {
      try {
        // Load the classDiagramView.fxml file and create a new stage for the
        // popup
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/edgeEditDialog.fxml"));
        AnchorPane dialog = loader.load();
        dialog.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
        dialog.setStyle("-fx-border-color: black");
        // Set location for "dialog".
        dialog.setLayoutX((edge.getStartNode().getTranslateX() + edge.getEndNode().getTranslateX()) / 2);
        dialog.setLayoutY((edge.getStartNode().getTranslateY() + edge.getEndNode().getTranslateY()) / 2);
        
        EdgeEditDialogController controller = loader.getController();
        controller.setEdge(edge);
        ChoiceBox directionBox = controller.getDirectionBox();
        ChoiceBox typeBox = controller.getTypeBox();
        controller.getOkButton().setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            // If no change in type of edge we just change direction of old edge
            // if(typeBox.getValue().equals(edge.getType()) || typeBox.getValue()
            // == null){
            //
            // edge.setStartMultiplicity(controller.getStartMultiplicity());
            // edge.setEndMultiplicity(controller.getEndMultiplicity());
            // edge.setLabel(controller.getLabel());
            // if (directionBox.getValue() != null) {
            // diagramController.getUndoManager().add(new
            // DirectionChangeEdgeCommand(edge, edge.getDirection(),
            // AbstractEdge.Direction.valueOf(directionBox.getValue().toString())));
            // edge.setDirection(AbstractEdge.Direction.valueOf(directionBox.getValue().toString()));
            // }
            //
            //
            // } else { //Else we create a new one to replace the old
            
            // solved the bug of change multiplicity and edge type in remote
            // collaboration setting
            AbstractEdge newEdge = null;
            if (typeBox.getValue().equals("Inheritance")) {
              newEdge = new InheritanceEdge(edge.getStartNode(), edge.getEndNode());
            }
            else if (typeBox.getValue().equals("Association") || typeBox.getValue() == null) {
              newEdge = new AssociationEdge(edge.getStartNode(), edge.getEndNode());
            }
            else if (typeBox.getValue().equals("Aggregation")) {
              newEdge = new AggregationEdge(edge.getStartNode(), edge.getEndNode());
            }
            else if (typeBox.getValue().equals("Composition")) {
              newEdge = new CompositionEdge(edge.getStartNode(), edge.getEndNode());
            }
            newEdge.setDirection(AbstractEdge.Direction.valueOf(directionBox.getValue().toString()));
            newEdge.setStartMultiplicity(controller.getStartMultiplicity());
            newEdge.setEndMultiplicity(controller.getEndMultiplicity());
            newEdge.setLabel(controller.getLabel());
            replaceEdge(edge, newEdge);
            
            aDrawPane.getChildren().remove(dialog);
            diagramController.removeDialog(dialog);
          }
        });
        controller.getCancelButton().setOnAction(event -> {
          aDrawPane.getChildren().remove(dialog);
          diagramController.removeDialog(dialog);
        });
        diagramController.addDialog(dialog);
        aDrawPane.getChildren().add(dialog);
        
        return controller.isOkClicked();
        
      }
      catch (IOException e) {
        // Exception gets thrown if the classDiagramView.fxml file could not be
        // loaded
        e.printStackTrace();
        return false;
      }
    }
    else {
      return false;
    }
  }
  
  public boolean showMessageEditDialog(MessageEdge edge) {
    try {
      // Load the classDiagramView.fxml file and create a new stage for the
      // popup
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/messageEditDialog.fxml"));
      AnchorPane dialog = loader.load();
      dialog.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
      dialog.setStyle("-fx-border-color: black");
      // Set location for "dialog".
      if (edge.getStartNode() != null) {
        dialog.setLayoutX((edge.getStartNode().getTranslateX() + edge.getEndNode().getTranslateX()) / 2);
        dialog.setLayoutY((edge.getStartNode().getTranslateY() + edge.getEndNode().getTranslateY()) / 2);
      }
      else {
        dialog.setLayoutX((edge.getStartX() + edge.getEndNode().getTranslateX()) / 2);
        dialog.setLayoutY((edge.getStartY() + edge.getEndNode().getTranslateY()) / 2);
      }
      
      MessageEditDialogController controller = loader.getController();
      controller.setEdge(edge);
      ChoiceBox directionBox = controller.getDirectionBox();
      ChoiceBox typeBox = controller.getTypeBox();
      TextField titleTextField = controller.getTitleTextField();
      controller.getOkButton().setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          edge.setMessageType((MessageEdge.MessageType) typeBox.getValue());
          if (directionBox.getValue() != null) {
            diagramController.getUndoManager().add(new DirectionChangeEdgeCommand(edge, edge.getDirection(), AbstractEdge.Direction.valueOf(directionBox.getValue().toString())));
            edge.setDirection(AbstractEdge.Direction.valueOf(directionBox.getValue().toString()));
          }
          if (titleTextField.getText() != null) {
            edge.setTitle(titleTextField.getText());
          }
          aDrawPane.getChildren().remove(dialog);
          diagramController.removeDialog(dialog);
        }
      });
      controller.getCancelButton().setOnAction(event -> {
        aDrawPane.getChildren().remove(dialog);
        diagramController.removeDialog(dialog);
      });
      diagramController.addDialog(dialog);
      aDrawPane.getChildren().add(dialog);
      
      return controller.isOkClicked();
      
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean replaceEdge(AbstractEdge oldEdge, AbstractEdge newEdge) {
    AbstractEdgeView oldEdgeView = null;
    for (AbstractEdgeView edgeView : diagramController.getAllEdgeViews()) {
      if (edgeView.getRefEdge().equals(oldEdge)) {
        oldEdgeView = edgeView;
        break;
      }
    }
    if (oldEdgeView == null) {
      return false;
    }
    diagramController.deleteEdgeView(oldEdgeView, null, true, false);
    
    AbstractEdgeView newEdgeView = diagramController.createEdgeView(newEdge, oldEdgeView.getStartNode(), oldEdgeView.getEndNode());
    
    diagramController.getUndoManager().add(new ReplaceEdgeCommand(oldEdge, newEdge, oldEdgeView, newEdgeView, diagramController, diagramController.getGraphModel()));
    
    return true;
  }
}
