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
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTQualifiedName.*;
import de.monticore.lang.montiarc.common._ast.ASTParameter;
import de.monticore.lang.montiarc.montiarc._ast.*;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import javafx.stage.Stage;
import controller.AbstractDiagramController;

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
    
    MontiArcController controller = new MontiArcController();
    ArrayList<String> generics = controller.getGenerics();
    ArrayList<String> types = controller.getTypes();
    
    // create AST
    de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit ast = 
        MontiArcNodeFactory.createASTMACompilationUnit();
        
    // create generic type params
    ArrayList<de.monticore.types.types._ast.ASTTypeVariableDeclaration> genericsTypes = new ArrayList<ASTTypeVariableDeclaration>();
    for (String g : generics) {
      String name = "";
      ArrayList<String> upperBounds = new ArrayList<String>();
      if (g.split("extends").length > 1) {
        name = (g.split("extends")[0]).replaceAll("\\s+","");
        for(String u : g.split("extends")[1].split(",")) {
          upperBounds.add(u.replaceAll("\\s+", ""));
        }
        List<de.monticore.types.types._ast.ASTSimpleReferenceType> simpleReferenceTypes = 
            (List<de.monticore.types.types._ast.ASTSimpleReferenceType>) TypesNodeFactory.createASTSimpleReferenceType(upperBounds, null);
        de.monticore.types.types._ast.ASTComplexReferenceType complexReferenceTypes = 
            TypesNodeFactory.createASTComplexReferenceType(simpleReferenceTypes);
        de.monticore.types.types._ast.ASTTypeVariableDeclaration typeVariableDec = 
            TypesNodeFactory.createASTTypeVariableDeclaration(name, 
                (List<de.monticore.types.types._ast.ASTComplexReferenceType>) complexReferenceTypes);
        genericsTypes.add(typeVariableDec);
      }
      else {
        name = g.replaceAll("\\s+", "");
      }
    }
    de.monticore.types.types._ast.ASTTypeParameters genericTypeParameters = 
        TypesNodeFactory.createASTTypeParameters(genericsTypes);
    
    //create types param
    ArrayList<de.monticore.lang.montiarc.common._ast.ASTParameter> parameters = new ArrayList<ASTParameter>();
    for (String t : types) {
      de.monticore.types.types._ast.ASTTypeVariableDeclaration varDec = 
          TypesNodeFactory.createASTTypeVariableDeclaration(t.split("\\s+")[0], null);
      de.monticore.types.types._ast.ASTType type = 
          (ASTType) TypesNodeFactory.createASTTypeParameters((List<ASTTypeVariableDeclaration>) varDec);
      String typeName = t.split("\\s+")[1];
      de.monticore.lang.montiarc.common._ast.ASTParameter parameter = 
          MontiArcNodeFactory.createASTParameter(type, typeName, null);
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
        builder.values((List<de.monticore.common.common._ast.ASTStereoValue>) value);
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
          
          de.monticore.types.types._ast.ASTTypeVariableDeclaration varDecPort = TypesNodeFactory.createASTTypeVariableDeclaration(p.getPortType(), null);
          de.monticore.types.types._ast.ASTType typePort = (ASTType) TypesNodeFactory.createASTTypeParameters((List<ASTTypeVariableDeclaration>) varDecPort);
          
          // create astPort for each PortNode of the current ComponentNode
          de.monticore.lang.montiarc.montiarc._ast.ASTPort astPort = 
              MontiArcNodeFactory.createASTPort(null, typePort, p.getTitle(), outgoing, incoming);
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
              List<de.monticore.types.types._ast.ASTQualifiedName> targets = (List<ASTQualifiedName>) builderQualifiedNameTargets.build();
              
              de.monticore.lang.montiarc.montiarc._ast.ASTConnector astConnector =
                  MontiArcNodeFactory.createASTConnector(null, source, targets);
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
