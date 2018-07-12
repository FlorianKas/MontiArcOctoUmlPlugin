package controller;

import controller.dialog.NodeEditDialogController;
import controller.dialog.NodeEditDialogControllerMonti;
import edu.tamu.core.sketch.BoundingBox;
import edu.tamu.core.sketch.Point;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.nodes.AbstractNode;
import model.nodes.ClassNode;
import model.nodes.ComponentNode;
import model.nodes.PackageNode;
import model.nodes.PortNode;
import util.Constants;
import util.commands.*;
import view.nodes.AbstractNodeView;
import view.nodes.ClassNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PackageNodeView;
import view.nodes.PortNodeView;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Used by MainController for handling moving and resizing Nodes, among other
 * things.
 */
public class NodeControllerMonti extends NodeController {
  // For resizing rectangles
  private Rectangle dragRectangle;
  
  private Pane aDrawPane;
  private AbstractDiagramController diagramController;
  private boolean snapToGrid = true, snapIndicators = false;
  private AbstractNode currentResizeNode;
  private HashMap<PortNode, Double> diffX = new HashMap<>();
  private HashMap<PortNode, Double> diffY = new HashMap<>();
  // For drag-moving nodes
  private double initMoveX, initMoveY;
  private HashMap<AbstractNode, Point2D.Double> initTranslateMap = new HashMap<>();
  private ArrayList<AbstractNode> toBeMoved = new ArrayList<>();
  private HashMap<AbstractNode, Line> xSnapIndicatorMap = new HashMap<>();
  private HashMap<AbstractNode, Line> ySnapIndicatorMap = new HashMap<>();
  
  public NodeControllerMonti(Pane pDrawPane, AbstractDiagramController pDiagramController) {
    super(pDrawPane, pDiagramController);
    diagramController = pDiagramController;
    aDrawPane = pDrawPane;
    
    dragRectangle = new Rectangle();
    dragRectangle.setFill(null);
    dragRectangle.setStroke(Color.BLACK);
  }
  
  public void translateDragRectangle(double translateX, double translateY) {
    dragRectangle.setTranslateX(translateX);
    dragRectangle.setTranslateY(translateY);
  }
  
  public void resizeStart(AbstractNodeView nodeView) {
    aDrawPane.getChildren().add(dragRectangle);
    dragRectangle.setWidth(nodeView.getWidth());
    dragRectangle.setHeight(nodeView.getHeight());
    dragRectangle.setX(nodeView.getTranslateX());
    dragRectangle.setY(nodeView.getTranslateY());
    currentResizeNode = nodeView.getRefNode();
    createSnapIndicators(currentResizeNode);
  }
  
  public void resize(MouseEvent event) {
    dragRectangle.setWidth(event.getX());
    dragRectangle.setHeight(event.getY());
    
    if (snapIndicators) {
      Double w = dragRectangle.getWidth() + dragRectangle.getX();
      Double h = dragRectangle.getHeight() + dragRectangle.getY();
      setSnapIndicators(closestInteger(w.intValue(), Constants.GRID_DISTANCE), closestInteger(h.intValue(), Constants.GRID_DISTANCE), currentResizeNode, false);
    }
    
    ArrayList<AbstractNodeView> selectedNodes = diagramController.getAllNodeViews();
    
  }
  
  public void resizeFinished(AbstractNode node) {
    double oldWidth = node.getWidth();
    double oldHeight = node.getHeight();
    if (snapToGrid) {
      Double w = dragRectangle.getWidth();
      Double h = dragRectangle.getHeight();
      node.setWidth(closestInteger(w.intValue(), Constants.GRID_DISTANCE));
      node.setHeight(closestInteger(h.intValue(), Constants.GRID_DISTANCE));
    }
    else {
      node.setWidth(dragRectangle.getWidth());
      node.setHeight(dragRectangle.getHeight());
    }
    
    diagramController.getUndoManager().add(new ResizeNodeCommand(node, oldWidth, oldHeight, node.getWidth(), node.getHeight()));
    
    removeSnapIndicators();
    dragRectangle.setHeight(0);
    dragRectangle.setWidth(0);
    translateDragRectangle(0, 0);
    aDrawPane.getChildren().remove(dragRectangle);
  }
  
  public void moveNodesStart(MouseEvent event) {
    initMoveX = event.getSceneX();
    initMoveY = event.getSceneY();
    
    Point2D.Double initTranslate;
    Point2D.Double initTranslate1;
    ArrayList<AbstractNode> selectedNodes = new ArrayList<>();
    for (AbstractNodeView nodeView : diagramController.getSelectedNodes()) {
      selectedNodes.add(diagramController.getNodeMap().get(nodeView));
    }
    
    for (AbstractNode n : diagramController.getGraphModel().getAllNodes()) {
      if (selectedNodes.contains(n)) {
        if (n instanceof ComponentNode) {
          initTranslate = new Point2D.Double(n.getTranslateX(), n.getTranslateY());
          System.out.println("ComponentNode " + n.getX() + " " + n.getY() + "initTranslate " + initTranslate.toString());
          initTranslateMap.put(n, initTranslate);
          toBeMoved.add(n);
          if (snapIndicators) {
            createSnapIndicators(n);
          }
          for (PortNode p : ((ComponentNode)n).getPorts()) {
            initTranslate1 = new Point2D.Double(p.getTranslateX(), p.getTranslateY());
            System.out.println("PortNode " + p.getX() + " " + p.getY() + "initTranslate " + initTranslate1.toString());
            initTranslateMap.put(p, initTranslate1);
            toBeMoved.add(p);
            diffX.put(p, n.getX()-p.getX());
            diffY.put(p, n.getY()-p.getY());
            if (snapIndicators) {
              createSnapIndicators(p);
            }
          }
        }
      }
    }
  }
  
  public void moveNodes(MouseEvent event) {
    double offsetX = (event.getSceneX() - initMoveX) * (1 / diagramController.drawPane.getScaleX());
    double offsetY = (event.getSceneY() - initMoveY) * (1 / diagramController.drawPane.getScaleY());
    
    // Drag all selected nodes and their children
    for (AbstractNode n : toBeMoved) {
      if (n instanceof ComponentNode) {
        Double x = initTranslateMap.get(n).getX() + offsetX;
        Double y = initTranslateMap.get(n).getY() + offsetY;
        n.setTranslateX(x);
        n.setTranslateY(y);
        n.setX(x);
        n.setY(y);
        if (snapIndicators) {
          setSnapIndicators(closestInteger(x.intValue(), Constants.GRID_DISTANCE), closestInteger(y.intValue(), Constants.GRID_DISTANCE), n, true);
        }
        for (PortNode p : ((ComponentNode)n).getPorts()) {
          Double x1 = initTranslateMap.get(p).getX() + offsetX;
          Double y1 = initTranslateMap.get(p).getY() + offsetY;
          p.setTranslateX(x1);
          p.setTranslateY(y1);
          p.setX(x1);
          p.setY(y1);
          if (snapIndicators) {
            setSnapIndicators(closestInteger(x1.intValue(), Constants.GRID_DISTANCE), closestInteger(y1.intValue(), Constants.GRID_DISTANCE), p, true);
          }
        }
      }
      else {
//        Double x = initTranslateMap.get(n).getX() + offsetX;
//        Double y = initTranslateMap.get(n).getY() + offsetY;
//        n.setTranslateX(x);
//        n.setTranslateY(y);
//        n.setX(x);
//        n.setY(y);
//        if (snapIndicators) {
//          setSnapIndicators(closestInteger(x.intValue(), Constants.GRID_DISTANCE), closestInteger(y.intValue(), Constants.GRID_DISTANCE), n, true);
//        }
      }
    }
  }
  
  public double[] moveNodesFinished(MouseEvent event) {
    double offsetX = (event.getSceneX() - initMoveX) * (1 / diagramController.drawPane.getScaleX());
    double offsetY = (event.getSceneY() - initMoveY) * (1 / diagramController.drawPane.getScaleY());
    System.out.println("OffsetX " + offsetX + "OffsetY " + offsetY);
    for (AbstractNode n : toBeMoved) {
      if (n instanceof ComponentNode) {
        Double x = n.getTranslateX();
        Double y = n.getTranslateY();
        if (snapToGrid) {
          System.out.println("SnapToGrid");
          int xSnap = closestInteger(x.intValue(), 20); // Snap to grid
          int ySnap = closestInteger(y.intValue(), 20);
          n.setTranslateX(xSnap);
          n.setTranslateY(ySnap);
          n.setX(xSnap);
          n.setY(ySnap);
          for (PortNode p : ((ComponentNode) n).getPorts()) {
            p.setTranslateX(n.getTranslateX()-diffX.get(p));
            p.setTranslateY(n.getTranslateY()-diffY.get(p));
            p.setX(n.getX()-diffX.get(p));
            p.setY(n.getY()-diffY.get(p));
          }
        }
        else {
          System.out.println("NOOOO SNAP");
          n.setTranslateX(x);
          n.setTranslateY(y);
          n.setX(x);
          n.setY(y);
          for (PortNode p : ((ComponentNode) n).getPorts()) {
            p.setTranslateX(n.getTranslateX()-diffX.get(p));
            p.setTranslateY(n.getTranslateY()-diffY.get(p));
            p.setX(n.getX()-diffX.get(p));
            p.setY(n.getY()-diffY.get(p));
          }
        }
//        for (PortNode p : ((ComponentNode) n).getPorts()) {
//          Double x1 = initTranslateMap.get(p).getX() + offsetX;
//          Double y1 = initTranslateMap.get(p).getY() + offsetY;
//          if (snapToGrid) {
//            int xSnap1 = closestInteger(x1.intValue(), 20); // Snap to grid
//            int ySnap1 = closestInteger(y1.intValue(), 20);
//            p.setTranslateX(xSnap1);
//            p.setTranslateY(ySnap1);
//            p.setX(xSnap1);
//            p.setY(ySnap1);
//            p.setTranslateX(n.getTranslateX()+diffX.get(p));
//            p.setTranslateY(n.getTranslateY()+diffY.get(p));
//            p.setX(n.getX()+diffX.get(p));
//            p.setY(n.getY()+diffY.get(p));
//          }
//          else {
//            p.setTranslateX(x1);
//            p.setTranslateY(y1);
//            p.setX(x1);
//            p.setY(y1);
//            p.setTranslateX(n.getTranslateX()+diffX.get(p));
//            p.setTranslateY(n.getTranslateY()+diffY.get(p));
//            p.setX(n.getX()+diffX.get(p));
//            p.setY(n.getY()+diffY.get(p));
//          
//          }
//        }
        System.out.println("ComponentNode Afterwards" + n.getX() + " " + n.getY());
        for (PortNode p : ((ComponentNode) n).getPorts()) {
          System.out.println("PortNode afterwards " + p.getX() + " " + p.getY());
        }
        
      }
    }

    toBeMoved.clear();
    initTranslateMap.clear();
    if (snapIndicators) {
      removeSnapIndicators();
    }
    
    double[] deltaTranslateVector = new double[2];
    deltaTranslateVector[0] = event.getSceneX() - initMoveX;
    deltaTranslateVector[1] = event.getSceneY() - initMoveY;
    
    ArrayList<AbstractNodeView> selectedNodes = diagramController.getSelectedNodes();
    return deltaTranslateVector;
  }
  
  /**
   * @param a
   * @param b
   * @return Multiple of b that is closest to a. Used for "snapping to grid".
   */
  static int closestInteger(int a, int b) { // TODO GRID DISTANCE CONSTANT
    int c1 = a - (a % b);
    int c2 = (a + b) - (a % b);
    if (a - c1 > c2 - a) {
      return c2;
    }
    else {
      return c1;
    }
  }
  
 
  /**
   * Initializes snap inidicators for AbstractNode.
   * 
   * @param n
   */
  private void createSnapIndicators(AbstractNode n) {
    Line xSnapIndicator = new Line(0, 0, 0, 0);
    Line ySnapIndicator = new Line(0, 0, 0, 0);
    xSnapIndicator.setStroke(Color.BLACK);
    ySnapIndicator.setStroke(Color.BLACK);
    aDrawPane.getChildren().addAll(xSnapIndicator, ySnapIndicator);
    xSnapIndicatorMap.put(n, xSnapIndicator);
    ySnapIndicatorMap.put(n, ySnapIndicator);
  }
  
  /**
   * Places snap indicators to where the node would be snapped to.
   * 
   * @param move True if moving, false if resizing.
   * @param xSnap
   * @param ySnap
   * @param n
   */
  private void setSnapIndicators(int xSnap, int ySnap, AbstractNode n, boolean move) {
    Line xSnapIndicator = xSnapIndicatorMap.get(n);
    Line ySnapIndicator = ySnapIndicatorMap.get(n);
    
    xSnapIndicator.setStartX(xSnap);
    xSnapIndicator.setEndX(xSnap);
    xSnapIndicator.setStartY(ySnap);
    if (move) {
      xSnapIndicator.setEndY(ySnap + Constants.GRID_DISTANCE);
    }
    else {
      xSnapIndicator.setEndY(ySnap - Constants.GRID_DISTANCE);
    }
    
    ySnapIndicator.setStartX(xSnap);
    if (move) {
      ySnapIndicator.setEndX(xSnap + Constants.GRID_DISTANCE);
    }
    else {
      ySnapIndicator.setEndX(xSnap - Constants.GRID_DISTANCE);
    }
    
    ySnapIndicator.setStartY(ySnap);
    ySnapIndicator.setEndY(ySnap);
  }
  
  /**
   * Removes all snap indicators from the view.
   */
  private void removeSnapIndicators() {
    aDrawPane.getChildren().removeAll(xSnapIndicatorMap.values());
    aDrawPane.getChildren().removeAll(ySnapIndicatorMap.values());
    xSnapIndicatorMap.clear();
    ySnapIndicatorMap.clear();
    currentResizeNode = null; // Only needed when resizing
  }
  
  public void onDoubleClick(AbstractNodeView nodeView, double posX, double posY) {
    System.out.println("No we are in it");
    boolean port = false;
    if (nodeView instanceof ComponentNodeView){
      
      ArrayList<PortNodeView> portViews = ((ComponentNodeView) nodeView).getPortNodeViews();
      ArrayList<PortNodeView> portViewsLeft = new ArrayList<>();
      ArrayList<PortNodeView> portViewsRight = new ArrayList<>();
      
      for (PortNodeView p : portViews) {
        if(p.getX() < nodeView.getX()) {
          portViewsLeft.add(p);
        }
        else {
          portViewsRight.add(p);
        }
      }
      
      String dir = "";
      if (posX > 0 && posX < 40) {
        dir = "left";
      }
      else if ((posX < nodeView.getBounds().getMinX() + nodeView.getBounds().getWidth()) 
          && (posX > nodeView.getBounds().getMinX() + nodeView.getBounds().getWidth()-40)) {
        dir = "right";
      }
      
      if (dir == "left") {
        for (PortNodeView p : portViewsLeft) {
          if((posY < p.getBounds().getMinY() + p.getBounds().getHeight()) &&
              (posY > p.getBounds().getMinY()) ) {
            showPortNodeEditDialog(((ComponentNodeView) nodeView).getNodeCompMap().get(p));
            port = true;
            break;
          }
        }
      } 
      else if (dir == "right") {
        for (PortNodeView p : portViewsRight) {
          if((posY < p.getBounds().getMinY() + p.getBounds().getHeight()) &&
              (posY > p.getBounds().getMinY()) ) {
            showPortNodeEditDialog(((ComponentNodeView) nodeView).getNodeCompMap().get(p));
            port = true;
          break;
          }
        }
      }
      if(port == false) {
        showComponentNodeEditDialog((ComponentNode) diagramController.getNodeMap().get(nodeView));  
        System.out.println("Rec. as Comp Node");
      }
    }
    else {
      showNodeTitleDialog(diagramController.getNodeMap().get(nodeView));
      // PackageNode
    }
  }
  
  public void setSnapIndicators(boolean snapIndicators) {
    this.snapIndicators = snapIndicators;
  }
  
  public void setSnapToGrid(boolean snapToGrid) {
    this.snapToGrid = snapToGrid;
  }
  
  // ------------------- VOICE ----------------------
  /**
   * Brings up a dialog to give a title to a Node.
   * 
   * @param node, the Node to give a Title
   * @return false if node == null, otherwise true.
   */
  private boolean showNodeTitleDialog(AbstractNode node) {
    if (diagramController.voiceController.voiceEnabled) {
      // Change variable testing in VoiceController to 1(true)
      diagramController.voiceController.testing = 1;
      
      String title2 = "";
      int time = 0;
      // Looking for a name you want to add to the package or until 5 seconds
      // have passed
      while ((title2.equals("") || title2 == null) && time < 500) {
        try {
          TimeUnit.MILLISECONDS.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        // Check if a name has been recognised
        title2 = diagramController.voiceController.titleName;
        time++;
      }
      
      // Change variable testing in VoiceController to 0(false)
      diagramController.voiceController.testing = 0;
      
      // If name found in less then 5 seconds it sets the name to the package
      if (time < 500) {
        diagramController.voiceController.titleName = "";
        node.setTitle(title2);
      }
      // Else the name is not changed to a new name
      else {
        diagramController.voiceController.titleName = "";
      }
      
      node.setTitle(title2);
    }
    
    VBox group = new VBox();
    TextField input = new TextField();
    input.setText(node.getTitle());
    Button okButton = new Button("Ok");
    okButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        node.setTitle(input.getText());
        System.out.println("New title is" + node.getTitle());
        aDrawPane.getChildren().remove(group);
      }
    });
    
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        aDrawPane.getChildren().remove(group);
      }
    });
    
    Label label = new Label("Choose title test");
    group.getChildren().add(label);
    group.getChildren().add(input);
    HBox buttons = new HBox();
    buttons.getChildren().add(okButton);
    buttons.getChildren().add(cancelButton);
    buttons.setPadding(new Insets(15, 0, 0, 0));
    group.getChildren().add(buttons);
    group.setLayoutX(node.getX() + 5);
    group.setLayoutY(node.getY() + 5);
    group.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
    group.setStyle("-fx-border-color: black");
    group.setPadding(new Insets(15, 12, 15, 12));
    aDrawPane.getChildren().add(group);
    return true;
  }
  
  
  public boolean showComponentNodeEditDialog(ComponentNode node) {
    if (diagramController.voiceController.voiceEnabled) {
      
      // Change variable testing in MainController to 1(true)
      diagramController.voiceController.testing = 1;
      
      String title = "";
      int time = 0;
      // Looking for a name you want to add to the class or until 5 seconds have
      // passed
      while ((title.equals("") || title == null) && time < 500) {
        try {
          TimeUnit.MILLISECONDS.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        // Check if a name has been commanded
        title = diagramController.voiceController.titleName;
        time++;
      }
      
      // Change variable testing in MainController to 0(false)
      diagramController.voiceController.testing = 0;
      
      // If name found in less then 5 seconds it sets the name to the class
      if (time < 500) {
        diagramController.voiceController.titleName = "";
        node.setTitle(title);
      }
      // Else the name is not changed to a new name
      else {
        diagramController.voiceController.titleName = "";
      }
    }
    
    try {
      // Load the classDiagramView.fxml file and create a new stage for the
      // popup
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/componentNodeEditDialogReal.fxml"));
      System.out.println("Loader load " + loader.getLocation());
      AnchorPane dialog = loader.load();
      dialog.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
      dialog.setStyle("-fx-border-color: black");
      // Set location for dialog.
      double maxX = aDrawPane.getWidth() - dialog.getPrefWidth();
      double maxY = aDrawPane.getHeight() - dialog.getPrefHeight();
      dialog.setLayoutX(Math.min(maxX, node.getTranslateX() + 5));
      dialog.setLayoutY(Math.min(maxY, node.getTranslateY() + 5));
      
      NodeEditDialogControllerMonti controller = loader.getController();
      controller.setNode(node);
      controller.getOkButton().setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          CompoundCommand command = new CompoundCommand();
          if (controller.hasNameChanged()) {
            command.add(new SetNodeNameCommand(node, controller.getName(), node.getTitle()));
            node.setTitle(controller.getName());
          }
          if (controller.hasSubNameChanged()) {
            command.add(new SetNodeSubNameCommand(node, controller.getSubName(), node.getSubName()));
            node.setSubName(controller.getSubName());
          }
          if (controller.hasStereotypeChanged()) {
            String stereotype = controller.getStereotype();
            if(!(stereotype.contains("<<"))) {
              String front = "<<";
              stereotype = front + controller.getStereotype();
            }
            if(!(stereotype.contains(">>"))) {
              String back = ">>";
              stereotype = stereotype + back;
            }
            
            command.add(new SetNodeStereotypeCommand(node, stereotype, node.getStereotype()));
            node.setStereotype(stereotype);
          }
          if (controller.hasTypeChanged()) {
            command.add(new SetNodeComponentTypeCommand(node, controller.getType(), node.getComponentType()));
            node.setComponentType(controller.getType());
          }
          if (controller.hasGenericsChanged()) {
            command.add(new SetNodeGenericsCommand(node, controller.getGenerics(), node.getGenerics()));
            node.setGenerics(controller.getGenerics());
          }
          if (command.size() > 0) {
            diagramController.getUndoManager().add(command);
          }
          aDrawPane.getChildren().remove(dialog);
          diagramController.removeDialog(dialog);
        }
      });
      
      controller.getCancelButton().setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          aDrawPane.getChildren().remove(dialog);
          diagramController.removeDialog(dialog);
        }
      });
      aDrawPane.getChildren().add(dialog);
      diagramController.addDialog(dialog);
      return controller.isOkClicked();
      
    }
    catch (IOException e) {
      // Exception gets thrown if the classDiagramView.fxml file could not be
      // loaded
      e.printStackTrace();
      return false;
    }
  }
  
  
  public boolean showPortNodeEditDialog(PortNode node) {
    System.out.println("We are in Edit");
    if (diagramController.voiceController.voiceEnabled) {
      
      // Change variable testing in MainController to 1(true)
      diagramController.voiceController.testing = 1;
      
      String title = "";
      int time = 0;
      // Looking for a name you want to add to the class or until 5 seconds have
      // passed
      while ((title.equals("") || title == null) && time < 500) {
        try {
          TimeUnit.MILLISECONDS.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        // Check if a name has been commanded
        title = diagramController.voiceController.titleName;
        time++;
      }
      
      // Change variable testing in MainController to 0(false)
      diagramController.voiceController.testing = 0;
      
      // If name found in less then 5 seconds it sets the name to the class
      if (time < 500) {
        diagramController.voiceController.titleName = "";
        node.setTitle(title);
      }
      // Else the name is not changed to a new name
      else {
        diagramController.voiceController.titleName = "";
      }
    }
    
    try {
      // Load the classDiagramView.fxml file and create a new stage for the
      // popup
      System.out.println("We are in try");
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/componentNodeEditDialog.fxml"));
      
      AnchorPane dialog = loader.load();
      dialog.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
      dialog.setStyle("-fx-border-color: black");
      // Set location for dialog.
      double maxX = aDrawPane.getWidth() - dialog.getPrefWidth();
      double maxY = aDrawPane.getHeight() - dialog.getPrefHeight();
      System.out.println("node " + node);
      dialog.setLayoutX(Math.min(maxX, node.getTranslateX() + 5));
      dialog.setLayoutY(Math.min(maxY, node.getTranslateY() + 5));
      
      NodeEditDialogControllerMonti controller = loader.getController();
      controller.setNode(node);
      controller.getOkButton().setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          CompoundCommand command = new CompoundCommand();
          if (controller.hasTitledChanged()) {
            command.add(new SetNodeTitleCommand(node, controller.getTitle(), node.getTitle()));
            node.setTitle(controller.getTitle());
          }
          if (controller.hasDataTypeChanged()) {
            command.add(new SetNodeDataTypeCommand(node, controller.getDataType(), node.getPortType()));
            
          }
          if (command.size() > 0) {
            diagramController.getUndoManager().add(command);
            node.setPortType(controller.getDataType());
          }
          aDrawPane.getChildren().remove(dialog);
          diagramController.removeDialog(dialog);
        }
      });
      
      controller.getCancelButton().setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          aDrawPane.getChildren().remove(dialog);
          diagramController.removeDialog(dialog);
        }
      });
      aDrawPane.getChildren().add(dialog);
      diagramController.addDialog(dialog);
      return controller.isOkClicked();
      
    }
    catch (IOException e) {
      // Exception gets thrown if the classDiagramView.fxml file could not be
      // loaded
      e.printStackTrace();
      return false;
    }
  }
}
