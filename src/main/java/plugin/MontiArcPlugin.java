package plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import controller.AbstractDiagramController;
import controller.MAPrettyPrinter;
import controller.MontiArcController;

import de.monticore.ast.ASTNode;
//import de.monticore.lang.montiarc.common._ast.ASTParameter;
//import ASTComponent;
//import ASTComponentBody;
//import ASTComponentHead;
//import ASTConnector;
//import ASTElement;
//import ASTInterface;
//import ASTPort;
//import MontiArcNodeFactory;
//import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.types.types._parser.TypesParser;

import exceptions.ComponentGenericsException;
import exceptions.ConnectorSourceException;
import exceptions.ConnectorTargetsException;
import exceptions.InnerNameMissingException;
import exceptions.InterfaceException;
import exceptions.OuterComponentGenericsException;
import exceptions.OuterNameMissingException;
import exceptions.PortDirectionException;
import exceptions.PortTypeException;
import javafx.stage.Stage;
import model.Graph;
import model.GraphElement;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;
import model.edges.Edge;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import montiarc.MontiArcTool;
import montiarc._ast.*;
import montiarc._parser.MontiArcParserTOP;
import montiarc._symboltable.MontiArcLanguage;
import view.edges.AbstractEdgeView;
import view.nodes.AbstractNodeView;
import de.montiarcautomaton.generator.MontiArcGeneratorTool;

import de.monticore.templateclassgenerator.Modelfinder;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.montiarcautomaton.generator.codegen.MAAGenerator;

public class MontiArcPlugin implements MontiCorePlugIn {

  private String usageFolderPath;
  private static final MontiArcPlugin plugIn = new MontiArcPlugin();
  private HashMap<ASTNode,GraphElement> astMap = new HashMap<ASTNode, GraphElement>();
  private HashMap<ASTNode,List<GraphElement>> astConnect = new HashMap();
  private List<AbstractNode> nameErrors = new ArrayList<AbstractNode>();
  private boolean outerName = true;
  private List<String> astPack = new ArrayList<String>();
  public static MontiArcPlugin getInstance() {
    return plugIn;
  }
  
  public void setUsageFolderPath(String path) {
    usageFolderPath = path;
  }
  
  @Override
  public void addUMLFlag(String arg0) {
    // TODO Auto-generated method stub
    
  }
  
  public List<AbstractNode> getNameErrors() {
    return nameErrors;
  }

  @Override
  public List<MontiCoreException> check(ASTNode arg0, HashMap<AbstractNodeView, AbstractNode> arg1) {
    // TODO Auto-generated method stub
    List errors = new ArrayList();
    if (outerName == false) {
      errors.add(new OuterNameMissingException());
    }
    if (!nameErrors.isEmpty() && arg0 == null) {
      for (AbstractNode node : nameErrors) {
        errors.add(new InnerNameMissingException(node, getNodeView(node, arg1)));
      }
      nameErrors.clear();
//      return errors;
    } 
    else {
      ASTMACompilationUnit compUnit = (ASTMACompilationUnit)arg0;
      ASTComponent component = compUnit.getComponent();
      ASTComponentHead outerHead = component.getHead();
      Optional<de.monticore.types.types._ast.ASTTypeParameters> outerGens = outerHead.getGenericTypeParameters();
      List<ASTParameter> outerParams = outerHead.getParameters();
      ASTComponentBody outerBody = component.getBody();
      String outerName = component.getName();
      if (outerName == null) {
        errors.add(new OuterNameMissingException());
      }
      if (outerGens.isPresent()) {
        for (de.monticore.types.types._ast.ASTTypeVariableDeclaration varDecs :
          outerGens.get().getTypeVariableDeclarations()) {
          if (varDecs.getUpperBounds() == null) {
            // error because extends need to contains at least one upperBound
            errors.add(new OuterComponentGenericsException());
          }
        }  
      }
      List<ASTElement> elements = outerBody.getElements();
      for (ASTElement e : elements) {
        System.out.println("e " + e.toString());
        if (e instanceof ASTComponent) {
          System.out.println("in Component");
          String innerName = ((ASTComponent) e).getName();
          ASTComponentHead innerHead = ((ASTComponent) e).getHead();
          if(innerHead.getGenericTypeParameters().isPresent()) {
            for (de.monticore.types.types._ast.ASTTypeVariableDeclaration varDecs :
              innerHead.getGenericTypeParameters().get().getTypeVariableDeclarations()) {
              if (varDecs.getUpperBounds() == null) {
                // error because extends need to contains at least one upperBound
                errors.add(new ComponentGenericsException((AbstractNode) astMap.get(e), 
                    getNodeView((AbstractNode) astMap.get(e),arg1)));
              }
            }
          }
          if (innerName == null) {
            errors.add(new InnerNameMissingException((AbstractNode) astMap.get(e), 
                getNodeView((AbstractNode) astMap.get(e),arg1)));
          }
          List<ASTElement> innerElements =  ((ASTComponent)e).getBody().getElements();
          
          
          System.out.println("innerElements " + innerElements.toString());
          for (ASTElement innerE : innerElements) {
            System.out.println("innerE " + innerE.toString());
            if (innerE instanceof ASTConnector) {
              if (((ASTConnector)innerE).getSource() == null) {
                // error because there needs to be a source
                errors.add(new ConnectorSourceException(((ConnectorEdge)innerE).getStartPort(), 
                    getNodeView((AbstractNode)((ConnectorEdge)innerE).getStartPort(),arg1)
                    ));
              }
              if (((ASTConnector)innerE).getTargets() == null) {
                errors.add(new ConnectorTargetsException());
              }
            }
            else if (innerE instanceof ASTInterface) {
              System.out.println(" inner E " + innerE.toString());
              if (((ASTInterface)innerE).getPorts().isEmpty()) {
                
                // error because there neeeds to be at least one port
                errors.add(new InterfaceException((AbstractNode) astMap.get(innerE), 
                getNodeView((AbstractNode) astMap.get(innerE),arg1)));
              }
              else {
                List<ASTPort> ports = ((ASTInterface )innerE).getPorts();
                System.out.println("ports " + ports.toString());
                for (ASTPort p : ports) {
                  System.out.println(p.getType());
                  if (p.getType() instanceof ASTSimpleReferenceType) {
                    if (((ASTSimpleReferenceType)p.getType()).getNames().isEmpty()) {
                    // error because there needs to be a type
                      errors.add(new PortTypeException((AbstractNode) astMap.get(p), 
                          getNodeView((AbstractNode) astMap.get(p),arg1)));
                    }
                  }
                  if (!p.isIncoming() && !p.isOutgoing()) {
                    // error because a port needs to hava a direction
                    errors.add(new PortDirectionException((AbstractNode) astMap.get(p), 
                    getNodeView((AbstractNode) astMap.get(p),arg1)));
                  }
                }
              }
            }
          }
          
        }
      }
    }
    return errors;
  }

  private AbstractNodeView getNodeView(AbstractNode node, HashMap<AbstractNodeView, AbstractNode> arg1) {
    for (AbstractNodeView nodeView : arg1.keySet()) {
      if (arg1.get(nodeView) == node) {
        return nodeView;
      }
    }
    return null;
  }
  
  private AbstractEdgeView getEdgeView(AbstractEdge edge, HashMap<AbstractEdgeView, AbstractEdge> arg1) {
    for (AbstractEdgeView edgeView : arg1.keySet()) {
      if (arg1.get(edgeView) == edge) {
        return edgeView;
      }
    }
    return null;
  }
  
  public boolean generateCode(ASTNode arg0, String arg1, String arg2) {
    // TODO Auto-generated method stub
    MontiArcGeneratorTool gen = new MontiArcGeneratorTool();
    String targetFolderPath = "C:\\\\Users\\\\Flo\\\\Desktop\\result\\";
    String hwcFolderPath = "C:\\\\Users\\\\Flo\\\\Desktop\\hwc\\";
    gen.generate(Paths.get(usageFolderPath).toFile(), Paths.get(targetFolderPath).toFile(), Paths.get(hwcFolderPath).toFile());
    System.out.println("After generation");
    return false;
  }

  @Override
  public ASTNode getASTNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AbstractDiagramController getController() {
    // TODO Auto-generated method stub
    try {
      Class c = getClass().getClassLoader().loadClass("controller.MontiArcController");
      return (MontiArcController)c.newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }return null;
  }

  @Override
  public String getDSLName() {
    // TODO Auto-generated method stub
    String DSLName = "MontiArc";
    return DSLName;
  }

  @Override
  public String getDSLPicture() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getFileEnding() {
    // TODO Auto-generated method stub
    return ".arc";
  }

  @Override
  public String getFlagName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getGenerator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TypesPrettyPrinterConcreteVisitor getPrettyPrinter() {
    // TODO Auto-generated method stub
    IndentPrinter indent = new IndentPrinter();
    return new MAPrettyPrinter(indent);
  }

  @Override
  public String getView() {
    // TODO Auto-generated method stub
    String view= "view/fxml/montiArcView.fxml";
    return view;
  }

  public String getUsageFolderPath() {
    return usageFolderPath;
  }
  @Override
  public ASTNode shapeToAST(Graph arg0, List<String> arg1) {
    // TODO Auto-generated method stub
    boolean error = false;
    TypesParser typeParser = new TypesParser();
    ArrayList<String> generics = MontiArcController.genericsArray;
    String gens1 = "<";
    for (String g : generics) {
      gens1 = gens1 + g;
    }
    gens1 = gens1 + ">";
    ArrayList<String> astTypes = MontiArcController.types;
    String packageDec = MontiArcController.packageName;
    System.out.println("packageDec " + packageDec.toString());
    ArrayList<String> imports = MontiArcController.importStatements;
    // temporary
    if (arg1.get(0).isEmpty()) {
      outerName = false;
    }
    else {
      outerName = true;
    }
    List<de.monticore.types.types._ast.ASTImportStatement> importDecls = 
        new ArrayList<de.monticore.types.types._ast.ASTImportStatement>();
    for (String s : imports) {
      boolean star = false;
      if (s.contains("*")) {
        star = true;
        s = s.split("\\*")[0];
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
    if (packageDec.split("\\.").length > 0) {
      for (String s : packageDec.split("\\.")) {
        packageParts.add(s);
      }
    }
    else {
      packageParts.add(packageDec);
    }
    MontiArcParserTOP parser = new MontiArcParserTOP();
    Optional<ASTTypeParameters> arguments = Optional.of(TypesNodeFactory.createASTTypeParameters());
    if (!gens1.equals("<>")) {
      try {
        arguments = parser.parseString_TypeParameters(gens1);
      }
      catch (IOException e2) {
        // TODO Auto-generated catch block
        e2.printStackTrace();
      }
    }
    ASTTypeParameters typeParams = null;
    System.out.println("Arguments " + arguments);
    if (arguments.isPresent()) {
      typeParams = arguments.get();
      System.out.println("GENERICS looks as follows " + typeParams);
    }
//    ASTQualifiedName packageName = TypesNodeFactory.createASTQualifiedName(packageParts);
//    List<de.monticore.java.javadsl._ast.ASTAnnotation> annotations = 
//        new ArrayList<de.monticore.java.javadsl._ast.ASTAnnotation>();
//    de.monticore.java.javadsl._ast.ASTPackageDeclaration packageDeclaration = 
//        JavaDSLNodeFactory.createASTPackageDeclaration(annotations, packageName);
    
    
//    ArrayList<ASTTypeVariableDeclaration> genericsTypes = new ArrayList<ASTTypeVariableDeclaration>();
//    if (!generics.isEmpty()) {
//      genericsTypes = createGenerics(generics);
//    } 
//    // Generics, needs to be added by construction of componentHead
//    de.monticore.types.types._ast.ASTTypeParameters typeParams = 
//        TypesNodeFactory.createASTTypeParameters(genericsTypes);
    
    //create types param
    ArrayList<ASTParameter> allParams = new ArrayList<ASTParameter>();
    System.out.println("astTypes " + astTypes);
    System.out.println("astTypes is empty? " + astTypes.isEmpty() + astTypes.get(0).isEmpty() + astTypes.get(0).equals(""));
    if (!astTypes.get(0).isEmpty()) {
      for (String s : astTypes) {
        try {
          Optional<ASTParameter> par = parser.parseString_Parameter(s);
          if (par.isPresent()) {
            allParams.add(par.get());
          }
        }
        catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
    System.out.println("ALLPARAMS " + allParams.toString());
    ArrayList<ASTParameter> parameters = genASTTypes(astTypes);
    System.out.println("PARAMETERS " + parameters.toString());
    ArrayList<ASTElement> elements = new ArrayList<ASTElement>();
    ASTComponent astComponent = null;
  
    
      // create outermost component head
    ASTComponentHead astHead = 
        MontiArcNodeFactory.createASTComponentHead(typeParams, allParams, null);
      
    // create params for outermost component body
    for (AbstractNode node : arg0.getAllNodes()) {
      if (node instanceof ComponentNode) {
        ASTStereoValue value = MontiArcNodeFactory.createASTStereoValue(((ComponentNode)node).getStereotype(), "");
//        ASTStereoValue value = CommonNodeFactory.createASTStereoValue(((ComponentNode) node).getStereotype(), null);
        List<ASTStereoValue> valueList = new ArrayList<ASTStereoValue>();
        valueList.add(value);
        ASTStereotype stereotype = MontiArcNodeFactory.createASTStereotype(valueList);
        String gen = ((ComponentNode) node).getGenerics();
//        String[] gen1 = gen.split("\\;");
//        ArrayList<String> gens = new ArrayList<String>();
//        for (String s: gen1) {
//          gens.add(s);
//        }
        
        
        Optional<ASTTypeParameters> arguments1 = Optional.of(TypesNodeFactory.createASTTypeParameters());
        if (!gens1.equals("<>")) {
          try {
            arguments1 = parser.parseString_TypeParameters(gen);
          }
          catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
          }
        }
        ASTTypeParameters typeGenComp = null;
        System.out.println("Arguments " + arguments1);
        if (arguments1.isPresent()) {
          typeGenComp = arguments1.get();
          System.out.println("GENERICS looks as follows " + typeParams);
        }
        
        
//        ArrayList<ASTTypeVariableDeclaration> genComp = createGenerics(gens);
//        de.monticore.types.types._ast.ASTTypeParameters typeGenComp = 
//            TypesNodeFactory.createASTTypeParameters(genComp);
        String tyComp = ((ComponentNode) node).getComponentType();
        String[] tyComp1 = tyComp.split("\\,");
        System.out.println("Types Splitted Size " + tyComp1.length);
        ArrayList<String> tyComp2 = new ArrayList<String>();
        for (String s : tyComp1) {
          tyComp2.add(s);
        }
        
        
        ArrayList<ASTParameter> typesComp = new ArrayList<ASTParameter>();
        if (!tyComp2.get(0).isEmpty()) {
          for (String s : tyComp2) {
            try {
              Optional<ASTParameter> par = parser.parseString_Parameter(s);
              Optional<ASTValuation> val1 = parser.parseString_Valuation(s.split("\\=")[1]);
              if (par.isPresent()) {
                if (val1.isPresent()) {
                  par.get().setDefaultValue(val1.get());
                }
                typesComp.add(par.get());
              }
            }
            catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        }
        
        System.out.println("typesComp " + typesComp);
//        ArrayList<ASTParameter> typesComp = genASTTypes(tyComp2); 
        ArrayList<String> names = new ArrayList<String>();
        ASTComponentHead head = 
            MontiArcNodeFactory.createASTComponentHead(typeGenComp,typesComp, null);
        ArrayList<ASTElement> bodyElements = new ArrayList<ASTElement>();
        
        ArrayList<ASTPort> astPorts = new ArrayList<ASTPort>();
        // create AstPorts
        System.out.println("Ports of COmponnetNode " + ((ComponentNode) node).getPorts());
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
          
          
//          ArrayList<String> namesPort = new ArrayList<String>();
//          namesPort.add(p.getPortType());
          Optional<ASTType> typeResult = null;
          ASTType typeOfPort = null;
          if (!p.getPortType().isEmpty()) {
            try {
              typeResult = typeParser.parse_String(p.getPortType());
            }
            catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
            typeOfPort = (ASTType)typeResult.get();
            
          }
          else {
            typeOfPort = TypesNodeFactory.createASTSimpleReferenceType();
          }
          
//            de.monticore.types.types._ast.ASTSimpleReferenceType varDecPort = 
//              TypesNodeFactory.createASTSimpleReferenceType(namesPort, null);
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
          List<String> name = new ArrayList<String>();
          name.add(p.getTitle());
          System.out.println("Name of PORT " + name);
          
          ASTPort astPort = MontiArcNodeFactory.createASTPort(stereotype, typeOfPort, name, incoming, outgoing);
          
          astMap.put(astPort, p);
          // add astPort to List
          System.out.println("astPort " +astPort.toString());
          astPorts.add(astPort);
        }
        
        // create astInterface (containing the ports) for each ComponentNode
        // TODO add stereotype for each port
        ASTInterface astInterface = 
            MontiArcNodeFactory.createASTInterface(null, astPorts);
        System.out.println("astINterface  " + astInterface);
        // add astInterface to bodyList of current astComponent
        bodyElements.add(astInterface);
        System.out.println("bodyelements " + bodyElements);
        
        
        // add Connectors for this Component
        ArrayList<ConnectorEdge> edges = new ArrayList<ConnectorEdge>();
        for (Edge e: arg0.getAllEdges()) {
          if (e instanceof ConnectorEdge) {
            if (((PortNode)e.getStartNode()).getComponentNode() == node) {
              edges.add((ConnectorEdge)e);
            }
          }
        }
        ArrayList<ConnectorEdge> visited = new ArrayList<ConnectorEdge>();
        for (ConnectorEdge c : edges) {
          System.out.println("c " + ((ConnectorEdge)c).getStartPort()  + "and Endenode "+((ConnectorEdge)c).getStartPort());
          List<GraphElement> tEdges = new ArrayList<GraphElement>();
          if (!visited.contains(c)) {
            visited.add(c);
            tEdges.add(c);
            ArrayList<String> title = new ArrayList<String>();
            String portName = c.getStartNode().getTitle();
            if (portName != null) {
              if (portName.split(".").length > 0) {
                for (String s : portName.split(".")) {
                  String tit = s;
                  if (!Character.isLowerCase(tit.charAt(0))) {
                    tit = tit.substring(0, 1).toLowerCase() + tit.substring(1);
                  }
                  title.add(s);
                }
              }
              else {
                String tit = ((PortNode)c.getStartNode()).getComponentNode().getTitle();
                if (!Character.isLowerCase(tit.charAt(0))) {
                  tit = tit.substring(0, 1).toLowerCase() + tit.substring(1);
                }
                title.add(tit);
                title.add(portName);
              }
            }
            ASTQualifiedName source = TypesNodeFactory.createASTQualifiedName(title);
            ArrayList<ASTQualifiedName> targets = new ArrayList<ASTQualifiedName>();
            ArrayList<String> firstTarget = new ArrayList<String>();
            String firstTit = ((PortNode)c.getEndNode()).getComponentNode().getTitle();
            if (!Character.isLowerCase(firstTit.charAt(0))) {
              firstTit = firstTit.substring(0, 1).toLowerCase() + firstTit.substring(1);
            }
            firstTarget.add(firstTit);
            firstTit = c.getEndNode().getTitle();
            if (!Character.isLowerCase(firstTit.charAt(0))) {
              firstTit = firstTit.substring(0, 1).toLowerCase() + firstTit.substring(1);
            }
            firstTarget.add(firstTit);
            ASTQualifiedName astTargetFirst = TypesNodeFactory.createASTQualifiedName(firstTarget);
            targets.add(astTargetFirst);
            for (ConnectorEdge e: edges) {
              if (c.getStartNode() == e.getStartNode() && c != e && !visited.contains(e)) {
                // create all targets
                ArrayList<String> target = new ArrayList<String>();
                tEdges.add(e);
                visited.add(e);
                String portNameTarget = e.getEndNode().getTitle();
                if (portNameTarget.split(".").length > 0) {
                  for (String s : portNameTarget.split(".")) {
                    String tit = s;
                    if (!Character.isLowerCase(tit.charAt(0))) {
                      tit = tit.substring(0, 1).toLowerCase() + tit.substring(1);
                    }
                    target.add(s);
                  }
                }
                else {
                  String tit = ((PortNode)e.getEndNode()).getComponentNode().getTitle();
                  if (!Character.isLowerCase(tit.charAt(0))) {
                    tit = tit.substring(0, 1).toLowerCase() + tit.substring(1);
                  }
                  target.add(tit);
                  target.add(portNameTarget);
                  System.out.println("Target " + target.toString());
                }
                ASTQualifiedName astTarget = TypesNodeFactory.createASTQualifiedName(target);
                targets.add(astTarget);
              }  
            }
            ASTConnector astConnector =
                MontiArcNodeFactory.createASTConnector(null, source, targets);
            astConnect.put(astConnector, tEdges);
            bodyElements.add(astConnector);
          }
        }
              
     // create astBody for each ComponentNode
        ASTComponentBody astBody = 
            MontiArcNodeFactory.createASTComponentBody(bodyElements);
        
        de.monticore.types.types._ast.ASTTypeArguments typeArgs = 
            MontiArcNodeFactory.createASTTypeArguments();
        
        // create astComponents for each ComponentNode
        if(node.getTitle() == null) {
          nameErrors.add(node);
          error = true;
          return null;
        }
        else {
          
        }
        
        astComponent = 
            MontiArcNodeFactory.createASTComponent(stereotype, node.getTitle(), head, "", typeArgs, astBody);
        astMap.put(astComponent, node);
       // add each astComponent to elementsList (the list for the body of the outermost component)
        elements.add(astComponent);
     }    
   }
    
    // create componentBody of outermost component
   ASTComponentBody astBody = 
       MontiArcNodeFactory.createASTComponentBody(elements);
   // create outermost component
   
//   List<de.monticore.common.common._ast.ASTStereoValue> tmp = new ArrayList<de.monticore.common.common._ast.ASTStereoValue>();
//   tmp.add(CommonNodeFactory.createASTStereoValue("tmp", "test"));
//   de.monticore.common.common._ast.ASTStereotype stereo = CommonNodeFactory.createASTStereotype(tmp);
   ASTComponent astComp = 
       MontiArcNodeFactory.createASTComponent(null, arg1.get(0), astHead, null, null, astBody);
   // create AST
   ASTMACompilationUnit ast = 
       MontiArcNodeFactory.createASTMACompilationUnit(packageParts, importDecls, astComp);
   
   
  // PrettyPrinting of outermost component
   IndentPrinter indentPrinter = new IndentPrinter();
   indentPrinter.setIndentLength(2);
   MAPrettyPrinter MaPrinter = new MAPrettyPrinter(indentPrinter);
   ArrayList<String> packParts = new ArrayList<String>(); 
   for (String s: ast.getPackage()) {
     packParts.add(s);
   }
   if (!packParts.get(0).isEmpty()) {
     MaPrinter.printPackageName(packParts);
   }
   if (!ast.getImportStatements().get(0).getImportList().get(0).isEmpty()) {
     ArrayList<ASTImportStatement> importSt = new ArrayList<ASTImportStatement>();
     for (ASTImportStatement s : ast.getImportStatements()) {
       importSt.add(s);
     }
     MaPrinter.printImport(importSt); 
   }
   MaPrinter.printComponentOuter(ast.getComponent());    
   MaPrinter.getPrinter().flushBuffer();
   System.out.println(MaPrinter.getPrinter().getContent());
   System.out.println("Test file ending" + MontiArcLanguage.FILE_ENDING);
//   usageFolderPath = "C:\\Users\\Flo\\Desktop\\usage\\test.arc";
   System.out.println("UsageFolderPath " + usageFolderPath);
   String folder = "";
   for (String s : ast.getPackage()) {
     folder = folder + "\\" + s;
   }
   String tmp = usageFolderPath+ folder+ "\\" +arg1.get(0)+"."+MontiArcLanguage.FILE_ENDING;
   System.out.println(tmp);
   File f = new File(tmp);
   BufferedWriter bw = null;
   FileWriter fw = null;
   try {
     String content = MaPrinter.getPrinter().getContent();
     fw = new FileWriter(tmp);
     bw = new BufferedWriter(fw);
     bw.write(content);
   } catch (IOException e) {
     e.printStackTrace();
   } finally {
     try {
       if (bw != null)
         bw.close();
       if (fw != null)
         fw.close();
     } catch (IOException ex) {
       ex.printStackTrace();
     }
   }

     
     // pretty Printing of inner components
   for (ASTElement e : ast.getComponent().getBody().getElements()) {
     if (e instanceof ASTComponent) {
       MAPrettyPrinter CompPrinter = new MAPrettyPrinter(indentPrinter);
       CompPrinter.getPrinter().clearBuffer();
       if (!packParts.get(0).isEmpty()) {
         CompPrinter.printPackageName(packParts);
       }
       CompPrinter.printComponent((ASTComponent) e);
       CompPrinter.getPrinter().flushBuffer();
       String nameComp = ((ASTComponent)e).getName();
       String first = "";
       if (!Character.isUpperCase(astComp.getName().charAt(0))) {
         nameComp = nameComp.substring(0, 1).toUpperCase() + nameComp.substring(1);
       }
       String tmp1 = usageFolderPath+ folder + "\\"+nameComp+"."+MontiArcLanguage.FILE_ENDING;
       System.out.println(tmp1);
       File f1 = new File(tmp1);
       
       BufferedWriter bw1 = null;
       FileWriter fw1 = null;
  
       try {
  
         System.out.println("in try: " + usageFolderPath);
         fw1 = new FileWriter(tmp1);
         bw1 = new BufferedWriter(fw1);
         bw1.write(CompPrinter.getPrinter().getContent());
         System.out.println("Done");
  
       } catch (IOException e1) {
  
         e1.printStackTrace();
  
       } finally {
  
         try {
  
           if (bw1 != null)
             bw1.close();
           if (fw1 != null)
             fw1.close();
  
         } catch (IOException ex) {
  
           ex.printStackTrace();
  
         }
       }
     } 

   }
   if (error == true) {
     return null;
   }
   else {
     return ast;
   }
  }
  
  public void setPackage(List<String> arg1) {
    astPack = arg1;
  }
  
  public List<String> getPackage() {
    return astPack;
  }

  @Override
  public List<String> showContainerInfoDialog(Stage arg0, List<String> arg1, List<String> arg2) {
    // TODO Auto-generated method stub
    return null;
  }
  
  protected ArrayList<String> findExtends(String args) {
    int opened = 0;
    int closed = 0;
    boolean IsSet = true;
    args = args.replace(" ", "");
    ArrayList<Integer> splitPos = new ArrayList<Integer>();
    for (int c = 0;c < args.length(); c++) {
      if (args.charAt(c) == '<') {
        opened++;
        IsSet = false;
      }
      if (args.charAt(c) == '>') {
        closed++;
        IsSet = false;
      }
      if(opened > 0 && closed > 0 && opened == closed && !IsSet) {
        splitPos.add(c);
        IsSet = true; 
      }
    }
    if (!splitPos.contains(0)) {
      splitPos.add(0, 0);
    }
    if (!splitPos.contains(args.length()-1)) {
      splitPos.add(args.length()-1);
    }
    int k = 0;
    ArrayList<String> parts = new ArrayList<String>();
    while(k < splitPos.size()-1) {
      if (k == 0) {
        parts.add(args.substring(splitPos.get(k),splitPos.get(k+1)+1));
      }
      else {
        parts.add(args.substring(splitPos.get(k)+1,splitPos.get(k+1)+1));
      }
      k = k + 1;
    }
    ArrayList<String> partsNew = new ArrayList<String>();
    for (String s : parts) {
      s = s.replace(" ", "");
      System.out.println("s " + s + "char at 0 " + s.charAt(0));
      if (s.charAt(0) == ',') {
        s = s.substring(1, s.length());
      }
      partsNew.add(s);
    }
    return partsNew;
  }
  
  protected ArrayList<String> handleArgs(String sup) {
    if(sup.contains(">") && sup.contains("<")) {
      String inner = sup.replace(sup.split("\\<")[0],"");
      inner = inner.substring(1, inner.length()-1);
      // now inner contains the arguments of sup
      ArrayList<String> innerSup = findExtends(inner);
      System.out.println("innerSups " + innerSup.toString());
      return innerSup;
    }
    else {
      return new ArrayList<String>();
    }
    
  }
  
  protected ArrayList<ASTTypeVariableDeclaration> createGenerics(ArrayList<String> generics) {
    // create List of TypeVariableDeclarations called TypeParameters
    ArrayList<de.monticore.types.types._ast.ASTTypeVariableDeclaration> genericsTypes = new ArrayList<ASTTypeVariableDeclaration>();
    System.out.println("Generics looks as follows" + generics);
    
    for (String g : generics) {
      List<de.monticore.types.types._ast.ASTSimpleReferenceType> firstList = 
          new ArrayList<de.monticore.types.types._ast.ASTSimpleReferenceType>();
      List<de.monticore.types.types._ast.ASTComplexReferenceType> upper= 
          new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
      
      
      System.out.println("g" + g);
   // create generic type params
      String name = "";
      System.out.println("Length of g" + g.split("extends").length );
      if (g.split("extends").length > 1) {
        name = (g.split("extends")[0]).replaceAll("\\s+","");
        System.out.println("Name " + name);
        System.out.println("Before findExtends " + g.split("extends")[1]);
        ArrayList<String> supClasses = findExtends(g.split("extends")[1]);
        System.out.println("supClasses " + supClasses.toString());
        boolean has = true;
        for (String sub : supClasses) {
          if(sub.contains("<") && sub.contains(">")) {
            has = false;
          }
        }
        if (has == true) {
          System.out.println("There are no < and >");
          List<String> tmpName = new ArrayList<String>();
          for (String s : supClasses.get(0).split("\\.")) {
            tmpName.add(s.replaceAll("\\s+",""));
          }
          List<ASTSimpleReferenceType> tmpList = new ArrayList<ASTSimpleReferenceType>();
          tmpList.add(TypesNodeFactory.createASTSimpleReferenceType(tmpName, null));
          upper.add(TypesNodeFactory.createASTComplexReferenceType(tmpList));
        }
        else {
        
  //        String[] supClasses = g.split("extends")[1].split("\\;");
  //        System.out.println("supClasses" + supClasses.length);
          for(String sup : supClasses) {
            List<String> tmpName = new ArrayList<String>();
            if (!sup.contains("<") && !sup.contains(">")) {
              for (String s : sup.split("\\.")) {
                tmpName.add(sup.replaceAll("\\s+",""));
              }
              List<ASTSimpleReferenceType> tmpList = new ArrayList<ASTSimpleReferenceType>();
              tmpList.add(TypesNodeFactory.createASTSimpleReferenceType(tmpName, null));
              upper.add(TypesNodeFactory.createASTComplexReferenceType(tmpList));
            }
            String[] firstSplit1 = sup.split("\\>\\."); 
            ArrayList<String> firstSplit = new ArrayList<String>();
            System.out.println("Num of splits" +sup.split("\\>\\.").length);
            if (sup.split("\\>\\.").length > 1) {
              for (int i = 0; i <sup.split("\\>\\.").length; i++) {
                if (i < sup.split("\\>\\.").length-1) {
                  firstSplit1[i] = firstSplit1[i] + ">";
                  firstSplit.add(firstSplit1[i]);
                }
                else {
                  firstSplit.add(firstSplit1[i]);
                }
              }
            }
            else {
              firstSplit.add(sup);
            }
            System.out.println("FirstSplit " + firstSplit.toString());
            for (String u : firstSplit) {
              // divide supClasses at '.'
              String withoutFirst = "";
              String firstElement  = "";
              if (u.contains("<")) {
                String[] partsSmaller = u.split("\\<");
                System.out.println("partsSmaller" + partsSmaller.length);
                System.out.println("parts Smaller " + partsSmaller);
                for (int i=1; i < partsSmaller.length; i++) {
                  if (i > 1) {
                    withoutFirst = withoutFirst + "<" +partsSmaller[i];
                  }
                  else {
                    withoutFirst = withoutFirst + partsSmaller[i];
                  }
                }
                firstElement = partsSmaller[0];
                System.out.println("First Element " + firstElement);
                
              }
              System.out.println("WithoutFirst" + withoutFirst);
              
              String withoutLast = "";
              
              if (withoutFirst.contains(">")) {
                String[] without = withoutFirst.split("\\>");
                String lastElement = without[without.length-1];
                String partsGreater[] = withoutFirst.split("\\>");
                System.out.println("Length of parts Greater " + partsGreater.length);
                for (int i=0; i<partsGreater.length; i++) {
                  System.out.println("partsGreater " + partsGreater[i]);
                  if (i < partsGreater.length) {
                    withoutLast = withoutLast + partsGreater[i] + ">" ;
                  }
                  else {
                    withoutLast = withoutLast + partsGreater[i];
                  }
                }
              }
              System.out.println("withoutLast" + withoutLast);
              String[] partsTmp = withoutLast.split("\\,");
              ArrayList<String> parts = new ArrayList<String>();
              for (int i=0 ; i <partsTmp.length; i++) {
                parts.add(partsTmp[i]);
              }
              System.out.println("parts" + parts.toString());
              List<de.monticore.types.types._ast.ASTTypeArgument> typeArgs = 
                  new ArrayList<de.monticore.types.types._ast.ASTTypeArgument>();
              int k = 0;
              int length = parts.size();
              while (k <length) {
                if (parts.get(k).contains("<") && parts.get(k).contains(">")) {
                  System.out.println("length of split at <" + parts.get(k).split("\\<")[0]);
                  if (parts.get(k).split("\\<")[0].isEmpty()) {
                    List<String> qn = new ArrayList<String>();
                    if (parts.get(k).split("\\.").length > 0) {
                      for (String s : parts.get(k).split("\\.")) {
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        if (s.contains("<")) {
                          s = s.split("\\<")[1];
                        }
                        qn.add(s.replaceAll("\\s+",""));
                      }
                    }
                    else {
                      String tmp = "";
                      if (parts.get(k).contains(">")) {
                        tmp = parts.get(k).split("\\>")[0];
                      }
                      else {
                        tmp = parts.get(k);
                      }
                      qn.add(tmp.replaceAll("\\s+",""));
                    }
                    
                    
                    de.monticore.types.types._ast.ASTSimpleReferenceType typeArg = 
                        TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                    System.out.println("typeArg" + typeArg);
                    typeArgs.add(typeArg);
                  }
                  else {
                    // create typeName 
                    String typeName = parts.get(k).split("\\<")[0];
                    List<String> typeNameQ = new ArrayList<String>();
                    
                    if (typeName.split("\\.").length > 0) {
                      for (String s : typeName.split("\\.")) {
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        typeNameQ.add(s.replaceAll("\\s+",""));
                      }
                    }
                    else {
                      String tmp = "";
                      if (typeName.contains(">")) {
                        tmp = typeName.split("\\>")[0];
                      }
                      else {
                        tmp = typeName;
                      }
                      typeNameQ.add(tmp.replaceAll("\\s+",""));
                    }
                    
                    // create typeArgs
                    String rest = parts.get(k).split("\\<")[1];
                    
                    ArrayList<String> rests = new ArrayList<String>();
                    
                    if(rest.contains("\\,")) {
                      String[] reste = rest.split("\\,");  
                      for (int l = 0 ; l<reste.length; l++) {
                        rests.add(reste[l]);
                      }
                    }
                    else {
                      rests.add(rest);
                    }
                    
                    List<de.monticore.types.types._ast.ASTTypeArgument> typeArgRest= 
                        new ArrayList<de.monticore.types.types._ast.ASTTypeArgument>();
                    for (String rest1: rests) {
                      ArrayList<String> argsQ = new ArrayList<String>();
                      if (rest1.split("\\.").length > 0) {
                        for (String s : rest.split("\\.")) {
                          if (s.contains(">")) {
                            s= s.split("\\>")[0];
                          }
                          argsQ.add(s.replaceAll("\\s+",""));
                        }
                      }
                      else {
                        String tmp = "";
                        if (rest.contains(">")) {
                          tmp = rest.split("\\>")[0];
                        }
                        else {
                          tmp = rest;
                        }
                        argsQ.add(tmp.replaceAll("\\s+",""));
                      }
                      de.monticore.types.types._ast.ASTSimpleReferenceType typeArg =    
                          TypesNodeFactory.createASTSimpleReferenceType(argsQ, null);
                      typeArgRest.add(typeArg);
                      
                    }
                    
                    // TODO Question if ASTSimpleReferenceType is a valid ASTTypeArgument
  //                  de.monticore.types.types._ast.ASTSimpleReferenceType typeArg =    
  //                      TypesNodeFactory.createASTSimpleReferenceType(restArgs, null);
  //                  typeArgRest.add(typeArg);
                    // TODO
                    de.monticore.types.types._ast.ASTTypeArguments typeArguments =
                        TypesNodeFactory.createASTTypeArguments(typeArgRest);
                    de.monticore.types.types._ast.ASTSimpleReferenceType add = 
                        TypesNodeFactory.createASTSimpleReferenceType(typeNameQ, typeArguments);
                    System.out.println("typeArg" + add);
                    
                    typeArgs.add(add);
                  }
    //              parts.remove(k);
                  k++;
                }
                else if (parts.get(k).contains("<")) {
                  String nameQu = "";
                  System.out.println("length of parts " + parts.get(k).split("\\<").length);
                  if(parts.get(k).split("\\<").length > 1) {
                    nameQu = parts.get(k).split("\\<")[0];
                    System.out.println("NameQU" + nameQu);
                    List<de.monticore.types.types._ast.ASTTypeArgument> tmpArgList = 
                        new ArrayList<de.monticore.types.types._ast.ASTTypeArgument>();
    //                parts.remove(k);
                    List<String> firstArg = new ArrayList<String>();
                    for (String s : parts.get(k).split("\\<")[1].split("\\.")) {
                      if (s.contains(">")) {
                        s= s.split("\\>")[0];
                      }
                      firstArg.add(s.replaceAll("\\s+",""));
                    }
                    de.monticore.types.types._ast.ASTSimpleReferenceType addFirst = 
                        TypesNodeFactory.createASTSimpleReferenceType(firstArg, null);
                    tmpArgList.add(addFirst);
                    k++;
                    System.out.println("parts k "  + parts.get(k) + "contains >" + parts.get(k).contains(">"));
                    while(!parts.get(k).contains(">")) {
                      List<String> qn = new ArrayList<String>();
                      if (parts.get(k).split("\\.").length > 0) {
                        for (String s : parts.get(k).split("\\.")) {
                          if (s.contains(">")) {
                            s= s.split("\\>")[0];
                          }
                          qn.add(s.replaceAll("\\s+",""));
                        }
                      }
                      else {
                        String tmp = "";
                        if (parts.get(k).contains(">")) {
                          tmp = parts.get(k).split("\\>")[0];
                        }
                        else {
                          tmp = parts.get(k);
                        }
                        qn.add(tmp.replaceAll("\\s+",""));
                      }
                      de.monticore.types.types._ast.ASTSimpleReferenceType add = 
                          TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                      tmpArgList.add(add);
    //                  parts.remove(k);
                      k++;
                      
                    }
                    // the last one with ">"
                    List<String> qn = new ArrayList<String>();
                    if (parts.get(k).split("\\.").length > 0) {
                      for (String s : parts.get(k).split("\\.")) {
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        qn.add(s.replaceAll("\\s+",""));
                      }
                    }
                    else {
                      String tmp = "";
                      if (parts.get(k).contains(">")) {
                        tmp = parts.get(k).split("\\>")[0];
                      }
                      else {
                        tmp = parts.get(k);
                      }
                      qn.add(tmp.replaceAll("\\s+",""));
                    }
                    k++;
                    de.monticore.types.types._ast.ASTSimpleReferenceType add1 = 
                        TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                    tmpArgList.add(add1);
                    List<String> nameQuList = new ArrayList<String>();
                    if (nameQu.split("\\.").length > 0) {
                      for (String s : nameQu.split("\\.")) {
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        nameQuList.add(s.replaceAll("\\s+",""));
                      }
                    }
                    else {
                      String tmp = "";
                      if (parts.get(k).contains(">")) {
                        tmp = nameQu.split("\\>")[0];
                      }
                      else {
                        tmp = nameQu;
                      }
                      nameQuList.add(tmp.replaceAll("\\s+",""));
                    }
                    de.monticore.types.types._ast.ASTTypeArguments typesList = 
                        TypesNodeFactory.createASTTypeArguments(tmpArgList);
                    de.monticore.types.types._ast.ASTSimpleReferenceType toBeAdded = 
                        TypesNodeFactory.createASTSimpleReferenceType(nameQuList, typesList);
                    System.out.println("typeArg" + toBeAdded);
                    
                    typeArgs.add(toBeAdded);
                    
                  }
                  else {
                    List<de.monticore.types.types._ast.ASTTypeArgument> tmpArgList = 
                        new ArrayList<de.monticore.types.types._ast.ASTTypeArgument>();
      //              parts.remove(k);
                    k++;
                    while(!parts.get(k).contains(">")) {
                      List<String> qn = new ArrayList<String>();
                      if (parts.get(k).split("\\.").length > 0) {
                        for (String s : parts.get(k).split("\\.")) {
                          if (s.contains(">")) {
                            s= s.split("\\>")[0];
                          }
                          qn.add(s.replaceAll("\\s+",""));
                        }
                      }
                      else {
                        String tmp = "";
                        if (parts.get(k).contains(">")) {
                          tmp = parts.get(k).split("\\>")[0];
                        }
                        else {
                          tmp = parts.get(k);
                        }
                        qn.add(tmp.replaceAll("\\s+",""));
                      }
                      de.monticore.types.types._ast.ASTSimpleReferenceType add = 
                          TypesNodeFactory.createASTSimpleReferenceType(qn, null);
                      System.out.println("qn " + qn.toString());
                      System.out.println("add " +add);
                      tmpArgList.add(add);
                      System.out.println("TmpArgList " + tmpArgList.toString());
      //                parts.remove(k);
                      k++;
                      
                    }
                    // the last one with ">"
                    List<String> qn = new ArrayList<String>();
                    if (parts.get(k).split("\\.").length > 0) {  
                      for (String s : parts.get(k).split("\\.")) {
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        qn.add(s.replaceAll("\\s+",""));
                      }
                    }
                    else {
                      String tmp = "";
                      if (parts.get(k).contains(">")) {
                        tmp = parts.get(k).split("\\>")[0];
                      }
                      else {
                        tmp = parts.get(k);
                      }
                      qn.add(tmp.replaceAll("\\s+",""));
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
                  System.out.println("ELSE CASE");
                  System.out.println("parts in else case " + parts.get(k));
                  List<String> onlyName = new ArrayList<String>();
                  if (parts.get(k).split("\\.").length > 0) {
                    for (String s : parts.get(k).split("\\.")) {
                      System.out.println("Add " + s);
                      if (s != ">") {
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        if (s.contains(">")) {
                          s= s.split("\\>")[0];
                        }
                        onlyName.add(s.replaceAll("\\s+",""));
                      }
                    }
                  }
                  else {
                    String tmp = "";
                    if (parts.get(k).contains(">")) {
                      tmp = parts.get(k).split("\\>")[0];
                      while(tmp.contains(">")) {
                        tmp = parts.get(k).split("\\>")[0];
                      }
                    }
                    else {
                      tmp = parts.get(k);
                    }
                    onlyName.add(tmp.replaceAll("\\s+",""));
                  }
                  de.monticore.types.types._ast.ASTSimpleReferenceType lastAdd = 
                      TypesNodeFactory.createASTSimpleReferenceType(onlyName, null);
                  System.out.println("lastAdd" +lastAdd.toString());
                  typeArgs.add(lastAdd);
                  
                  k++;
                }
              }
              
              // now we handle the first and the last one 
  //            System.out.println("firstElement" + firstElement);
  //            System.out.println("util " + firstElement.split("\\.")[0]);
  //            System.out.println("list " + firstElement.split("\\.")[1]);
              String[] qualifier = firstElement.split("\\.");
              System.out.println("qualifier length " +qualifier.length);
              ArrayList<String> qualifiers = new ArrayList<String>();
              for (String s:qualifier) {
                System.out.println("s " + s);
                if (s.contains(">")) {
                  s= s.split("\\>")[0];
                }
                qualifiers.add(s.replaceAll("\\s+",""));
              }
              de.monticore.types.types._ast.ASTTypeArguments temp = 
                  TypesNodeFactory.createASTTypeArguments(typeArgs);
              de.monticore.types.types._ast.ASTSimpleReferenceType first = 
                  TypesNodeFactory.createASTSimpleReferenceType(qualifiers, temp);
    //          System.out.println("First element to add " + first.toString());
              firstList.add(first);
    //          typeArgs.add(0,first);
    //          System.out.println("typeArgs " + typeArgs.toString());
              
            }
            de.monticore.types.types._ast.ASTComplexReferenceType bla = 
                TypesNodeFactory.createASTComplexReferenceType(firstList);
            upper.add(bla);
          }
        }
        de.monticore.types.types._ast.ASTTypeVariableDeclaration varDec = 
            TypesNodeFactory.createASTTypeVariableDeclaration(name, upper);
        genericsTypes.add(varDec);
           
      }  
    }
    return genericsTypes;    
  }
  
  protected ArrayList<ASTParameter> genASTTypes(ArrayList<String> astTypes){
  //create types param
    ArrayList<ASTParameter> parameters = new ArrayList<ASTParameter>();
    System.out.println("Is types empty? " + astTypes.isEmpty());
    if (!astTypes.isEmpty()) {
      System.out.println("Types looks as follows" + astTypes);
      for (String t : astTypes) {
        if (!t.isEmpty()) {
          if (t.charAt(0) == ' ') {
            t = t.substring(1, t.length());
          }
          System.out.println("t " + t);
          ArrayList<String> names = new ArrayList<String>();
          names.add(t.split("\\s+")[0]);
          de.monticore.types.types._ast.ASTSimpleReferenceType varDec = 
              TypesNodeFactory.createASTSimpleReferenceType(names, null);
          String typeName = t.split("\\s+")[1];
          System.out.println("typeName " + typeName);
          ASTParameter parameter = 
              MontiArcNodeFactory.createASTParameter(varDec, typeName, null);
          parameters.add(parameter);    
        }
      }
    }
    return parameters;
  }

  
}
