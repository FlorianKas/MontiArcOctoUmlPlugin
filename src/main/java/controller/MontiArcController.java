package controller;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import org.controlsfx.control.Notifications;

import controller.dialog.MontiInitDialogController;
import controller.dialog.AddGenericsController;
import controller.dialog.AddTypesController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Sketch;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import util.commands.AddDeleteNodeCommand;
import util.commands.CompoundCommand;
import util.commands.MoveGraphElementCommand;
import view.edges.AbstractEdgeView;
import view.edges.ConnectorEdgeView;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PackageNodeView;

public class MontiArcController extends AbstractDiagramController {
  
  

  NodeControllerMonti nodeController;
  MontiRecognizeController recognizeController;
  TabControllerMonti tabController;
  EdgeControllerMonti edgeController;
  SelectControllerMonti selectController;
  MontiArcPlugin plugin;
  
  
  public static ArrayList<String> genericsArray = new ArrayList<String>();
  public static ArrayList<String> types = new ArrayList<String>();
  String modelName = "";
  public static String packageName = ""; 
  @FXML
  VBox config;
  @FXML
  protected Button createBtn, packageBtn, edgeBtn, selectBtn, drawBtn, undoBtn, redoBtn, moveBtn, deleteBtn, voiceBtn, recognizeBtn, checkValidityBtn, addGenericsBtn, addTypesBtn;
  
  @FXML
  public void initialize() {
    super.initialize();
    initToolBarActions();
    initDrawPaneActions();  
    
    nodeController = new NodeControllerMonti(drawPane, this);
    recognizeController = new MontiRecognizeController(drawPane, this);
    edgeController = new EdgeControllerMonti(drawPane,this);
    selectController = new SelectControllerMonti(drawPane, this);
    plugin = new MontiArcPlugin();
    
    
    
  }
  
  public ArrayList<String> getGenerics() {
    return genericsArray;  
  }
  
  public ArrayList<String> getTypes() {
    return types;  
  }
  
  public void showConfiguration(MontiInitDialogController controller) {
    if(controller.isOkClicked()) {
      modelName = controller.nameTextField.getText();  
      packageName = controller.packageTextField.getText();
      String genericsString = controller.genericsTextField.getText();
      String[] genericsTmp = genericsString.split("\\;");
      for (String g: genericsTmp) {
        genericsArray.add(g);
      }
      String typeParam = controller.arcParameterTextField.getText();

      String[] typesTmp = typeParam.split("\\;");
      for (String t : typesTmp) {
        types.add(t);
      }
      
      
      showOutput();
      
    }
  }
  
  void showOutput(){
    config.getChildren().removeAll(config.getChildren());
    
    Label packetName = new Label();
    packetName.setText("Package Name: " + packageName);
    config.getChildren().add(packetName);
    
    
    Label name = new Label();
    name.setText("Name: " + modelName);
    config.getChildren().add(name);
    
    Label generics = new Label();
    if (genericsArray.size() > 0) {
      generics.setText("Generics: ");
      config.getChildren().add(generics);
      for( String g : genericsArray) {
        Label tmp = new Label();
        tmp.setText(g);
        config.getChildren().add(tmp);
      }
    }
    Label typeParameters = new Label();
    if (types.size() > 0) {
      typeParameters.setText("Type Parameters: ");
      config.getChildren().add(typeParameters);
      for( String t : types) {
        Label tmp = new Label();
        tmp.setText(t);
        config.getChildren().add(tmp);
      }
    }
    this.config.setPadding(new Insets(0, 20, 0, 0));
    this.config.setSpacing(5);
    
  }
  
  void addGenerics() {
    AddGenericsController controller = showGenericsDialog();
    String[] tmp = controller.genericsTextField.getText().split("\\;");
    for (String g: tmp) {
      genericsArray.add(g);
    }  
    showOutput();
  }
  
  
  public AddGenericsController showGenericsDialog() {
    AddGenericsController controllerGenerics = null; 
    
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/addGenerics.fxml"));
      System.out.println("Loader load " + loader.getLocation());
      AnchorPane dialog = loader.load();
      
      controllerGenerics = loader.getController();
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.setScene(new Scene(dialog));
      
      controllerGenerics = loader.getController();
      controllerGenerics.setDialogStage(dialogStage);
      dialogStage.showAndWait();
    
      
    }
    
    
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    
    return controllerGenerics;
  }
  
  
  void addTypes() {
    AddTypesController controller = showTypesDialog();
    String[] tmp = controller.arcParameterTextField.getText().split("\\;");
    for (String g: tmp) {
      types.add(g);
    }  
    showOutput();

  }
  
  public AddTypesController showTypesDialog() {
    AddTypesController controllerTypes = null; 
    
    try {

FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/addTypes.fxml"));
      System.out.println("Loader load " + loader.getLocation());
      AnchorPane dialog = loader.load();
      
      controllerTypes = loader.getController();
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.setScene(new Scene(dialog));
      
      controllerTypes = loader.getController();
      controllerTypes.setDialogStage(dialogStage);
      dialogStage.showAndWait();
    }

    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    
    return controllerTypes;
  }
  
  
  void initDrawPaneActions() {
    drawPane.setOnMousePressed(event -> {
      if (mode == Mode.NO_MODE) {
        if (event.getButton() == MouseButton.SECONDARY) { // Create context menu
                                                          // on right-click.
          mode = Mode.CONTEXT_MENU;
          copyPasteController.copyPasteCoords = new double[] { event.getX(), event.getY() };
          aContextMenu.show(drawPane, event.getScreenX(), event.getScreenY());
        }
        else if (tool == ToolEnum.SELECT || tool == ToolEnum.EDGE) { // Start
                                                                     // selecting
                                                                     // elements.
          selectController.onMousePressed(event);
        }
        else if ((tool == ToolEnum.CREATE_CLASS || tool == ToolEnum.CREATE_PACKAGE) && mouseCreationActivated) { // Start
                                                                                                                 // creation
                                                                                                                 // of
                                                                                                                 // package
                                                                                                                 // or
                                                                                                                 // class.
          mode = Mode.CREATING;
          createNodeController.onMousePressed(event);
        }
        else if (tool == ToolEnum.MOVE_SCENE) { // Start panning of graph.
          mode = Mode.MOVING;
          graphController.movePaneStart(event);
        }
        else if (tool == ToolEnum.DRAW && mouseCreationActivated) { // Start
                                                                    // drawing.
          mode = Mode.DRAWING;
          System.out.println("In InitDrawPaneActions event looks like " + event.toString());
          sketchController.onTouchPressed(event);
        }
        
      }
      else if (mode == Mode.CONTEXT_MENU) {
        if (event.getButton() == MouseButton.SECONDARY) {
          copyPasteController.copyPasteCoords = new double[] { event.getX(), event.getY() };
          aContextMenu.show(drawPane, event.getScreenX(), event.getScreenY());
        }
        else {
          aContextMenu.hide();
        }
      }
      event.consume();
    });
    
    drawPane.setOnMouseDragged(event -> {
      if (tool == ToolEnum.SELECT && mode == Mode.SELECTING) { // Continue
                                                               // selection of
                                                               // elements.
        selectController.onMouseDragged(event);
      }
      else if (tool == ToolEnum.DRAW && mode == Mode.DRAWING && mouseCreationActivated) { // Continue
                                                                                          // drawing.
        sketchController.onTouchMoved(event);
      }
      else if ((tool == ToolEnum.CREATE_CLASS || tool == ToolEnum.CREATE_PACKAGE) && mode == Mode.CREATING && mouseCreationActivated) { // Continue
                                                                                                                                        // creation
                                                                                                                                        // of
                                                                                                                                        // class
                                                                                                                                        // or
                                                                                                                                        // package.
        createNodeController.onMouseDragged(event);
      }
      else if (mode == Mode.MOVING && tool == ToolEnum.MOVE_SCENE) { // Continue
                                                                     // panning
                                                                     // of
                                                                     // graph.
        graphController.movePane(event);
      }
      event.consume();
    });
    
    drawPane.setOnMouseReleased(event -> {
      if (tool == ToolEnum.SELECT && mode == Mode.SELECTING) { // Finish
                                                               // selecting
                                                               // elements.
        selectController.onMouseReleased();
      }
      else if (tool == ToolEnum.DRAW && mode == Mode.DRAWING && mouseCreationActivated) { // Finish
                                                                                          // drawing.
        sketchController.onTouchReleased(event);
        // We only want to move out of drawing mode if there are no other
        // current drawings.
        if (!sketchController.currentlyDrawing()) {
          mode = Mode.NO_MODE;
        }
      }
      else if (tool == ToolEnum.CREATE_CLASS && mode == Mode.CREATING && mouseCreationActivated) { // Finish
                                                                                                   // creation
                                                                                                   // of
                                                                                                   // class.
        createNodeController.onMouseReleasedClass();
        if (!createNodeController.currentlyCreating()) {
          mode = Mode.NO_MODE;
        }
        
      }
      else if (tool == ToolEnum.CREATE_PACKAGE && mode == Mode.CREATING && mouseCreationActivated) { // Finish
                                                                                                     // creation
                                                                                                     // of
                                                                                                     // package.
        createNodeController.onMouseReleasedPackage();
        if (!createNodeController.currentlyCreating()) {
          mode = Mode.NO_MODE;
        }
      }
      else if (mode == Mode.MOVING && tool == ToolEnum.MOVE_SCENE) { // Finish
                                                                     // panning
                                                                     // of
                                                                     // graph.
        graphController.movePaneFinished();
        mode = Mode.NO_MODE;
      }
    });
    // ------------------------- Touch ---------------------------------
    // There are specific events for touch when creating and drawing to utilize
    // multitouch. //TODO edge creation multi-user support.
    drawPane.setOnTouchPressed(event -> {
      if ((tool == ToolEnum.CREATE_CLASS || tool == ToolEnum.CREATE_PACKAGE) && !mouseCreationActivated) {
        mode = Mode.CREATING;
        createNodeController.onTouchPressed(event);
      }
      else if (tool == ToolEnum.DRAW && !mouseCreationActivated) {
        mode = Mode.DRAWING;
        sketchController.onTouchPressed(event);
      }
    });
    
    drawPane.setOnTouchMoved(event -> {
      if ((tool == ToolEnum.CREATE_CLASS || tool == ToolEnum.CREATE_PACKAGE) && mode == Mode.CREATING && !mouseCreationActivated) {
        createNodeController.onTouchDragged(event);
      }
      else if (tool == ToolEnum.DRAW && mode == Mode.DRAWING && !mouseCreationActivated) {
        sketchController.onTouchMoved(event);
      }
      event.consume();
    });
    
    drawPane.setOnTouchReleased(event -> {
      if (tool == ToolEnum.CREATE_CLASS && mode == Mode.CREATING && !mouseCreationActivated) {
        createNodeController.onTouchReleasedClass(event);
        if (!createNodeController.currentlyCreating()) {
          mode = Mode.NO_MODE;
        }
        
      }
      else if (tool == ToolEnum.CREATE_PACKAGE && mode == Mode.CREATING && !mouseCreationActivated) {
        createNodeController.onTouchReleasedPackage(event);
        if (!createNodeController.currentlyCreating()) {
          mode = Mode.NO_MODE;
        }
      }
      else if (tool == ToolEnum.DRAW && mode == Mode.DRAWING && !mouseCreationActivated) {
        sketchController.onTouchReleased(event);
        if (!sketchController.currentlyDrawing()) {
          mode = Mode.NO_MODE;
        }
      }
      event.consume();
    });
    
  }
  
  boolean wasAlreadySelected = false;
  
  void initNodeActions(AbstractNodeView nodeView) {
    nodeView.setOnMousePressed(event -> {
      if (event.getClickCount() == 2) { // Open dialog window on double click.
        System.out.println("We are before calling the NodeControllerMonti");
        nodeController.onDoubleClick(nodeView, event.getX(), event.getY());
        tool = ToolEnum.SELECT;
        setButtonClicked(selectBtn);
      }
      else if (tool == ToolEnum.MOVE_SCENE) { // Start panning of graph.
        mode = Mode.MOVING;
        graphController.movePaneStart(event);
        event.consume();
      }
      else if (event.getButton() == MouseButton.SECONDARY) { // Open context
                                                             // menu on left
                                                             // click.
        copyPasteController.copyPasteCoords = new double[] { nodeView.getX() + event.getX(), nodeView.getY() + event.getY() };
        aContextMenu.show(nodeView, event.getScreenX(), event.getScreenY());
      }
      else if (tool == ToolEnum.SELECT || tool == ToolEnum.CREATE_CLASS) { // Select
                                                                           // node
        setTool(ToolEnum.SELECT);
        setButtonClicked(selectBtn);
        if (!(nodeView instanceof PackageNodeView)) {
          nodeView.toFront();
        }
        if (mode == Mode.NO_MODE) { // Either drag selected elements or resize
                                    // node.
          Point2D.Double eventPoint = new Point2D.Double(event.getX(), event.getY());
          if (eventPoint.distance(new Point2D.Double(nodeView.getWidth(), nodeView.getHeight())) < 20) { // Resize
                                                                                                         // if
                                                                                                         // event
                                                                                                         // is
                                                                                                         // close
                                                                                                         // to
                                                                                                         // corner
                                                                                                         // of
                                                                                                         // node
            mode = Mode.RESIZING;
            nodeController.resizeStart(nodeView);
          }
          else {
            mode = Mode.DRAGGING;
            if (!selectedNodes.contains(nodeView)) { // Drag
              wasAlreadySelected = false;
              selectedNodes.add(nodeView);
            }
            else {
              wasAlreadySelected = true;
            }
            drawSelected();
            nodeController.moveNodesStart(event);
            sketchController.moveSketchStart(event);
          }
        }
      }
      else if (tool == ToolEnum.EDGE) { // Start edge creation.
        mode = Mode.CREATING;
        edgeController.onMousePressedOnNode(event);
      }
      event.consume();
    });
    
    nodeView.setOnMouseDragged(event -> {
      if ((tool == ToolEnum.SELECT || tool == ToolEnum.CREATE_CLASS) && mode == Mode.DRAGGING) { // Continue
                                                                                                 // dragging
                                                                                                 // selected
                                                                                                 // elements
        nodeController.moveNodes(event);
        sketchController.moveSketches(event);
      }
      else if (mode == Mode.MOVING && tool == ToolEnum.MOVE_SCENE) { // Continue
                                                                     // panning
                                                                     // graph.
        graphController.movePane(event);
      }
      else if ((tool == ToolEnum.SELECT || tool == ToolEnum.CREATE_CLASS) && mode == Mode.RESIZING) { // Continue
                                                                                                      // resizing
                                                                                                      // node.
        nodeController.resize(event);
      }
      else if (tool == ToolEnum.EDGE && mode == Mode.CREATING) { // Continue
                                                                 // creating
                                                                 // edge.
        edgeController.onMouseDragged(event);
      }
      event.consume();
      
    });
    
    nodeView.setOnMouseReleased(event -> {
      if ((tool == ToolEnum.SELECT || tool == ToolEnum.CREATE_CLASS) && mode == Mode.DRAGGING) { // Finish
                                                                                                 // dragging
                                                                                                 // nodes
                                                                                                 // and
                                                                                                 // create
                                                                                                 // a
                                                                                                 // compound
                                                                                                 // command.
        double[] deltaTranslateVector = nodeController.moveNodesFinished(event);
        sketchController.moveSketchFinished(event);
        if (deltaTranslateVector[0] != 0 || deltaTranslateVector[1] != 0) { // If
                                                                            // it
                                                                            // was
                                                                            // actually
                                                                            // moved
          CompoundCommand compoundCommand = new CompoundCommand();
          for (AbstractNodeView movedView : selectedNodes) {
            compoundCommand.add(new MoveGraphElementCommand(nodeMap.get(movedView), deltaTranslateVector[0], deltaTranslateVector[1]));
          }
          for (Sketch sketch : selectedSketches) {
            compoundCommand.add(new MoveGraphElementCommand(sketch, deltaTranslateVector[0], deltaTranslateVector[1]));
          }
          undoManager.add(compoundCommand);
        }
        else {
          if (wasAlreadySelected) {
            selectedNodes.remove(nodeView);
          }
          drawSelected();
        }
      }
      else if (mode == Mode.MOVING && tool == ToolEnum.MOVE_SCENE) { // Finish
                                                                     // panning
                                                                     // of
                                                                     // graph.
        graphController.movePaneFinished();
        mode = Mode.NO_MODE;
      }
      else if ((tool == ToolEnum.SELECT || tool == ToolEnum.CREATE_CLASS) && mode == Mode.RESIZING) { // Finish
                                                                                                      // resizing
                                                                                                      // node.
        nodeController.resizeFinished(nodeMap.get(nodeView));
      }
      else if (tool == ToolEnum.EDGE && mode == Mode.CREATING) { // Finish
                                                                 // creation of
                                                                 // edge.
        edgeController.onMouseReleasedRelation();
      }
      mode = Mode.NO_MODE;
      event.consume();
    });
    nodeView.setOnTouchPressed(event -> {
      if (nodeView instanceof PackageNodeView && (tool == ToolEnum.CREATE_CLASS || tool == ToolEnum.CREATE_PACKAGE)) {
        mode = Mode.CREATING;
        createNodeController.onTouchPressed(event);
      }
      else if (tool == ToolEnum.DRAW) {
        mode = Mode.DRAWING;
        System.out.println("Event looks like " + event.toString());
        sketchController.onTouchPressed(event);
      }
      event.consume();
    });
    
    nodeView.setOnTouchMoved(event -> {
      if (nodeView instanceof PackageNodeView && (tool == ToolEnum.CREATE_CLASS || tool == ToolEnum.CREATE_PACKAGE) && mode == Mode.CREATING) {
        createNodeController.onTouchDragged(event);
      }
      else if (tool == ToolEnum.DRAW && mode == Mode.DRAWING) {
        sketchController.onTouchMoved(event);
      }
      event.consume();
      
    });
    
    nodeView.setOnTouchReleased(event -> {
      if (nodeView instanceof PackageNodeView && tool == ToolEnum.CREATE_CLASS && mode == Mode.CREATING) {
        createNodeController.onTouchReleasedClass(event);
        if (!createNodeController.currentlyCreating()) {
          mode = Mode.NO_MODE;
        }
        
      }
      else if (nodeView instanceof PackageNodeView && tool == ToolEnum.CREATE_PACKAGE && mode == Mode.CREATING) {
        createNodeController.onTouchReleasedPackage(event);
        if (!createNodeController.currentlyCreating()) {
          mode = Mode.NO_MODE;
        }
      }
      else if (tool == ToolEnum.DRAW && mode == Mode.DRAWING) {
        sketchController.onTouchReleased(event);
        if (!sketchController.currentlyDrawing()) {
          mode = Mode.NO_MODE;
        }
      }
      else if (mode == Mode.MOVING && tool == ToolEnum.MOVE_SCENE) {
        mode = Mode.NO_MODE;
      }
      event.consume();
    });
    
  }
  
  // ------------ Init Buttons -------------------------------------------
  private void initToolBarActions() {
    Image image = new Image("/icons/classw.png");
    createBtn.setGraphic(new ImageView(image));
    createBtn.setText("");
    
    image = new Image("/icons/packagew.png");
    packageBtn.setGraphic(new ImageView(image));
    packageBtn.setText("");
    
    image = new Image("/icons/edgew.png");
    edgeBtn.setGraphic(new ImageView(image));
    edgeBtn.setText("");
    
    image = new Image("/icons/selectw.png");
    selectBtn.setGraphic(new ImageView(image));
    selectBtn.setText("");
    
    image = new Image("/icons/undow.png");
    undoBtn.setGraphic(new ImageView(image));
    undoBtn.setText("");
    
    image = new Image("/icons/redow.png");
    redoBtn.setGraphic(new ImageView(image));
    redoBtn.setText("");
    
    image = new Image("/icons/movew.png");
    moveBtn.setGraphic(new ImageView(image));
    moveBtn.setText("");
    
    image = new Image("/icons/deletew.png");
    deleteBtn.setGraphic(new ImageView(image));
    deleteBtn.setText("");
    
    image = new Image("/icons/draww.png");
    drawBtn.setGraphic(new ImageView(image));
    drawBtn.setText("");
    
    image = new Image("/icons/recow.png");
    recognizeBtn.setGraphic(new ImageView(image));
    recognizeBtn.setText("");
    
    image = new Image("/icons/micw.png");
    voiceBtn.setGraphic(new ImageView(image));
    voiceBtn.setText("");
    
    image = new Image("/icons/recow.png");
    // needs to be replaced by another picture
    checkValidityBtn.setGraphic(new ImageView(image));
    checkValidityBtn.setText("Check");
    
    image = new Image("/icons/recow.png");
    // needs to be replaced by another picture
    addGenericsBtn.setGraphic(new ImageView(image));
    addGenericsBtn.setText("");
    
    image = new Image("/icons/recow.png");
    // needs to be replaced by another picture
    addTypesBtn.setGraphic(new ImageView(image));
    addTypesBtn.setText("");
    
    
    buttonInUse = createBtn;
    buttonInUse.getStyleClass().add("button-in-use");
    
    // ---------------------- Actions for buttons ----------------------------
    createBtn.setOnAction(event -> {
      tool = ToolEnum.CREATE_CLASS;
      setButtonClicked(createBtn);
    });
    
    packageBtn.setOnAction(event -> {
      tool = ToolEnum.CREATE_PACKAGE;
      setButtonClicked(packageBtn);
    });
    
    edgeBtn.setOnAction(event -> {
      tool = ToolEnum.EDGE;
      setButtonClicked(edgeBtn);
    });
    
    selectBtn.setOnAction(event -> {
      tool = ToolEnum.SELECT;
      setButtonClicked(selectBtn);
    });
    
    drawBtn.setOnAction(event -> {
      tool = ToolEnum.DRAW;
      setButtonClicked(drawBtn);
    });
    
    moveBtn.setOnAction(event -> {
      setButtonClicked(moveBtn);
      tool = ToolEnum.MOVE_SCENE;
    });
    
    undoBtn.setOnAction(event -> { 
      undoManager.undoCommand();
      });
    
    redoBtn.setOnAction(event -> undoManager.redoCommand());
    
    deleteBtn.setOnAction(event -> deleteSelected());
    
    recognizeBtn.setOnAction(event -> recognizeController.recognize(selectedSketches));
    addGenericsBtn.setOnAction(event -> addGenerics());
    addTypesBtn.setOnAction(event -> addTypes());
    
    voiceBtn.setOnAction(event -> {
      if (voiceController.voiceEnabled) {
        Notifications.create().title("Voice disabled").text("Voice commands are now disabled.").showInformation();
      }
      else {
        Notifications.create().title("Voice enabled").text("Voice commands are now enabled.").showInformation();
      }
      voiceController.onVoiceButtonClick();
      
    });
    
   
    checkValidityBtn.setOnAction(event -> {
    
    System.out.println("graph looks as follows " + graph.getAllNodes().toString() + graph.getAllEdges().toString());
    System.out.println("modelName " + modelName);
      
    plugin.shapeToAST(graph, modelName);
    
    });
  }
  
  public AbstractNodeView createNodeView(AbstractNode node, boolean remote) {
    AbstractNodeView newView = null;
    if (node instanceof ComponentNode) {
      newView = new ComponentNodeView((ComponentNode) node);
      System.out.println("We are in MONTIARCCONTROLLER BY CREATING NODEVIEW");
    }
    else if (node instanceof PortNode) {
//      ComponentNodeView compView = new ComponentNodeView();
      System.out.println("NodeMap " + nodeMap.toString());
      System.out.println("AllNodeViews "+ allNodeViews);
      for (AbstractNodeView view : allNodeViews) {
        if(nodeMap.get(view) ==  (ComponentNode)((PortNode) node).getComponentNode()) {
          newView = ((ComponentNodeView)view).getNodeToViewMap().get(node);
          break;
        }
      } 
    }
    if (!graph.getAllNodes().contains(node)) {
      // test; maybe also without AbstractDiagramController-cast
      graph.addNode(node, remote);
      undoManager.add(new AddDeleteNodeCommand((AbstractDiagramController)MontiArcController.this, graph, newView, node, true));
    }
    return addNodeView(newView, node);
  }
  
  
  public ConnectorEdgeView addEdgeView(AbstractEdge edge, boolean remote) {
    AbstractNodeView startNodeView = null;
    AbstractNodeView endNodeView = null;
    AbstractNode tempNode;

    System.out.println("We are in MONTIARCCONTROLLER BY ADDING EDGEVIEW");
    for (AbstractNodeView nodeView : allNodeViews) {
      tempNode = nodeMap.get(nodeView);
      System.out.println("TempNode " + tempNode);
      if (edge.getStartNode().getId().equals(tempNode.getId())) {
        edge.setStartNode(tempNode);
        startNodeView = nodeView;
      }
      else if (edge.getEndNode().getId().equals(tempNode.getId())) {
        edge.setEndNode(tempNode);
        endNodeView = nodeView;
      }  
    }    
    System.out.println("We are in ConnectorEdge case");
    System.out.println("StartNodeView "+ startNodeView);
    System.out.println("EndNodeView "+ endNodeView);
    ConnectorEdgeView edgeView = new ConnectorEdgeView(edge, startNodeView, endNodeView);
    
    allEdgeViews.add(edgeView);
    drawPane.getChildren().add(edgeView);
    if (!graph.getAllEdges().contains(edge)) {
      graph.addEdge(edge, remote);
    }
    return edgeView;
  }
  
  public AbstractEdgeView createEdgeView(AbstractEdge edge, AbstractNodeView startNodeView, AbstractNodeView endNodeView) {
    AbstractEdgeView edgeView;

    System.out.println("We are in MONTIARCCONTROLLER BY CREATING EDGEVIEW");
    if (edge instanceof ConnectorEdge) {
      System.out.println("Detected a ConnectorEdge");
      edgeView = new ConnectorEdgeView(edge, startNodeView, endNodeView);
    }
    else {
      edgeView = null;
    }
    System.out.println("EdgeView" + edgeView.getStartX() + edgeView.getStartY() + edgeView.getEndX() + edgeView.getEndY());
    return addEdgeView(edgeView);
  }

  @Override
  public String getTabControllerName() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
}
