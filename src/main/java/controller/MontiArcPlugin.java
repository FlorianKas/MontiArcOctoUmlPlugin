package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._ast.ASTMACompilationUnit;
import de.monticore.common.common._ast.ASTStereoValue;
import de.monticore.common.common._ast.ASTStereotype;
import de.monticore.common.common._ast.ASTStereotype.Builder;
import de.monticore.common.common._ast.CommonNodeFactory;
import de.monticore.java.javadsl._ast.ASTCompilationUnit;
import de.monticore.java.javadsl._ast.JavaDSLNodeFactory;
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
    IndentPrinter indent = new IndentPrinter();
    // TODO Auto-generated method stub
    return new MAPrettyPrinter(indent);
  }

  @Override
  public ASTNode getASTNode() {
    
    return null;
  }

  protected ArrayList<ASTTypeVariableDeclaration> createGenerics(ArrayList<String> generics) {
    // create List of TypeVariableDeclarations called TypeParameters
    ArrayList<de.monticore.types.types._ast.ASTTypeVariableDeclaration> genericsTypes = new ArrayList<ASTTypeVariableDeclaration>();
    System.out.println("Generics looks as follows" + generics);
// TODO TypeArgs for util.list<String,String> and not 3 simpleReferenceTypes 
    
    for (String g : generics) {
      System.out.println("g" + g);
   // create generic type params
      String name = "";
      ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType> upperBounds = 
          new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
      System.out.println("Length of g" + g.split("extends").length );
      if (g.split("extends").length > 1) {
        name = (g.split("extends")[0]).replaceAll("\\s+","");
        System.out.println("Name " + name);
        String[] supClasses = g.split("extends")[1].split("#");
        System.out.println("supClasses" + supClasses.length);
        for(String u : supClasses) {
          // divide supClasses at '.'
          String[] partsSmaller = u.split("<");
          System.out.println("partsSmaller" + partsSmaller.length);
          String withoutFirst = "";
          for (int i=1; i<partsSmaller.length; i++) {
            if (i > 1) {
              withoutFirst = withoutFirst + "<" +partsSmaller[i];
            }
            else {
              withoutFirst = withoutFirst + partsSmaller[i];
            }
          }
          System.out.println("WithoutFirst" + withoutFirst);
          String[] without = withoutFirst.split(">");
          String lastElement = without[without.length-1];
          String partsGreater[] = withoutFirst.split(">");
          System.out.println("Length of parts Greater " + partsGreater.length);
          String withoutLast = "";
          for (int i=0; i<partsGreater.length; i++) {
            System.out.println("partsGreater " + partsGreater[i]);
            if (i < partsGreater.length) {
              withoutLast = withoutLast + partsGreater[i] + ">" ;
            }
            else {
              withoutLast = withoutLast + partsGreater[i];
            }
          }
          System.out.println("withoutLast" + withoutLast);
          String[] partsTmp = withoutLast.split(",");
          String firstElement = partsSmaller[0];
          System.out.println("First Element " + firstElement);
          ArrayList<String> parts = new ArrayList<String>();
          for (int i=0 ; i <partsTmp.length; i++) {
            System.out.println("partsTmp " + partsTmp[i]);
            parts.add(partsTmp[i]);
          }
          System.out.println("parts" + parts.size());
          
          List<de.monticore.types.types._ast.ASTSimpleReferenceType> typeArgs = 
              new ArrayList<de.monticore.types.types._ast.ASTSimpleReferenceType>();
          int k = 0;
          int length = parts.size();
          while (k <length) {
            if (parts.get(k).contains("<") && parts.get(k).contains(">")) {
              if (parts.get(k).split("<").length == 0) {
                List<String> qn = new ArrayList<String>();
                for (String s : parts.get(k).split("\\.")) {
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  qn.add(s);
                }
                de.monticore.types.types._ast.ASTSimpleReferenceType typeArg = 
                    TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                System.out.println("typeArg" + typeArg);
                typeArgs.add(typeArg);
              }
              else {
                // create typeName 
                String typeName = parts.get(k).split("<")[0];
                List<String> qn = new ArrayList<String>();
                for (String s : typeName.split("\\.")) {
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  qn.add(s);
                }
                // create typeArgs
                String rest = parts.get(k).split("<")[1];
                List<String> restArgs = new ArrayList<String>();
                for (String s : rest.split("\\.")) {
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  restArgs.add(s);
                }
                // TODO Question if ASTSimpleReferenceType is a valid ASTTypeArgument
                de.monticore.types.types._ast.ASTSimpleReferenceType typeArg =    
                    TypesNodeFactory.createASTSimpleReferenceType(restArgs, null);
                List<de.monticore.types.types._ast.ASTTypeArgument> typeArgRest= 
                    new ArrayList<de.monticore.types.types._ast.ASTTypeArgument>();
                typeArgRest.add(typeArg);
                // TODO
                de.monticore.types.types._ast.ASTTypeArguments typeArguments =
                    TypesNodeFactory.createASTTypeArguments(typeArgRest);
                de.monticore.types.types._ast.ASTSimpleReferenceType add = 
                    TypesNodeFactory.createASTSimpleReferenceType(qn, typeArguments);
                System.out.println("typeArg" + add);
                
                typeArgs.add(add);
              }
//              parts.remove(k);
              k++;
            }
            else if (parts.get(k).contains("<")) {
              String nameQu = "";
              if(parts.get(k).split("<").length == 1) {
                nameQu = parts.get(k).split("<")[0];
                List<de.monticore.types.types._ast.ASTTypeArgument> tmpArgList = 
                    new ArrayList<de.monticore.types.types._ast.ASTTypeArgument>();
//                parts.remove(k);
                k++;
                while(!parts.get(k).contains(">")) {
                  List<String> qn = new ArrayList<String>();
                  for (String s : parts.get(k).split("\\.")) {
                    if (s.contains(">")) {
                      s= s.split(">")[0];
                    }
                    qn.add(s);
                  }
                  de.monticore.types.types._ast.ASTSimpleReferenceType add = 
                      TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                  tmpArgList.add(add);
//                  parts.remove(k);
                  k++;
                  
                }
                // the last one with ">"
                List<String> qn = new ArrayList<String>();
                for (String s : parts.get(k).split("\\.")) {
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  qn.add(s);
                }
                de.monticore.types.types._ast.ASTSimpleReferenceType add1 = 
                    TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                tmpArgList.add(add1);
                List<String> nameQuList = new ArrayList<String>();
                for (String s : nameQu.split("\\.")) {
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  nameQuList.add(s);
                }
                de.monticore.types.types._ast.ASTTypeArguments typesList = 
                    TypesNodeFactory.createASTTypeArguments(tmpArgList);
                de.monticore.types.types._ast.ASTSimpleReferenceType toBeAdded = 
                    TypesNodeFactory.createASTSimpleReferenceType(nameQuList, typesList);
                System.out.println("typeArg" + toBeAdded);
                
                typeArgs.add(toBeAdded);
                
              }
              else {
                List<de.monticore.types.types._ast.ASTSimpleReferenceType> tmpArgList = 
                    new ArrayList<de.monticore.types.types._ast.ASTSimpleReferenceType>();
  //              parts.remove(k);
                k++;
                while(!parts.get(k).contains(">")) {
                  List<String> qn = new ArrayList<String>();
                  for (String s : parts.get(k).split("\\.")) {
                    if (s.contains(">")) {
                      s= s.split(">")[0];
                    }
                    qn.add(s);
                  }
                  de.monticore.types.types._ast.ASTSimpleReferenceType add = 
                      TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                  tmpArgList.add(add);
  //                parts.remove(k);
                  k++;
                  
                }
                // the last one with ">"
                List<String> qn = new ArrayList<String>();
                for (String s : parts.get(k).split("\\.")) {
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  qn.add(s);
                }
                de.monticore.types.types._ast.ASTSimpleReferenceType add1 = 
                    TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                tmpArgList.add(add1);
                System.out.println("tmpArgList" + tmpArgList.toString());
                typeArgs.addAll(tmpArgList);
                k++;
              }
            }
            else {
              List<String> onlyName = new ArrayList<String>();
              if (parts.get(k).split(".").length > 0) {
                for (String s : parts.get(k).split(".")) {
                  System.out.println("Add " + s);
                  if (s.contains(">")) {
                    s= s.split(">")[0];
                  }
                  onlyName.add(s);
                }
              }
              else {
                onlyName.add(parts.get(k));
              }
              de.monticore.types.types._ast.ASTSimpleReferenceType lastAdd = 
                  TypesNodeFactory.createASTSimpleReferenceType(onlyName, null);
              System.out.println("lastAdd" +lastAdd.toString());
              typeArgs.add(lastAdd);
              
              k++;
            }
          }
          
          // now we handle the first and the last one 
          System.out.println("firstElement" + firstElement);
          System.out.println("util " + firstElement.split("\\.")[0]);
          System.out.println("list " + firstElement.split("\\.")[1]);
          String[] qualifier = firstElement.split("\\.");
          System.out.println("qualifier length " +qualifier.length);
          ArrayList<String> qualifiers = new ArrayList<String>();
          for (String s:qualifier) {
            System.out.println("s " + s);
            if (s.contains(">")) {
              s= s.split(">")[0];
            }
            qualifiers.add(s);
          }
          de.monticore.types.types._ast.ASTSimpleReferenceType first = 
              TypesNodeFactory.createASTSimpleReferenceType(qualifiers, null);
          System.out.println("First element to add " + first.toString());
          typeArgs.add(0,first);
          System.out.println("typeArgs " + typeArgs.toString());
          de.monticore.types.types._ast.ASTComplexReferenceType bla = 
              TypesNodeFactory.createASTComplexReferenceType(typeArgs);
          List<de.monticore.types.types._ast.ASTComplexReferenceType> upper= 
              new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
          upper.add(bla);
          de.monticore.types.types._ast.ASTTypeVariableDeclaration varDec = 
              TypesNodeFactory.createASTTypeVariableDeclaration(name, upper);
          genericsTypes.add(varDec);
        }   
      }  
    }
    return genericsTypes;    
  }
  
  
  @Override
  public ASTNode shapeToAST(Graph graph, String modelName) {
    
    ArrayList<String> generics = MontiArcController.genericsArray;
    ArrayList<String> types = MontiArcController.types;
    // temporary
    ArrayList<String> imports = new ArrayList<String>();
    // temporary
    String packageDec = "bla.bla2.bla3;";
    List<de.monticore.types.types._ast.ASTImportStatement> importDecls = 
        new ArrayList<de.monticore.types.types._ast.ASTImportStatement>();
    for (String s : imports) {
      boolean star = false;
      if (s.contains("*")) {
        star = true;
        s = s.split("*")[0];
      }
      String[] qualifiers = s.split("\\.");
      ArrayList<String> qual = new ArrayList<String>();
      for (String t : qualifiers) {
        qual.add(t);
      }
      de.monticore.types.types._ast.ASTImportStatement importDec = 
          TypesNodeFactory.createASTImportStatement(qual, star);
      System.out.println("Imports " + importDec.toString());
      importDecls.add(importDec);
    }
    List<String> packageParts = new ArrayList<String>();
    for (String s : packageDec.split("\\.")) {
      packageParts.add(s);
    }
    ASTQualifiedName packageName = TypesNodeFactory.createASTQualifiedName(packageParts);
    de.monticore.java.javadsl._ast.ASTPackageDeclaration packageDeclaration = 
        JavaDSLNodeFactory.createASTPackageDeclaration(null, packageName);
    System.out.println("PackageDec " + packageDeclaration.toString());
    
    
    
    
    
    ArrayList<ASTTypeVariableDeclaration> genericsTypes = createGenerics(generics);
    // Generics, needs to be added by construction of componentHead
    // Darf auch leer sein. Dann muss ja eigentlich doch ein Optional zurueckgegeben werden der leer ist oder?

    de.monticore.types.types._ast.ASTTypeParameters typeParams = 
        TypesNodeFactory.createASTTypeParameters(genericsTypes);
    System.out.println("typeParams"+ typeParams.toString());
    IndentPrinter print = new IndentPrinter();
    TypesPrettyPrinterConcreteVisitor printer= new TypesPrettyPrinterConcreteVisitor(print);
    printer.prettyprint(typeParams);
    
    
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
        MontiArcNodeFactory.createASTComponentHead(typeParams, parameters, null);
    
    // create params for outermost component body
    for (AbstractNode node : graph.getAllNodes()) {
      if (node instanceof ComponentNode) {
        de.monticore.common.common._ast.ASTStereotype.Builder builder = new Builder();
        ASTStereoValue value = CommonNodeFactory.createASTStereoValue(((ComponentNode) node).getStereotype(), null);
        ArrayList<ASTStereoValue> valueList = new ArrayList<ASTStereoValue>();
        valueList.add(value);
        builder.values(valueList);
        de.monticore.common.common._ast.ASTStereotype stereotype = builder.build();
        de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead head = MontiArcNodeFactory.createASTComponentHead();
        ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTElement> bodyElements = new ArrayList<ASTElement>();
        
        ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> astPorts = new ArrayList<ASTPort>();
        // create AstPorts
        for (PortNode p : ((ComponentNode) node).getPorts()) {
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
            if (((PortNode)e.getStartNode()).getComponentNode() == node ||
                ((PortNode)e.getEndNode()).getComponentNode() == node) {
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
            MontiArcNodeFactory.createASTComponent(stereotype, node.getTitle(), head, "", typeArgs, astBody);
       
        
        System.out.println("astComponent Head looks as follows: " + astComponent.getHead());
        System.out.println("astComponent Body looks as follows: " + astComponent.getBody().getElements().toString());
        System.out.println("astComponent Stereotype looks as follows: " + astComponent.getStereotype());
        
      // add each astComponent to elementsList (the list for the body of the outermost component)
        elements.add(astComponent);
      
      }  
    }
    
    // create componentBody of outermost component
    de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody = 
        MontiArcNodeFactory.createASTComponentBody(elements);
    // create outermost component
    de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComp = 
        MontiArcNodeFactory.createASTComponent(null, modelName, astHead, null, null, astBody);
 // create AST
    de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit ast = 
        MontiArcNodeFactory.createASTMACompilationUnit(packageParts, importDecls, astComp);

   return ast;
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
