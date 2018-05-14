package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._ast.ASTMACompilationUnit;

import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.lang.montiarc.montiarc._ast.*;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.ast.ASTNode;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import javafx.stage.Stage;
import controller.AbstractDiagramController;

import model.Graph;
import model.GraphElement;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;

public class MontiArcPlugin implements MontiCorePlugIn {
	
  public AbstractDiagramController getController() {
    return null;
  }
  
  public String getView() {
	  String view= "montiArcView";
	  return view;
  }
  
  public String getDSLName() {
	  String DSLName = "MontiArc";
	  return DSLName;
  }
  
  public String getFileEnding() {
	  return MontiArcLanguage.FILE_ENDING;
  }
  
  public String getDSLPicture() {
    return null;
  }
  
    
//  ASTMACompilationUnit shapeToAST(Graph g) {
//    ASTMACompilationUnit ast = new ASTMACompilationUnit();
//	  List<GraphElement> elements	= g.getAllGraphElements();
//    for(GraphElement e : elements) {
//      if(e instanceof ComponentNode) {
//    	List<PortNode> ports = ((ComponentNode) e).getPorts();
//    	ArrayList<String> astPorts = new ArrayList<>(); 
//    	String astPortList = "";
//    	if(ports.size() == 0) {	
//    	    // error message. Is not allowed
//    	  } 
//    	  else if(ports.size() == 1) {
//    		astPorts.add( " default " + ports.get(0).toString());
//    		astPortList = " port " + astPorts;
//    	  }
//    	  else {
//    		for (PortNode p : ports) {
//    		  astPorts.add( " default " + p.toString());
//        	}
//    		astPortList = " ports " + astPorts;
//    	  }
//    	
//        ASTComponent aComponent = new ASTComponent(  );
////        new ASTStereotype(), e.getId().toString(), 
////        " Buffer1 " ," Component ", " Buffer2 ", astPortList
//      }
//    }
//    return null;
//  }
  
  
  
  public String getGenerator() {
	  return "MAAGenerator";  
  }

  @Override
  public TypesPrettyPrinterConcreteVisitor getPrettyPrinter() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ASTNode getASTNode() {
    
    return null;
  }

  @Override
  public ASTNode shapeToAST(Graph graph, String modelName) {
    
    MontiArcController controller = new MontiArcController();
    ArrayList<String> generics = controller.getGenerics();
    ArrayList<String> types = controller.getTypes();
    
    // create generic type params
    List<de.monticore.types.types._ast.ASTTypeVariableDeclaration> genericsTypes = null;
    for (String g : generics) {
      String name = "";
      List<String> upperBounds = null;
      if (g.split("extends").length > 1) {
        name = (g.split("extends")[0]).replaceAll("\\s+","");
        for(String u : g.split("extends")[1].split(",")) {
          upperBounds.add(u.replaceAll("\\s+", ""));
        }
        List<de.monticore.types.types._ast.ASTSimpleReferenceType> simpleReferenceTypes = (List<de.monticore.types.types._ast.ASTSimpleReferenceType>) TypesNodeFactory.createASTSimpleReferenceType(upperBounds, null);
        de.monticore.types.types._ast.ASTComplexReferenceType complexReferenceTypes = TypesNodeFactory.createASTComplexReferenceType(simpleReferenceTypes);
        de.monticore.types.types._ast.ASTTypeVariableDeclaration typeVariableDec = TypesNodeFactory.createASTTypeVariableDeclaration(name, (List<de.monticore.types.types._ast.ASTComplexReferenceType>) complexReferenceTypes);
        genericsTypes.add(typeVariableDec);
      }
      else {
        name = g.replaceAll("\\s+", "");
      }
    }
    de.monticore.types.types._ast.ASTTypeParameters genericTypeParameters = TypesNodeFactory.createASTTypeParameters(genericsTypes);
    
    //create types param
    List<de.monticore.lang.montiarc.common._ast.ASTParameter> parameters = null;
    for (String t : types) {
      de.monticore.types.types._ast.ASTTypeVariableDeclaration varDec = TypesNodeFactory.createASTTypeVariableDeclaration(t.split("\\s+")[0], null);
      de.monticore.types.types._ast.ASTType type = (ASTType) TypesNodeFactory.createASTTypeParameters((List<ASTTypeVariableDeclaration>) varDec);
      String typeName = t.split("\\s+")[1];
      de.monticore.lang.montiarc.common._ast.ASTParameter parameter = MontiArcNodeFactory.createASTParameter(type, typeName, null);
      parameters.add(parameter);    
    }
    
    de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit ast = 
        MontiArcNodeFactory.createASTMACompilationUnit();
    de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead astHead = 
        MontiArcNodeFactory.createASTComponentHead(genericTypeParameters, parameters, null);
    for (AbstractNode g : graph.getAllNodes()) {
      if (g instanceof ComponentNode) {
        // dann muessen wir das in den Body einbauen, also einen ComponentNode erzeugen
        // hier machen wir nachher weiter
      }
    }
    List<de.monticore.lang.montiarc.montiarc._ast.ASTElement> elements = null;
    de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody = 
        MontiArcNodeFactory.createASTComponentBody(elements);
    de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComp = 
        MontiArcNodeFactory.createASTComponent(null, modelName, astHead, null, null, astBody);
    ast.setComponent(astComp);
    return null;
  }

  @Override
  public List<String> check(ASTNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean generateCode(ASTNode node, String path) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getFlagName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addUMLFlag(String modelname) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public List<String> showContainerInfoDialog(Stage stage) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
