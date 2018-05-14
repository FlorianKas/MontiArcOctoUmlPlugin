package controller;


import java.util.List;

import controller.AbstractDiagramController;
import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import javafx.stage.Stage;
import model.Graph;

public interface MontiCorePlugIn {
  // returns the diagram controller
  AbstractDiagramController getController();
  
  // returns the name of the fxml file
  String getView();
  
  // returns the name of the DSL
  String getDSLName();
  
  // returns the file ending to the DSL for saving models
  String getFileEnding();
  
  // returns the path to the picture
  String getDSLPicture();
  
  // returns the prettyprinter of the DSL
  TypesPrettyPrinterConcreteVisitor getPrettyPrinter();
  
  // returns the AST node
  ASTNode getASTNode();
  
  // returns the transformed (model to model) AST from the OctoUML Graph
  ASTNode shapeToAST(Graph graph, String modelName);
  
  // returns a list of errors
  List<String> check(ASTNode node);
  
  // returns the generator of the DSL
  String getGenerator();
  
  // returns true if generation of code was successful
  boolean generateCode(ASTNode node, String path);
  
  // returns the symbol for the UML flag
  String getFlagName();
  
  // adds the UML flag to the grid
  void addUMLFlag(String modelname);
  
  // show container info dialog for language
  List<String> showContainerInfoDialog(Stage stage);
}