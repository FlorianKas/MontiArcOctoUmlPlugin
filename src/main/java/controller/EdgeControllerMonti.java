package controller;

import controller.dialog.EdgeEditDialogController;
import controller.dialog.EdgeEditDialogControllerMonti;
import controller.dialog.MessageEditDialogController;
import edu.tamu.core.sketch.BoundingBox;
//import edu.tamu.core.sketch.BoundingBox;
import edu.tamu.core.sketch.Point;
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
import model.edges.ConnectorEdge;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import util.commands.CompoundCommand;
import util.commands.DirectionChangeEdgeCommand;
import util.commands.MoveMessageCommand;
import util.commands.ReplaceEdgeCommand;
import util.commands.SetEdgeStereoTypeCommand;
import util.commands.SetNodeNameCommand;
import view.edges.AbstractEdgeView;
import view.edges.ConnectorEdgeView;
import view.edges.MessageEdgeView;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PortNodeView;
import view.nodes.SequenceObjectView;

import java.awt.Rectangle;
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
//    double tmp = event.getX() + ((AbstractNodeView) event.getSource()).getTranslateX() + 1.5*((ComponentNodeView) event.getSource()).getPortNodeViews().get(0).getPortWidth();
    startNodeView = null;
	ComponentNodeView tmpView = (ComponentNodeView) event.getSource();
    for (AbstractNodeView n : diagramController.getAllNodeViews()) {
      if (n instanceof PortNodeView) {
    	BoundingBox portBox = new BoundingBox(n.getX()+20,n.getY() - ((PortNodeView)n).getPortHeight()+40,n.getX() + ((PortNodeView)n).getPortWidth()+20,n.getY()+40);
    	Point mousePoint = new Point(tmpView.getX()+event.getX(), tmpView.getY()+event.getY());
    	System.out.println("PortBox " + portBox.getX() + " "+ portBox.getY()+" "+ portBox.getHeight()+ " "+portBox.getWidth());
    	System.out.println("MousePoint " + mousePoint.getX() +" "+ mousePoint.getY());
    	
    	if (portBox.contains(mousePoint)) {
    		
//    	if (n.getX() > tmpView.getX() + tmpView.getWidth())
    	  //    	if (event.getX() + tmpView.getTranslateX() > n.getX() + 0.5*((PortNodeView) n).getPortWidth() 
//        && event.getX() + tmpView.getTranslateX()  < n.getX() + 1.5*((PortNodeView) n).getPortWidth() &&
//        event.getY() + tmpView.getTranslateY() > n.getY() &&
//        event.getY() + tmpView.getTranslateY() < n.getY() + ((PortNodeView) n).getPortHeight()) {
          System.out.println("STARTNODEVIEW FOUND");
          dragStartX = n.getX() + 1.5*((PortNodeView) n).getPortWidth();
          dragStartY = n.getY() + 0.5*((PortNodeView) n).getPortHeight(); 
          startNodeView = n;
          System.out.println("StartNodeView is " + startNodeView);
        }  
      }
    }
//    for (PortNodeView pView : tmpView.getPortNodeViews()) {
////      System.out.println("port getX " + pView.getPortX());
////      System.out.println("port getY " + pView.getPortY());
//      System.out.println(" componentNodeView translate Y " + tmpView.getTranslateY());
//      System.out.println(" componentNodeView translate X " + tmpView.getTranslateX());
//      System.out.println(" event get Y " + event.getY());
//      System.out.println("Evenet get X " + event.getX());
//      System.out.println("Scene " + event.getSceneX());
//      System.out.println("Screne" + event.getScreenX());
//      System.out.println("pView X" + pView.getX());
//      System.out.println("pView Y" + pView.getY());
//      System.out.println("pView Height" + pView.getPortHeight());
//      System.out.println("pView Width" + pView.getPortWidth());
//      
//      
//      if (event.getX() + tmpView.getTranslateX() > pView.getX() + 0.5*pView.getPortWidth() 
//          && event.getX() + tmpView.getTranslateX()  < pView.getX() + 1.5*pView.getPortWidth() &&
//          event.getY() + tmpView.getTranslateY() > pView.getY() &&
//          event.getY() + tmpView.getTranslateY() < pView.getY() + pView.getHeight()) {
//        System.out.println("STARTNODEVIEW FOUND");
//        dragStartX = pView.getX() + 1.5*pView.getPortWidth();
//        dragStartY = pView.getY() + 0.5*pView.getPortHeight(); 
//        startNodeView = pView;
//        System.out.println("StartNodeView is " + startNodeView);
//      }
//    }
    aDrawPane.getChildren().add(dragLine);
  }
  
    
  public void onMouseDragged(MouseEvent event) {
    dragLine.setStartX(dragStartX);
    dragLine.setStartY(dragStartY);
    
    if (event.getSource() instanceof AbstractNodeView) {
      dragLine.setEndX(event.getX() + ((AbstractNodeView) event.getSource()).getTranslateX());
      dragLine.setEndY(event.getY() + ((AbstractNodeView) event.getSource()).getTranslateY());
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
      Point2D endPoint = getEndPoint();
      Point2D endPointBetter = new Point2D(endPoint.getX()-20,endPoint.getY());
      if(nodeView.contains(endPointBetter) && nodeView instanceof PortNodeView){
    	System.out.println("Here we found an Endpoint");  
        endNodeView = nodeView;
        System.out.println("Endpoint is " + endNodeView.toString());
        System.out.println("And StartNodeView is " + startNodeView);
      }
    }
    PortNode endNode = new PortNode();
    PortNode startNode = new PortNode();
    if (endNodeView != null && startNodeView != null && endNodeView instanceof PortNodeView) {
      endNode = (PortNode) diagramController.getNodeMap().get(endNodeView);
      startNode = (PortNode) diagramController.getNodeMap().get(startNodeView);
      System.out.println("EndNode " + endNode.toString());
      System.out.println("startNode " + startNode.toString());
      System.out.println("EndNodeView " + endNodeView.toString());
      System.out.println("StartNodeView " + startNodeView.toString());
      ConnectorEdge edge = new ConnectorEdge(startNode, endNode);
      ((MontiArcController)diagramController).createEdgeView(edge, (PortNodeView)startNodeView, (PortNodeView)endNodeView);
    } 
    else if (endNodeView != null && startNodeView != null) {
      System.out.println("Nothing found");	
      System.out.println("StartNodeView " + startNodeView.toString());
      System.out.println("EndNodeView " + endNodeView.toString());
      for (PortNodeView pView: ((ComponentNodeView) endNodeView).getPortNodeViews()) {
        if(pView.getX() <= getEndPoint().getX() && pView.getX() + pView.getPortWidth() >= getEndPoint().getX()
            && pView.getY() <= getEndPoint().getY() && pView.getY() + pView.getPortHeight() >= getEndPoint().getY()) {
          endNode = (PortNode)diagramController.getNodeMap().get(pView);
          startNode = (PortNode) diagramController.getNodeMap().get(startNodeView);
          ConnectorEdge edge = new ConnectorEdge(startNode, endNode);
          ((MontiArcController)diagramController).createEdgeView(edge, (PortNodeView)startNodeView, (PortNodeView)endNodeView);
        }
      }
    }
    else {
      System.out.println("There is no EndPoint or StartPoint");
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
      if (edgeView instanceof ConnectorEdgeView) {
        diagramController.undoManager.add(new MoveMessageCommand((MessageEdge) edgeView.getRefEdge(), 0, edgeView.getStartY() - dragStartY));
      }
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
    if ((edge instanceof ConnectorEdge)) {
      try {
        // Load the classDiagramView.fxml file and create a new stage for the
        // popup
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/connectorEdgeEditDialog.fxml"));
        AnchorPane dialog = loader.load();
        dialog.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
        dialog.setStyle("-fx-border-color: black");
        // Set location for "dialog".
        dialog.setLayoutX((edge.getStartNode().getTranslateX() + edge.getEndNode().getTranslateX()) / 2);
        dialog.setLayoutY((edge.getStartNode().getTranslateY() + edge.getEndNode().getTranslateY()) / 2);
        
        EdgeEditDialogControllerMonti controller = loader.getController();
        controller.setEdge(edge);
        ChoiceBox directionBox = controller.getDirectionBox();
        ChoiceBox typeBox = controller.getTypeBox();
        String stereoType = controller.getStereoType();
        controller.getOkButton().setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            CompoundCommand command = new CompoundCommand();
            if (controller.hasStereoTypeChanged()) {
              command.add(new SetEdgeStereoTypeCommand(edge, controller.getStereoType(), ((ConnectorEdge)edge).getStereoType()));
              ((ConnectorEdge)edge).setStereoType(controller.getStereoType());
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
    
    AbstractEdgeView newEdgeView = ((MontiArcController)diagramController).createEdgeView(newEdge, (PortNodeView)oldEdgeView.getStartNode(), (PortNodeView)oldEdgeView.getEndNode());
    
    diagramController.getUndoManager().add(new ReplaceEdgeCommand(oldEdge, newEdge, oldEdgeView, newEdgeView, diagramController, diagramController.getGraphModel()));
    
    return true;
  }
}
