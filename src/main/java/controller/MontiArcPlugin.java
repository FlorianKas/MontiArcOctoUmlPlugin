package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._ast.ASTMACompilationUnit;


import de.monticore.ast.ASTNode;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.prettyprint.IndentPrinter;

import controller.AbstractDiagramController;

import model.Graph;
import model.GraphElement;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
//implements MontiCorePlugIn 
public class MontiArcPlugin {
//	
//  void checkValidity(Graph g) {
//	  ASTNode node = shapeToAST(g);
//  }
//	
//  AbstractDiagramController getController() {
//  
//  }
//  
//  String getView() {
//	  String view= "montiArcView";
//	  return view;
//  }
//  
//  String getDSLName() {
//	  String DSLName = "MontiArc";
//	  return DSLName;
//  }
//  
//  String getFileEnding() {
//	  return MontiArcLanguage.FILE_ENDING;
//  }
//  
//  String getDSLPicture() {
//  
//  }
//  
//  PrettyPrinter getPrinter() {
//  
//  }
//  
//  Class getASTNode() {
//  
//  }
//  
  ASTMACompilationUnit shapeToAST(Graph g) {
    ASTMACompilationUnit ast = new ASTMACompilationUnit();
	  List<GraphElement> elements	= g.getAllGraphElements();
    for(GraphElement e : elements) {
      if(e instanceof ComponentNode) {
    	List<PortNode> ports = ((ComponentNode) e).getPorts();
    	ArrayList<String> astPorts = new ArrayList<>(); 
    	String astPortList = "";
    	if(ports.size() == 0) {	
    	    // error message. Is not allowed
    	  } 
    	  else if(ports.size() == 1) {
    		astPorts.add( " default " + ports.get(0).toString());
    		astPortList = " port " + astPorts;
    	  }
    	  else {
    		for (PortNode p : ports) {
    		  astPorts.add( " default " + p.toString());
        	}
    		astPortList = " ports " + astPorts;
    	  }
    	
        ASTComponent aComponent = new ASTComponent(  );
//        new ASTStereotype(), e.getId().toString(), 
//        " Buffer1 " ," Component ", " Buffer2 ", astPortList
      }
    }
    return ;
  }
  
//  List<String> check(ASTMontiArcNode node){
//	  
//  
//  }
//  
//  Boolean generate(ASTNode n, String path) {
//  
//  }
//  
//  String getGenerator() {
//	  return "MAAGenerator";  
//  }
//  
}
