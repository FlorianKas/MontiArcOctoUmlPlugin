package controller;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.Notifications;

import controller.dialog.MontiInitDialogController;
import de.monticore.ast.ASTNode;
import groovyjarjarantlr.collections.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import model.Sketch;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;
import model.edges.Edge;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.Infos;
import model.nodes.PortNode;

import plugin.MontiArcPlugin;
import plugin.MontiCoreException;
import exceptions.ConnectorNotCompatibleException;
import exceptions.InnerNameLow;
import exceptions.InnerNameMissingException;
import exceptions.InstanceDuplicateError;
import exceptions.ModelNameError;
import exceptions.PackageNameMissing;
import exceptions.PortTypeException;
import exceptions.allFine;

import util.commands.AddDeleteNodeCommand;
import util.commands.Command;
import util.commands.CompoundCommand;
import util.commands.MoveCompViewCommand;
import view.edges.AbstractEdgeView;
import view.edges.ConnectorEdgeView;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PackageNodeView;
import view.nodes.PortNodeView;

public class MontiArcController extends AbstractDiagramController { 
  
  

  NodeControllerMonti nodeController;
  MontiRecognizeController recognizeController; 
  EdgeControllerMonti edgeController;
  MontiArcPlugin plugin;
  Infos in;
  MontiInitDialogController controller; 
  SelectControllerMonti selectController;
  String genericsString;
  private AnchorPane dialog; 
  private ArrayList<MontiCoreException> errorList1 = new ArrayList<MontiCoreException>();
  public static ArrayList<String> genericsArray = new ArrayList<String>();
  public static ArrayList<String> types = new ArrayList<String>();
  String modelName = "";
  public static ArrayList<String> importStatements = new ArrayList<String>(); 
  public static String packageName = ""; 
  @FXML
  VBox config,topBox;
  
  @FXML
  protected Button edgeBtn, selectBtn, drawBtn, undoBtn, redoBtn, moveBtn, deleteBtn, voiceBtn, recognizeBtn, checkValidityBtn, generateBtn;
  
  @FXML
  public void initialize() {
    System.out.println("We are in MontiArcController");
    super.initialize();
    initToolBarActions();
    initDrawPaneActions();  
    
    nodeController = new NodeControllerMonti(drawPane, this);
    recognizeController = new MontiRecognizeController(drawPane, this);
    edgeController = new EdgeControllerMonti(drawPane,this);
    selectController = new SelectControllerMonti(drawPane, this);
    in = new Infos();
  }
  
  public void showSomething() {
    String name = "";
    boolean bla = showMontiInitDialog();
    showConfiguration();
  }
  
  public boolean showMontiInitDialog() {
    try {
      // Load the montiInitDialog.fxml file and create a new stage for the
      // popup
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/montiInitDialog.fxml"));
      System.out.println("Loader load TestActtion" + loader.getLocation());
      dialog = loader.load();    
      System.out.println("After load" + dialog);
      dialog.toFront();
      dialog.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(1), null)));
      dialog.setStyle("-fx-border-color: black");
      controller = loader.getController();
      controller.set(in);
      Stage dialogStage = new Stage();
      dialogStage.initOwner(getStage());
      //dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.setAlwaysOnTop(true);
      dialogStage.setScene(new Scene(dialog));
      controller.setDialogStage(dialogStage);
      dialogStage.showAndWait();
      return controller.isOkClicked();
    }
    catch (IOException e) {
      // Exception gets thrown if the classDiagramView.fxml file could not be
      // loaded
      e.printStackTrace();
      return false;
    }
    
  }
  
  
  public String getGenerics() {
    return genericsString;  
  }
  
  public ArrayList<String> getTypes() {
    return types;  
  }
  
  public void showConfiguration() {
    if(controller.isOkClicked()) {
      modelName = controller.nameTextField.getText();  
      packageName = controller.packageTextField.getText();
      if (packageName.contains("\\;")) {
        packageName = packageName.split("\\;")[0];
      }
      String importStates = controller.importTextField.getText();
      in.setName(controller.nameTextField.getText());
      in.setPackageName(controller.packageTextField.getText());
      in.setImports(controller.importTextField.getText());
      String[] importTmp = importStates.split("\\;");
      importStatements.clear();
      for (String i: importTmp) {
        importStatements.add(i.trim());
      }
      genericsString = controller.genericsTextField.getText();
//      in.setGenerics(controller.genericsTextField.getText());
//      String[] genericsTmp = genericsString.split("\\;");
//      genericsArray.clear();
//      for (String g: genericsTmp) {
//        genericsArray.add(g.trim());
//      }
      in.setGenerics(genericsString);
      String typeParam = controller.arcParameterTextField.getText();
      in.setArcParam(controller.arcParameterTextField.getText());
      types.clear();
      String[] typesTmp = typeParam.split("\\,");
      for (String t : typesTmp) {
        if (t.contains(",")) {
          t = t.replace(",", "");
        }
        types.add(t.trim());
      }
      
      System.out.println("modelName "+ modelName );
      
      
      showOutputTopBox();
      
    }
  }
  
  void showOutputTopBox(){
    VBox tmp  = new VBox();
    tmp.getChildren().add(topBox.getChildren().get(0));
    topBox.getChildren().removeAll(topBox.getChildren());
    System.out.println("tmp children 0" + tmp.getChildren().get(0).toString());
    topBox.getChildren().add(tmp.getChildren().get(0));
    System.out.println("topBox " +topBox.getChildren().toString());
    if (!packageName.isEmpty()) {
      Label packetName = new Label();
      packetName.setText("  package " + packageName + ";");
      if (topBox.getChildren().contains(packetName)) {
        topBox.getChildren().remove(packetName);
      }
      topBox.getChildren().add(packetName);
    }
    if (!(null == importStatements) && !(importStatements.isEmpty())) {
//      if (topBox.getChildren().contains(imports)) {
//        topBox.getChildren().remove(imports);
//      }
      for (Node n : topBox.getChildren()) {
        if (n instanceof Label) {
          if (((Label) n).getText().contains("import")) {
            topBox.getChildren().remove(n);
          }
        }
      }
      for( String g : importStatements) {
        Label imports = new Label();
        if (!g.isEmpty()) {
          imports.setText("  import " + g + ";");
          topBox.getChildren().add(imports);
        }
      }
    }
    
    Label name = new Label();
    name.setText("  component " + modelName);
//    topBox.getChildren().add(name);
    
    
//    if (!genericsArray.isEmpty()) {
//      int k = 0;
//      if (!genericsArray.get(0).isEmpty()) {
//        name.setText(name.getText() + " <"); 
//      }
//      for( String g : genericsArray) {
//        if(!g.isEmpty()) {
//          k++;
//          name.setText(name.getText() + g + "; ");
//        }
//      }
//      if (k>0) {
//        name.setText(name.getText().substring(0,name.getText().length()-2) + ">");
//      }
//    }
    boolean front = false;
    boolean tail = false;
    if (!genericsString.equals("")) {
      if (genericsString.substring(0, 1).equals("<")) {
        front = true;	  
      }
      if (genericsString.substring(genericsString.length()-1).equals(">") 
    	&& genericsString.split("<").length == genericsString.split(">").length
    	&& genericsString.contains("<")
      ){
    	tail = true;  
      }
      if (front == true && tail == true) {
    	name.setText(name.getText() + genericsString);  
      }
      else if (front == true && tail == false) {
    	name.setText(name.getText() + genericsString + ">");  
      }
      else if (front == false && tail == true) {
    	name.setText(name.getText() + "<" +genericsString);
      }
      else {
    	name.setText(name.getText() + "<" + genericsString + ">");  
      }
    }
    if (types.size() > 0) {
      int i = 0;
      if (!types.get(0).isEmpty()) {
        name.setText(name.getText() + " (");
      }
      for( String t : types) {
        if (!t.isEmpty()) {
          i++;
          name.setText(name.getText() + t + ", ");
        }
      }
      if (i > 0) {
        name.setText(name.getText().substring(0,name.getText().length()-2) + ")");
      }
      if (topBox.getChildren().contains(name)) {
        topBox.getChildren().remove(name);
      }
      topBox.getChildren().add(name);
    }
    this.topBox.setPadding(new Insets(0, 20, 0, 0));
    this.topBox.setSpacing(5);
    System.out.println("ViewBox " + topBox.getChildren().toString());
  }
  
  
  void showOutput(){
    config.getChildren().removeAll(config.getChildren());
    
    Label packetName = new Label();
    packetName.setText("Package Name: " + packageName);
    config.getChildren().add(packetName);
    
    Label imports = new Label();
    if (importStatements.size() > 0) {
      imports.setText("Imports: ");
      config.getChildren().add(imports);
      for( String g : importStatements) {
        Label tmp = new Label();
        tmp.setText(g);
        config.getChildren().add(tmp);
      }
    }
    
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
    if (types.size() > 0) {
      for( String t : types) {
        Label tmp = new Label();
        tmp.setText(t);
        config.getChildren().add(tmp);
      }
    }
    this.config.setPadding(new Insets(0, 20, 0, 0));
    this.config.setSpacing(5);
    
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
      if (tool == ToolEnum.DRAW && !mouseCreationActivated) {
        mode = Mode.DRAWING;
        sketchController.onTouchPressed(event);
      }
    });
    
    drawPane.setOnTouchMoved(event -> {
      if (tool == ToolEnum.DRAW && mode == Mode.DRAWING && !mouseCreationActivated) {
        sketchController.onTouchMoved(event);
      }
      event.consume();
    });
    
    drawPane.setOnTouchReleased(event -> {
      if (tool == ToolEnum.DRAW && mode == Mode.DRAWING && !mouseCreationActivated) {
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
      else if (tool == ToolEnum.SELECT) { // Select
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
        System.out.println("Event " + event.toString());
        edgeController.onMousePressedOnNode(event);
        
      }
      event.consume();
    });
    
    nodeView.setOnMouseDragged(event -> {
      if ((tool == ToolEnum.SELECT) && mode == Mode.DRAGGING) { // Continue
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
      else if ((tool == ToolEnum.SELECT) && mode == Mode.RESIZING) { // Continue
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
      if ((tool == ToolEnum.SELECT) && mode == Mode.DRAGGING) { // Finish
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
            System.out.println("Selected Nodes " + selectedNodes);
            if (movedView instanceof ComponentNodeView) {
              System.out.println("We are in moving and deltaTranslateVector looks as follows " + deltaTranslateVector[0] + deltaTranslateVector[1]);
              compoundCommand.add(new MoveCompViewCommand(nodeMap.get(movedView), deltaTranslateVector[0], deltaTranslateVector[1]));
              System.out.println("We are after compound");
            } 
            else {
              compoundCommand.add(new MoveCompViewCommand(nodeMap.get(movedView), deltaTranslateVector[0], deltaTranslateVector[1]));
            }
          }
          for (Sketch sketch : selectedSketches) {
            compoundCommand.add(new MoveCompViewCommand(sketch, deltaTranslateVector[0], deltaTranslateVector[1]));
          }
          for (Command c : compoundCommand.getCommands()) {
            System.out.println("Command looks as follows " + c.toString());
          }
          System.out.println("CompoundCommand  " + compoundCommand.getCommands());
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
      else if ((tool == ToolEnum.SELECT) && mode == Mode.RESIZING) { // Finish
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
      if (tool == ToolEnum.DRAW) {
        mode = Mode.DRAWING;
        sketchController.onTouchPressed(event);
      }
      event.consume();
    });
    
    nodeView.setOnTouchMoved(event -> {
      if (tool == ToolEnum.DRAW && mode == Mode.DRAWING) {
        sketchController.onTouchMoved(event);
      }
      event.consume();
      
    });
    
    nodeView.setOnTouchReleased(event -> {
      if (tool == ToolEnum.DRAW && mode == Mode.DRAWING) {
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
  
  private void setErrors(ArrayList<MontiCoreException> errors) {
    errorList1.clear();
    errorList1 = errors;
  }
  
  private void addErrors(ArrayList<MontiCoreException> errors) {
	errorList1.addAll(errors);
  }
  
  public ArrayList<MontiCoreException> getErrors(){
    return errorList1;
  }
  // ------------ Init Buttons -------------------------------------------
  private void initToolBarActions() {
//    Image image = new Image("/icons/classw.png");
//    createBtn.setGraphic(new ImageView(image));
//    createBtn.setText("");
//    
//    image = new Image("/icons/packagew.png");
//    packageBtn.setGraphic(new ImageView(image));
//    packageBtn.setText("");
   
    Image image = new Image("/icons/edgew.png");
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
    
    image = new Image("/icons/recognizew.png");
    checkValidityBtn.setGraphic(new ImageView(image));
    checkValidityBtn.setText("Check");
        
    image = new Image("/icons/generatew.png");
    generateBtn.setGraphic(new ImageView(image));
    generateBtn.setText("Generate");
    generateBtn.setDisable(true);
    
    image = new Image("/icons/editinfow.png");
    editInfoBtn.setGraphic(new ImageView(image));
    editInfoBtn.setText("MontiArc - Model Configuration");
    
    image = new Image("/icons/showerrorlogw.png");
    showErrorLogBtn.setGraphic(new ImageView(image));
    showErrorLogBtn.setText("Show errors");
    
    buttonInUse = selectBtn;
    buttonInUse.getStyleClass().add("button-in-use");
    
    // ---------------------- Actions for buttons ----------------------------
    
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
      System.out.println("Before undo");
      undoManager.undoCommand();
      });
    
    redoBtn.setOnAction(event -> {
      undoManager.redoCommand();
      
    });
    
    deleteBtn.setOnAction(event -> deleteSelected());
    
    recognizeBtn.setOnAction(event -> {
      ArrayList errors = recognizeController.recognize(selectedSketches);
      if (!errors.isEmpty()) {
      	setErrors(errors);
      }
      else {
    	ArrayList<MontiCoreException> tmp = new ArrayList<MontiCoreException>();
        tmp.add(new allFine());
        setErrors(tmp);
      }
    	
    });
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
      ArrayList<MontiCoreException> tmp1 = new ArrayList<MontiCoreException>();
      tmp1.add(new allFine());
      setErrors(tmp1);  	
      ArrayList errors2 = checkPortTypes();
      ArrayList errors3 = checkCompNames();
      System.out.println("Errors2 " + errors2.toString());
      for (Object e : errors2) {
    	System.out.println("PortNodeException " + ((PortNode)((PortTypeException)e).getNode()).getId());
    	System.out.println("PortNodeException " + ((PortNode)((PortTypeException)e).getNode()).getTitle());  
      }
      ArrayList<MontiCoreException> overallErrors = new ArrayList<MontiCoreException>();
      if (!errors2.isEmpty()) {
    	overallErrors.addAll(errors2);
      }
      if(!errors3.isEmpty()) {
    	overallErrors.addAll(errors3);
      }
      if (!overallErrors.isEmpty()) {
        setErrors(overallErrors);
	  }
      else {
	    checkPortNames();
	    ArrayList errors1 = checkPortsCompatibility();
	    System.out.println("Errors1 " + errors1.toString());
	    if (!errors1.isEmpty()) {
	      setErrors(errors1);
	    }
	    else {
	      
	      ArrayList<String> arg = new ArrayList<String>();
	      arg.add(modelName);
	      arg.add(genericsString);
	      MontiArcPlugin plug = plugin.getInstance();
	      ASTNode node = plug.shapeToAST(graph, arg);
	      ArrayList<MontiCoreException> errors = (ArrayList<MontiCoreException>) plug.check(node, getNodeMap());
	      if (errors.isEmpty()) {
	    	ArrayList<MontiCoreException> tmp = new ArrayList<MontiCoreException>();
	        tmp.add(new allFine());
	        setErrors(tmp);
          }
	      else {
	    	setErrors(errors);
	  	  }
	    }
      }
      for (MontiCoreException el : errorList1) {
    	if (el instanceof allFine) {
    	  generateBtn.setDisable(false);	
    	}
      }
      
    });
    
    editInfoBtn.setOnAction(event -> {
      showSomething();
    });
    
    showErrorLogBtn.setOnAction(event -> {
      if (errorList1.isEmpty()) {
        ArrayList<MontiCoreException> tmp = new ArrayList<MontiCoreException>();
        tmp.add(new allFine());
        showErrorLog(tmp);
      }
      else {
        showErrorLog(errorList1);
      }
    });
    
    topBox.setOnMouseClicked(event -> this.showSomething());
    
    generateBtn.setOnAction(event -> {
      boolean good = true;	
      for (MontiCoreException e: errorList1) {
    	if (!(e instanceof allFine)) {
    	  good = false;  	
    	}
      }
      if (good == true) {
        MontiArcPlugin plug = plugin.getInstance();
        plug.generateCode(null, null, null);
        showErrorLog(plug.getGenErrorList());
        plug.clearGenErrorList();
      }
    });
  }
  
  void deleteSelected() {
    CompoundCommand command = new CompoundCommand();
    for (AbstractNodeView nodeView : selectedNodes) {
      if (nodeView instanceof ComponentNodeView) {
    	for (PortNodeView pView: ((ComponentNodeView)nodeView).getPortNodeViews()) {
    	  deleteNode(pView, command, false, false);	
    	}
        deleteNode(nodeView, command, false, false);
      }
      else {
        deleteNode(nodeView, command, false, false);
      }
    }
    for (AbstractEdgeView edgeView : selectedEdges) {
      deleteEdgeView(edgeView, command, false, false);
    }
    ArrayList<Sketch> deletedSketches = new ArrayList<Sketch>();
    for (Sketch sketch : selectedSketches) {
      deletedSketches.add(sketch);
    }
    Iterator<Sketch> itr = deletedSketches.iterator();
    while (itr.hasNext()) {
      Sketch element = (Sketch) itr.next();
      deleteSketch(element, command, false);
    }
    selectedNodes.clear();
    selectedEdges.clear();
    selectedSketches.clear();
    undoManager.add(command);
    }
    

  private ArrayList checkCompNames() {
	ArrayList<MontiCoreException> errorList = new ArrayList();
	if (packageName.equals("")) {
	  errorList.add(new PackageNameMissing());	
	}
	for (AbstractNode node : graph.getAllNodes()) {
	  if (node instanceof ComponentNode) {
		System.out.println("NODETITLE IS " + node.getTitle());
		if (((ComponentNode)node).getTitle() == null ||((ComponentNode)node).getTitle().equals("")) {
		  errorList.add(new InnerNameMissingException(node, getNodeView(node, getNodeMap())));	
		}
		else if(((ComponentNode)node).getTitle() != null && Character.isLowerCase(((ComponentNode)node).getTitle().charAt(0))) {
	      errorList.add(new InnerNameLow(node,getNodeView(node, getNodeMap())));	
		}
		if(node.getTitle().equals(modelName)) {
		  errorList.add(new ModelNameError(node,getNodeView(node, getNodeMap())));	
		}
		for (AbstractNode n1 : graph.getAllNodes()) {
		  if (n1 instanceof ComponentNode) {
			if (n1 != node) {
			  if (((ComponentNode)n1).getTitle().equals(((ComponentNode)node).getTitle() )
				&& ((ComponentNode)n1).getSubName().equals(((ComponentNode)node).getSubName())
				) {
			    errorList.add(new InstanceDuplicateError(node,getNodeView(node, getNodeMap()), n1,getNodeView(n1, getNodeMap())));	  
			  }
			}
		  }
		}
	  }
	}
	return errorList;
}

private ArrayList<MontiCoreException> checkPortsCompatibility() {
    ArrayList<MontiCoreException> errorList = new ArrayList();
    for (Edge edge : graph.getAllEdges()) {
      if (edge instanceof ConnectorEdge) {
        System.out.println("type " + ((ConnectorEdge)edge).getStartPort().getPortType());
        System.out.println("type " + ((ConnectorEdge)edge).getEndPort().getPortType());
        System.out.println("Edge " + edge.toString());
        if (!((ConnectorEdge)edge).getStartPort().getPortType().equals(((ConnectorEdge)edge).getEndPort().getPortType())) {
          for (AbstractEdgeView view : allEdgeViews) {
            if (view.getRefEdge() == edge) {
              System.out.println("view " + view.toString());
              if (view instanceof ConnectorEdgeView) {
                errorList.add(new ConnectorNotCompatibleException(edge, view));
              }
            }
          }
          
        }
      }
    }
    return errorList;
    // TODO Auto-generated method stub
    
  }
  
  private ArrayList<MontiCoreException> checkPortTypes() {
	ArrayList<MontiCoreException> errorList = new ArrayList();
	for (AbstractNode node : graph.getAllNodes()) {
	  if (node instanceof PortNode) {
		if (((PortNode)node).getPortType().equals("")) {
		  errorList.add(new PortTypeException(node, getNodeView(node, getNodeMap())));	
		}
	  }
	}
    return errorList;
  }
  
  private AbstractNodeView getNodeView(AbstractNode node, HashMap<AbstractNodeView, AbstractNode> arg1) {
	for (AbstractNodeView nodeView : arg1.keySet()) {
	  if (arg1.get(nodeView) == node) {
	    return nodeView;
	  }
	}
	return null;
  }

  public AbstractNodeView createNodeView(AbstractNode node, boolean remote) {
    AbstractNodeView newView = null;
    if (node instanceof ComponentNode) {
      newView = new ComponentNodeView((ComponentNode) node);
    }
    else if (node instanceof PortNode) {
      for (AbstractNodeView view : allNodeViews) {
        if(nodeMap.get(view) ==  (ComponentNode)((PortNode) node).getComponentNode()) {
          newView = ((ComponentNodeView)view).getNodeToViewMap().get(node);
          break;
        }
      } 
    }
    if (!graph.getAllNodes().contains(node)) {
      graph.addNode(node, remote);
      undoManager.add(new AddDeleteNodeCommand((AbstractDiagramController)MontiArcController.this, graph, newView, node, true));
    }
    System.out.println("newView " + newView);
    System.out.println("node " + node);
    return addNodeView(newView, node);
  }
  
  
  public AbstractEdgeView addEdgeView(AbstractEdge edge, boolean remote) {
    PortNodeView startNodeView = null;
    PortNodeView endNodeView = null;
    AbstractNode tempNode;

    for (AbstractNodeView nodeView : allNodeViews) {
      tempNode = nodeMap.get(nodeView);
      if (edge.getStartNode().getId().equals(tempNode.getId())) {
        edge.setStartNode(tempNode);
        startNodeView = (PortNodeView) nodeView;
      }
      else if (edge.getEndNode().getId().equals(tempNode.getId())) {
        edge.setEndNode(tempNode);
        endNodeView = (PortNodeView) nodeView;
      }  
    }    
    AbstractEdgeView edgeView;
    edgeView = new ConnectorEdgeView((ConnectorEdge)edge, startNodeView, endNodeView);
    
    allEdgeViews.add(edgeView);
    drawPane.getChildren().add(edgeView);
    if (!graph.getAllEdges().contains(edge)) {
      graph.addEdge(edge, remote);
    }
    return edgeView;
  }
  
  public AbstractEdgeView createEdgeView(AbstractEdge edge, PortNodeView startNodeView, PortNodeView endNodeView) {
    AbstractEdgeView edgeView;

    if (edge instanceof ConnectorEdge) {
      edgeView = new ConnectorEdgeView((ConnectorEdge)edge, startNodeView, endNodeView);
    }
    else {
      edgeView = null;
    }
    return addEdgeView(edgeView);
  }

  public String getTabControllerName() {
    return "MontiArcController";
  }
  
  
  void drawSelected() {
    for (AbstractNodeView nodeView : allNodeViews) {
      if (selectedNodes.contains(nodeView)) {
        nodeView.setSelected(true);
      }
      else {
        nodeView.setSelected(false);
      }
    }
    for (AbstractEdgeView edgeView : allEdgeViews) {
      if (selectedEdges.contains(edgeView)) {
        ((ConnectorEdgeView)edgeView).setSelected(true);
      }
      else {
        ((ConnectorEdgeView)edgeView).setSelected(false);
      }
    }
    for (Sketch sketch : graph.getAllSketches()) {
      if (selectedSketches.contains(sketch)) {
        sketch.setSelected(true);
        sketch.getPath().toFront();
      }
      else {
        sketch.setSelected(false);
        sketch.getPath().toFront();
      }
    }
  }
  
  public void checkPortNames() {
    for (AbstractNode node: graph.getAllNodes()) {
      if(node instanceof PortNode) {
        if (node.getTitle() == ""|| node.getTitle() == null || node.getTitle().isEmpty()) {
          String name = ((PortNode)node).getPortType();
          name = name.replaceAll("([A-Z])", "$1").toLowerCase();
          node.setTitle(name);
        }
      }
    }
  }
  
  
  
}
