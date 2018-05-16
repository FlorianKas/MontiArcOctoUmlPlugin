package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._ast.ASTMACompilationUnit;
import de.monticore.common.common._ast.ASTStereoValue;
import de.monticore.common.common._ast.ASTStereotype;
import de.monticore.common.common._ast.ASTStereotype.Builder;
import de.monticore.common.common._ast.CommonNodeFactory;
import de.monticore.types.types._ast.ASTComplexArrayType;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTQualifiedName.*;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.lang.montiarc.common._ast.ASTParameter;
import de.monticore.lang.montiarc.montiarc._ast.*;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import javafx.stage.Stage;
import controller.AbstractDiagramController;
import de.monticore.lang.montiarc.helper.SymbolPrinter;
import model.Graph;
import model.GraphElement;
import model.edges.ConnectorEdge;
import model.edges.Edge;
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
    
    ArrayList<String> generics = MontiArcController.genericsArray;
    ArrayList<String> types = MontiArcController.types;
    
    // create AST
    de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit ast = 
        MontiArcNodeFactory.createASTMACompilationUnit();
        
    // create generic type params
    ArrayList<de.monticore.types.types._ast.ASTTypeVariableDeclaration> genericsTypes = new ArrayList<ASTTypeVariableDeclaration>();
    System.out.println("Generics looks as follows" + generics);
    for (String g : generics) {
      String name = "";
      ArrayList<String> upperBounds = new ArrayList<String>();
      if (g.split("extends").length > 1) {
        name = (g.split("extends")[0]).replaceAll("\\s+","");
        for(String u : g.split("extends")[1].split(",")) {
          upperBounds.add(u.replaceAll("\\s+", ""));
        }
        ArrayList<de.monticore.types.types._ast.ASTSimpleReferenceType> simpleReferenceTypes = new ArrayList<de.monticore.types.types._ast.ASTSimpleReferenceType>();
        de.monticore.types.types._ast.ASTSimpleReferenceType simpleReferenceType =    
            TypesNodeFactory.createASTSimpleReferenceType(upperBounds, null);
        simpleReferenceTypes.add(simpleReferenceType);
        ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType> complexReferenceTypes = 
            new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
        de.monticore.types.types._ast.ASTComplexReferenceType complexReferenceType = 
            TypesNodeFactory.createASTComplexReferenceType(simpleReferenceTypes);
        complexReferenceTypes.add(complexReferenceType);
        de.monticore.types.types._ast.ASTTypeVariableDeclaration typeVariableDec = 
            TypesNodeFactory.createASTTypeVariableDeclaration(name, 
                (List<de.monticore.types.types._ast.ASTComplexReferenceType>) complexReferenceTypes);
        genericsTypes.add(typeVariableDec);
        System.out.println("typeVariableDec" + typeVariableDec.toString());
      }
      else {
        name = g.replaceAll("\\s+", "");
      }
    }
    de.monticore.types.types._ast.ASTTypeParameters genericTypeParameters = 
        TypesNodeFactory.createASTTypeParameters(genericsTypes);
    
    //create types param
    ArrayList<de.monticore.lang.montiarc.common._ast.ASTParameter> parameters = new ArrayList<ASTParameter>();
    System.out.println("Types looks as follows" + types);
    for (String t : types) {
      ArrayList<String> names = new ArrayList<String>();
      names.add(t.split("\\s+")[1]);
      de.monticore.types.types._ast.ASTSimpleReferenceType varDec = 
          TypesNodeFactory.createASTSimpleReferenceType(names, null);
      String typeName = t.split("\\s+")[1];
      System.out.println("typeName " + typeName);
      de.monticore.lang.montiarc.common._ast.ASTParameter parameter = 
          MontiArcNodeFactory.createASTParameter(varDec, typeName, null);
      parameters.add(parameter);    
    }
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTElement> elements = new ArrayList<ASTElement>();
    de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComponent = null;
    
    // create outermost component head
    de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead astHead = 
        MontiArcNodeFactory.createASTComponentHead(genericTypeParameters, parameters, null);
    
    // create params for outermost component body
    for (AbstractNode g : graph.getAllNodes()) {
      if (g instanceof ComponentNode) {
        de.monticore.common.common._ast.ASTStereotype.Builder builder = new Builder();
        ASTStereoValue value = CommonNodeFactory.createASTStereoValue(((ComponentNode) g).getStereotype(), null);
        ArrayList<ASTStereoValue> valueList = new ArrayList<ASTStereoValue>();
        valueList.add(value);
        builder.values(valueList);
        de.monticore.common.common._ast.ASTStereotype stereotype = builder.build();
        de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead head = MontiArcNodeFactory.createASTComponentHead();
        ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTElement> bodyElements = new ArrayList<ASTElement>();
        
        ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> astPorts = new ArrayList<ASTPort>();
        // create AstPorts
        for (PortNode p : ((ComponentNode) g).getPorts()) {
          boolean incoming = false;
          boolean outgoing = true;
          if (p.getPortDirection() == "in") {
            incoming = true;
            outgoing = false;
          }
          else {
            incoming = false;
            outgoing = true;
          }
          
          
          ArrayList<String> namesPort = new ArrayList<String>();
          namesPort.add(p.getPortType());
          de.monticore.types.types._ast.ASTSimpleReferenceType varDecPort = 
              TypesNodeFactory.createASTSimpleReferenceType(namesPort, null);
          // ich glaube ich muss hier nicht einen solchen Type createn, sondern checken, ob er existiert oder?
          
//          ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType> upperBounds = 
//              new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
//          de.monticore.types.types._ast.ASTTypeVariableDeclaration varDecPort = 
//              TypesNodeFactory.createASTTypeVariableDeclaration(p.getPortType(), upperBounds);
//          ArrayList<ASTTypeVariableDeclaration> varDecPortList = new ArrayList<ASTTypeVariableDeclaration>();
//          varDecPortList.add(varDecPort);
//          System.out.println("varDecPortList " + varDecPortList.toString());
//          de.monticore.types.types._ast.ASTTypeParameters typePort = 
//               TypesNodeFactory.createASTTypeParameters(varDecPortList);
          
          // create astPort for each PortNode of the current ComponentNode
          de.monticore.lang.montiarc.montiarc._ast.ASTPort astPort = 
              MontiArcNodeFactory.createASTPort(null, varDecPort, p.getTitle(), outgoing, incoming);
          // add astPort to List
          astPorts.add(astPort);
        }
        
        // create astInterface (containing the ports) for each ComponentNode
        de.monticore.lang.montiarc.montiarc._ast.ASTInterface astInterface = 
            MontiArcNodeFactory.createASTInterface(null, astPorts);
        // add astInterface to bodyList of current astComponent
        bodyElements.add(astInterface);
        
        
        // add Connectors for this Component
        // TODO ConnectorEdge anpassen auf List of targets
        for (Edge e: graph.getAllEdges()) {
          if (e instanceof ConnectorEdge) {
            if (((PortNode)e.getStartNode()).getComponentNode() == g ||
                ((PortNode)e.getEndNode()).getComponentNode() == g) {
              de.monticore.types.types._ast.ASTQualifiedName.Builder builderQualifiedNameSource = 
                  new de.monticore.types.types._ast.ASTQualifiedName.Builder();
              ArrayList<String> title = new ArrayList<String>();
              title.add(e.getStartNode().getTitle());
              builderQualifiedNameSource.parts(title);
              de.monticore.types.types._ast.ASTQualifiedName source = builderQualifiedNameSource.build();
              
              de.monticore.types.types._ast.ASTQualifiedName.Builder builderQualifiedNameTargets = 
                  new de.monticore.types.types._ast.ASTQualifiedName.Builder();
              ArrayList<String> titleTargets = new ArrayList<String>();
              titleTargets.add(e.getEndNode().getTitle());
              builderQualifiedNameTargets = builderQualifiedNameTargets.parts(titleTargets);
              ArrayList<de.monticore.types.types._ast.ASTQualifiedName> targets = 
                  new ArrayList<de.monticore.types.types._ast.ASTQualifiedName>();
              de.monticore.types.types._ast.ASTQualifiedName target = builderQualifiedNameTargets.build();
              targets.add(target);
              de.monticore.lang.montiarc.montiarc._ast.ASTConnector astConnector =
                  MontiArcNodeFactory.createASTConnector(null, source, targets);
              System.out.println("astConnector PrettyPrint Test" + astConnector.getPrettyPrinter().toString());
              System.out.println("astConnector " + astConnector.getSource() + astConnector.getTargets() + astConnector.getStereotype());
              bodyElements.add(astConnector);
            }
          }
        }
     // create astBody for each ComponentNode
        de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody = 
            MontiArcNodeFactory.createASTComponentBody(bodyElements);
        
        de.monticore.types.types._ast.ASTTypeArguments typeArgs = 
            MontiArcNodeFactory.createASTTypeArguments();
        
        // create astComponents for each ComponentNode
        astComponent = 
            MontiArcNodeFactory.createASTComponent(stereotype, g.getTitle(), head, "", typeArgs, astBody);
       
        
        System.out.println("astComponent Head looks as follows: " + astComponent.getHead());
        System.out.println("astComponent Body looks as follows: " + astComponent.getBody().getElements().toString());
        
      // add each astComponent to elementsList (the list for the body of the outermost component)
        elements.add(astComponent);
      
      }  
    }
    
//    ArrayList<Edge> edges = (ArrayList<Edge>) graph.getAllEdges();
//    while(edges.isEmpty()) {
//      Edge e = edges.get(0);
//      ArrayList<ConnectorEdge> tmp = new ArrayList<ConnectorEdge>();
//      if (e instanceof ConnectorEdge) {
//        for (Edge e2 : graph.getAllEdges()) {
//          if (e.getId() != e2.getId() && e.getStartNode() == e2.getStartNode()) {
//            tmp.add((ConnectorEdge) e2);  
//          }
//        }
//        
//        de.monticore.types.types._ast.ASTQualifiedName.Builder builderQualifiedNameSource = new de.monticore.types.types._ast.ASTQualifiedName.Builder();
//        ArrayList<String> title = new ArrayList<String>();
//        title.add(e.getStartNode().getTitle());
//        builderQualifiedNameSource.parts(title);
//        de.monticore.types.types._ast.ASTQualifiedName source = builderQualifiedNameSource.build();
//        
//        de.monticore.types.types._ast.ASTQualifiedName.Builder builderQualifiedNameTargets = new de.monticore.types.types._ast.ASTQualifiedName.Builder();
//        ArrayList<String> titleTargets = new ArrayList<String>();
//        for (Edge t : tmp) {
//          titleTargets.add(t.getEndNode().getTitle());
//        }
//        builderQualifiedNameTargets = builderQualifiedNameTargets.parts(titleTargets);
//        List<de.monticore.types.types._ast.ASTQualifiedName> targets = (List<ASTQualifiedName>) builderQualifiedNameTargets.build();
//        
//        de.monticore.lang.montiarc.montiarc._ast.ASTConnector astConnector =
//            MontiArcNodeFactory.createASTConnector(null, source, targets);
//        bodyElements.add(astConnector);
//      }
//      else {
//        edges.remove(e);
//      }
//      for (Edge t : tmp) {
//        edges.remove(t);
//      }
//    }
//    
    // create componentBody of outermost component
    de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody = 
        MontiArcNodeFactory.createASTComponentBody(elements);
    // create outermost component
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
