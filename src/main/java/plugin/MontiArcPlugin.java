package plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import controller.AbstractDiagramController;
import controller.MontiArcController;
import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.monticore.ast.ASTNode;
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
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import exceptions.ComponentGenericsException;
import exceptions.ConnectorSourceException;
import exceptions.ConnectorTargetsException;
import exceptions.DuplicateParam;
import exceptions.ImportException;
import exceptions.InnerNameLow;
import exceptions.InnerNameMissingException;
import exceptions.InterfaceException;
import exceptions.OuterComponentGenericsException;
import exceptions.OuterNameMissingException;
import exceptions.PortDirectionException;
import exceptions.PortTypeException;
import exceptions.cocoError;
import exceptions.genOuterExtendException;
import exceptions.genSplitException;
import exceptions.genTypeWrong;
import exceptions.genTypeWrongOuter;
import exceptions.genTypeWrongParam;
import exceptions.genTypeWrongParamOuter;
import exceptions.packageDirectoryException;
import exceptions.wrongPackageNameForm;
import javafx.stage.Stage;
import model.Graph;
import model.GraphElement;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;
import model.edges.Edge;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTComponentBody;
import montiarc._ast.ASTComponentHead;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTStereoValue;
import montiarc._ast.ASTStereotype;
import montiarc._ast.ASTValuation;
import montiarc._ast.MontiArcNodeFactory;
import montiarc._parser.MontiArcParserTOP;
import montiarc._symboltable.MontiArcLanguage;
import prettyprinter.MAPrettyPrinter;
import view.edges.AbstractEdgeView;
import view.nodes.AbstractNodeView;

public class MontiArcPlugin implements MontiCorePlugIn {

  private String usageFolderPath;
  private static final MontiArcPlugin plugIn = new MontiArcPlugin();
  private HashMap<ASTNode,GraphElement> astMap = new HashMap<ASTNode, GraphElement>();
  private HashMap<ASTNode,List<GraphElement>> astConnect = new HashMap();
  private List<AbstractNode> nameErrors = new ArrayList<AbstractNode>();
  private boolean outerName = true;
  private List<String> astPack = new ArrayList<String>();
  private ArrayList<MontiCoreException> errorList = new ArrayList<MontiCoreException>();
  private List<Finding> oldFindings = new ArrayList<Finding>();
  private List<MontiCoreException> genErrorList = new ArrayList<MontiCoreException>();
  
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
    List errors = new ArrayList();
    // TODO Auto-generated method stub
    
    if (!errorList.isEmpty()) {
      for (Object e:errorList) {
        if(e instanceof InnerNameLow) {
          ((InnerNameLow) e).setNodeView(getNodeView(((InnerNameLow) e).getNode(),arg1));
        }
        if (e instanceof genTypeWrongOuter) {
          ((genTypeWrongOuter)e).setNodeView(getNodeView(((genTypeWrongOuter) e).getNode(),arg1));
        }
        if (e instanceof genTypeWrongParamOuter) {
          ((genTypeWrongParamOuter)e).setNodeView(getNodeView(((genTypeWrongParamOuter) e).getNode(),arg1));
        }
        
        errors.add(e);
      }
      errorList.clear();
      return errors;
    }
    
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
  
  public List getGenErrorList() {
    return genErrorList;
  }
  
  public void clearGenErrorList() {
    genErrorList.clear();
  }
  
  public boolean generateCode(ASTNode arg0, String arg1, String arg2) {
    // TODO Auto-generated method stub
    MontiArcGeneratorTool gen = new MontiArcGeneratorTool();
    String targetFolderPath = "C:\\\\Users\\\\Flo\\\\Desktop\\result\\";
    String hwcFolderPath = "C:\\\\Users\\\\Flo\\\\Desktop\\hwc\\";
//    String targetFolderPath = usageFolderPath;
//    String hwcFolderPath = usageFolderPath;
    Log.enableFailQuick(false);
    gen.generate(Paths.get(usageFolderPath).toFile(), Paths.get(targetFolderPath).toFile(), Paths.get(hwcFolderPath).toFile());
    List<Finding> findings = Log.getFindings();
    findings = findings.subList(oldFindings.size(), findings.size());
    System.out.println("Findings " + findings);
    for (Finding f: findings) {
      if (f.isError()) {
        genErrorList.add(new cocoError(f.getMsg()));
      }
    }
    oldFindings.addAll(findings);
 
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
  
  protected ArrayList<ASTParameter> parseTypes( ArrayList<String> astTypes ,  MontiCoreException ex, AbstractNode node) {
    MontiArcParserTOP parser = new MontiArcParserTOP();
    Log.enableFailQuick(false);
    ArrayList<ASTParameter> allParams = new ArrayList<ASTParameter>();
  
    if(!astTypes.isEmpty()) {
      if (!astTypes.get(0).isEmpty()) {
        for (String s : astTypes) {
          System.out.println("s " +s);
          try {
            Optional<ASTParameter> par = parser.parseString_Parameter(s);
            
            if (par.isPresent()) {
              allParams.add(par.get());
              
            }
            String test4 ="(([a-zA-z])+([a-zA-z0-9])*)\\s(([a-zA-z])+([a-zA-z0-9])*)(\\s)*(=(\\s)*([a-zA-z0-9]|'))*"; 
            System.out.println(s.matches(test4));
            if (!s.matches(test4)) {
              if (ex instanceof genTypeWrongParam) {
                errorList.add(new genTypeWrongParam("Configs should have the form int i = 5 and are separated with ','"));
              }
              if (ex instanceof genTypeWrongParamOuter) {
                errorList.add(new genTypeWrongParamOuter("Configs should have the form int i = 5 and are separated with ','", node, null));
              }
            }

            List<Finding> findings = Log.getFindings();
            findings = findings.subList(oldFindings.size(), findings.size());
            System.out.println("Findings " + findings);
            for (Finding f: findings) {
              if (f.isError()) {
                if (ex instanceof genTypeWrong) {
                  errorList.add(new genTypeWrong(f.getMsg()));
                }
                if (ex instanceof genTypeWrongOuter) {
                  errorList.add(new genTypeWrongOuter(f.getMsg(), node, null));
                }
              }
            }
            oldFindings.addAll(findings);
          }
          catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
          }
        }
      }
    }
    return allParams;
  }
  
  protected ASTTypeParameters parseGenerics(String gens1, MontiCoreException ex, AbstractNode node) {
    ASTTypeParameters typeParams = null;
    MontiArcParserTOP parser = new MontiArcParserTOP();
    
    Optional<ASTTypeParameters> arguments = Optional.of(TypesNodeFactory.createASTTypeParameters());
    if (!gens1.equals("<>")) {
      Log.enableFailQuick(false);
      try {
        arguments = parser.parseString_TypeParameters(gens1);
        
        List<Finding> findings = Log.getFindings();
        findings = findings.subList(oldFindings.size(), findings.size());
        System.out.println("Findings " + findings);
        for (Finding f: findings) {
          if (f.isError()) {
            if (ex instanceof genTypeWrong) {
              errorList.add(new genTypeWrong(f.getMsg()));
            }
            if (ex instanceof genTypeWrongOuter) {
              errorList.add(new genTypeWrongOuter(f.getMsg(), node, null));
            }
          }
        }
        oldFindings.addAll(findings);
      }
      catch (IOException e2) {
        // TODO Auto-generated catch block
        e2.printStackTrace();
      }
    }
    
    if (arguments.isPresent()) {
      typeParams = arguments.get();
    }
    
    return typeParams;
  }
  
  
  @Override
  public ASTNode shapeToAST(Graph arg0, List<String> arg1) {
    System.out.println("ErrorList " + errorList);
    MontiArcParserTOP parser = new MontiArcParserTOP();
    boolean error = false;
    TypesParser typeParser = new TypesParser();
    
    //checking for input errors (if not existing, for inputs the ast is created)
    
    //checking for imports
    
    ArrayList<String> imports = MontiArcController.importStatements;
    List<de.monticore.types.types._ast.ASTImportStatement> importDecls = 
        new ArrayList<de.monticore.types.types._ast.ASTImportStatement>();
    for (String s : imports) {
      System.out.println("import looks : " + s);
      if (!s.isEmpty()) {
        if (s.matches("((([a-zA-z]+)([a-zA-z0-9])*)(\\.))*(([a-zA-z]+)([a-zA-z0-9])*)") ||
            s.matches("((([a-zA-z]+)([a-zA-z0-9])*)(\\.))*(\\*)")) {
          try {
            System.out.println("import " + s + ";");
            s = "import " + s + ";"; 
            Optional<ASTImportStatement> imp = parser.parseString_ImportStatement(s);
            if (imp.isPresent()) {
              importDecls.add(imp.get());
            }
          }
          catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
        else {
          System.out.println("YEAAAH");
          errorList.add(new ImportException());
          // FEHLERHAFTER IMPORT BEFEHL
        }
      }
    }
    System.out.println("ImportDecls " + importDecls.toString());
    
    //checking for ModelName (also checked for all Components)
    
    // for the model
    if (arg1.get(0).isEmpty()) {
      outerName = false;
      errorList.add(new OuterNameMissingException());
    }
    else {
      if (Character.isLowerCase(arg1.get(0).charAt(0))) {
        arg1.set(0, arg1.get(0).substring(0, 1).toUpperCase() + arg1.get(0).substring(1)); 
      }
      outerName = true;
    }
    
    for (AbstractNode node : arg0.getAllNodes()) {
      if (node instanceof ComponentNode) {
        System.out.println("TITILE " + node.getTitle());
        if (node.getTitle() != null) {
          if (Character.isLowerCase(((ComponentNode)node).getTitle().charAt(0))) {
            errorList.add(new InnerNameLow(node, null));
          }
        }
      }
    }
    
    //checking for PackageName
    
    String packageDec = MontiArcController.packageName;
    List<String> packageParts = new ArrayList<String>();
    if (!packageDec.isEmpty()) {
      System.out.println("PackageName " + packageDec);
      if (!packageDec.matches("((([a-zA-z]+)([a-zA-z0-9])*)(\\.))*(([a-zA-z]+)([a-zA-z0-9])*)")) {
        errorList.add(new wrongPackageNameForm());
      }
      else {
        String folderPath = "";
        if (packageDec.split("\\.").length > 0) {
          for (String s: packageDec.split("\\.")) {
            folderPath = folderPath + "\\" + s;
            packageParts.add(s);  
          }
        }
        else {
          folderPath = "\\" + packageDec;
          packageParts.add(packageDec);
        }
        File f = new File(usageFolderPath + folderPath);
        if (!f.exists() && !f.isDirectory()) {
          errorList.add(new packageDirectoryException());
        }
        
      }
    }
    
    //checking for Generics (also checked for all Components)
    
//    ArrayList<String> generics = MontiArcController.genericsArray;
    String gens = "<" + arg1.get(1) + ">";
    System.out.println("GEEEENS " + gens);
//    String gens1 = "<";
//    for (String g : generics) {
//      System.out.println("g " +g);
//      if(!g.isEmpty()) {
//        if (!g.contains("extends")) {
//          errorList.add(new genOuterExtendException());
//        }
//        else if(g.split("extends").length > 2) {
//          errorList.add(new genSplitException());
//        }
//        else {
//          gens1 = gens1 + g;
//        }
//      }
//    }
//    gens1 = gens1 + ">";
    genTypeWrong ex2 = new genTypeWrong(null);
    ASTTypeParameters typeParams = parseGenerics(gens, ex2, null);
    
    
    
    // Now for ComponentNodes
    
    for (AbstractNode node : arg0.getAllNodes()) {
      if (node instanceof ComponentNode) {
        String genComp = ((ComponentNode)node).getGenerics();
        genTypeWrongOuter ex1 = new genTypeWrongOuter(null,null,null);
        ASTTypeParameters bin = parseGenerics("<"+genComp+">", ex1, node);
      }
    }
    
    //checking for Params (also for all Components)
    ArrayList<String> astTypes = MontiArcController.types;
    System.out.println("astTypes " + astTypes);
    
    genTypeWrongParam ex3 = new genTypeWrongParam(null);
    ArrayList<ASTParameter> allParams = parseTypes(astTypes, ex3, null);
    
    for (ASTParameter p : allParams) {
      for (ASTParameter pam : allParams) {
      	if (p != pam) {
      	  if (p.getName().equals(pam.getName())){
      	    errorList.add(new DuplicateParam());	  
      	  }
        }
      }
    }
    for (AbstractNode node : arg0.getAllNodes()) {
      if (node instanceof ComponentNode) {
        String genComp = ((ComponentNode)node).getComponentType();
        ArrayList<String> compTypes = new ArrayList<String>();
        for (String s : genComp.split("\\,")) {
          compTypes.add(s);  	
        }
        genTypeWrongParamOuter ex4 = new genTypeWrongParamOuter(null,null,null);
        ArrayList<ASTParameter> bin = parseTypes(compTypes, ex4, node);
        ArrayList<String> names = new ArrayList<String>();
        for (ASTParameter b : bin) {
          names.add(b.getName());	
        }
        for (ASTParameter p : allParams) {
          if (bin.contains(p)) {
        	errorList.add(new DuplicateParam());		  
          }
          else if (names.contains(p.getName())) {
        	errorList.add(new DuplicateParam());  
          }
          else {
        	bin.add(p);  
          }
        }
        
        
        
      }
    }
    
    
    if (errorList.isEmpty()) {
      ArrayList<ASTElement> elements = new ArrayList<ASTElement>();
      ASTComponent astComponent = null;
      List<ASTTypeVariableDeclaration> gensOuter = typeParams.getTypeVariableDeclarations();
      ArrayList<ASTTypeVariableDeclaration> gensToAdd = new ArrayList<ASTTypeVariableDeclaration>();
      for (AbstractNode node:arg0.getAllNodes()) {
    	if (node instanceof ComponentNode) {
    	  String gen = ((ComponentNode) node).getGenerics();
          gen = "<" + gen +">";
            
          Optional<ASTTypeParameters> arguments1 = Optional.of(TypesNodeFactory.createASTTypeParameters());
          if (!gen.equals("<>")) {
            try {
              arguments1 = parser.parseString_TypeParameters(gen);
            }
            catch (IOException e2) {
              // TODO Auto-generated catch block
              e2.printStackTrace();
            }
          }
          ASTTypeParameters typeGenComp = null;
          if (arguments1.isPresent()) {
            typeGenComp = arguments1.get();
          }
          // add all Generics from Modell
          // remove duplicates
          // check for errors like T extends String and T extends int
//          List<ASTTypeVariableDeclaration> gensComp = typeGenComp.getTypeVariableDeclarations();
          System.out.println("gensOuter " + gensOuter.toString());
          for (ASTTypeVariableDeclaration g : typeGenComp.getTypeVariableDeclarations()) {
            if (!typeParams.getTypeVariableDeclarations().contains(g)) {
              for (ASTTypeVariableDeclaration gOut : typeParams.getTypeVariableDeclarations()) {
            	System.out.println("gOut " + gOut + " its name " + gOut.getName());
            	System.out.println("g " + g + " its name " + g.getName());
            	if (!gOut.getName().equals(g.getName())) {
            	  gensToAdd.add(g);	
//            	  typeParams.getTypeVariableDeclarations().add(g);	
              	}
              }
            }
          }
        }
      }
      for (ASTTypeVariableDeclaration g : gensOuter) {
    	gensToAdd.add(g);  
      }
      ASTTypeParameters genCompAllOuter = TypesNodeFactory.createASTTypeParameters(gensToAdd);
   // create outermost component head
      
      // create params for outermost component body
      for (AbstractNode node : arg0.getAllNodes()) {
        if (node instanceof ComponentNode) {
          ArrayList<ASTTypeVariableDeclaration> gensToAddPro = new ArrayList<ASTTypeVariableDeclaration>();
          List<ASTTypeVariableDeclaration> gensCompW = typeParams.getTypeVariableDeclarations();
          gensToAddPro.addAll(gensCompW);  	
          ASTStereoValue value = MontiArcNodeFactory.createASTStereoValue(((ComponentNode)node).getStereotype(), "");
          List<ASTStereoValue> valueList = new ArrayList<ASTStereoValue>();
          valueList.add(value);
          ASTStereotype stereotype = MontiArcNodeFactory.createASTStereotype(valueList);
          String gen = ((ComponentNode) node).getGenerics();
          gen = "<" + gen +">";
          
          Optional<ASTTypeParameters> arguments1 = Optional.of(TypesNodeFactory.createASTTypeParameters());
          if (!gen.equals("<>")) {
            try {
              arguments1 = parser.parseString_TypeParameters(gen);
            }
            catch (IOException e2) {
              // TODO Auto-generated catch block
              e2.printStackTrace();
            }
          }
          ASTTypeParameters typeGenComp = null;
          if (arguments1.isPresent()) {
            typeGenComp = arguments1.get();
          }
          System.out.println("Component has gens " + typeGenComp);
          // add all Generics from Modell
          // remove duplicates
          // check for errors like T extends String and T extends int
          for (ASTTypeVariableDeclaration g : typeGenComp.getTypeVariableDeclarations()) {
            if (!gensToAddPro.contains(g)) {
              for (ASTTypeVariableDeclaration gOut : typeParams.getTypeVariableDeclarations()) {
            	if (!gOut.getName().equals(g.getName())) {
            	  gensToAddPro.add(g);	
            	}
              }
            }
          }
          ASTTypeParameters genCompAll = TypesNodeFactory.createASTTypeParameters(gensToAddPro);
          String tyComp = ((ComponentNode) node).getComponentType();
          String[] tyComp1 = tyComp.split("\\,");
          ArrayList<String> tyComp2 = new ArrayList<String>();
          for (String s : tyComp1) {
            tyComp2.add(s);
          }
          
          ArrayList<ASTParameter> typesComp = new ArrayList<ASTParameter>();
          if (!tyComp2.get(0).isEmpty()) {
            for (String s : tyComp2) {
              try {
                Optional<ASTParameter> par = parser.parseString_Parameter(s);
                if (s.contains("\\=")) {
                  Optional<ASTValuation> val1 = parser.parseString_Valuation(s.split("\\=")[1]);
                  if (par.isPresent()) {
                    if (val1.isPresent()) {
                      par.get().setDefaultValue(val1.get());
                    }
                    typesComp.add(par.get());
                  }
                }
                else {
                  if (par.isPresent()) {
                    typesComp.add(par.get());
                  }	
                }
                
              }
              catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }
            }
          }
          
          ArrayList<String> names = new ArrayList<String>();
          ASTSimpleReferenceType superComp = MontiArcNodeFactory.createASTSimpleReferenceType();
          ASTComponentHead head = 
              MontiArcNodeFactory.createASTComponentHead(genCompAll,typesComp, superComp);
          ArrayList<ASTElement> bodyElements = new ArrayList<ASTElement>();
          
       // create AstPorts
          ArrayList<ASTPort> astPorts = new ArrayList<ASTPort>();
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
          // create astPort for each PortNode of the current ComponentNode
            List<String> name = new ArrayList<String>();
            name.add(p.getTitle());
            ASTPort astPort = MontiArcNodeFactory.createASTPort(stereotype, typeOfPort, name, incoming, outgoing);
            astMap.put(astPort, p);
            // add astPort to List
            astPorts.add(astPort);
          }
          
          // create astInterface (containing the ports) for each ComponentNode
          ASTInterface astInterface = 
              MontiArcNodeFactory.createASTInterface(null, astPorts);
          // add astInterface to bodyList of current astComponent
          bodyElements.add(astInterface);
          
          
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
                  if (((PortNode)c.getEndNode()).getComponentNode().getSubName().equals("")) {
                    String tit = ((PortNode)c.getStartNode()).getComponentNode().getTitle();
                    if (!Character.isLowerCase(tit.charAt(0))) {
                      tit = tit.substring(0, 1).toLowerCase() + tit.substring(1);
                    }
                    title.add(tit);  
                  }
                  else {
                    String subName1 = ((PortNode)c.getStartNode()).getComponentNode().getSubName();
                    title.add(subName1);
                  }
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
                    if (((PortNode)e.getEndNode()).getComponentNode().getSubName().equals("")) {
                      String tit = ((PortNode)e.getEndNode()).getComponentNode().getTitle();
                      if (!Character.isLowerCase(tit.charAt(0))) {
                        tit = tit.substring(0, 1).toLowerCase() + tit.substring(1);
                      }
                      target.add(tit);  
                    }
                    else {
                      String subName = ((PortNode)e.getEndNode()).getComponentNode().getSubName();
                      target.add(subName);
                    }
                    target.add(portNameTarget);
                  }
                  ASTQualifiedName astTarget = TypesNodeFactory.createASTQualifiedName(target);
                  targets.add(astTarget);
                }  
              }
              ASTStereoValue stereoConnectVal = MontiArcNodeFactory.createASTStereoValue(c.getStereoType(), "");
              ArrayList<ASTStereoValue> stereoConnectVals = new ArrayList<ASTStereoValue>();
              stereoConnectVals.add(stereoConnectVal);
              ASTStereotype stereoConnect = MontiArcNodeFactory.createASTStereotype(stereoConnectVals);
              ASTConnector astConnector =
                  MontiArcNodeFactory.createASTConnector(stereoConnect, source, targets);
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
          
          String compName = node.getTitle().split(" ")[0];
          String instanceName = (((ComponentNode) node).getSubName());
          System.out.println("instanceName " + instanceName);
          astComponent = 
              MontiArcNodeFactory.createASTComponent(stereotype, compName , 
                  head, instanceName, typeArgs, astBody);
          astMap.put(astComponent, node);
         // add each astComponent to elementsList (the list for the body of the outermost component)
          elements.add(astComponent);
       }    
     }
      
      // create componentBody of outermost component
     ASTComponentBody astBody = 
         MontiArcNodeFactory.createASTComponentBody(elements);
     // create outermost component
     ASTStereotype stereoOutComp = MontiArcNodeFactory.createASTStereotype();
     ASTTypeArguments typeArgsOut = TypesNodeFactory.createASTTypeArguments();
     ASTSimpleReferenceType superCompOut = MontiArcNodeFactory.createASTSimpleReferenceType();
     ASTComponentHead astHead = 
       MontiArcNodeFactory.createASTComponentHead(genCompAllOuter, allParams, superCompOut);
     
     ASTComponent astComp = 
         MontiArcNodeFactory.createASTComponent(stereoOutComp, arg1.get(0), astHead, "", typeArgsOut, astBody);
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
     if (!packParts.isEmpty()) {
       if (!packParts.get(0).isEmpty()) {
         MaPrinter.printPackageName(packParts);
       }
     }
     if (!ast.getImportStatements().isEmpty()) {
       if (!ast.getImportStatements().get(0).getImportList().get(0).isEmpty()) {
         ArrayList<ASTImportStatement> importSt = new ArrayList<ASTImportStatement>();
         for (ASTImportStatement s : ast.getImportStatements()) {
           importSt.add(s);
         }
         MaPrinter.printImport(importSt); 
       }
     }
     MaPrinter.printComponentOuter(ast.getComponent());    
     MaPrinter.getPrinter().flushBuffer();
     String folder = "";
     if (!ast.getPackage().isEmpty()) {
       for (String s : ast.getPackage()) {
         if (!s.isEmpty()) {
           folder = folder + "\\" + s;
         }
       }
     }
     String tmp = usageFolderPath+ folder+ "\\" +arg1.get(0)+"."+MontiArcLanguage.FILE_ENDING;
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
         if (!packParts.isEmpty()) {
           if (!packParts.get(0).isEmpty()) {
             CompPrinter.printPackageName(packParts);
           }
         }
        
         CompPrinter.printComponentInner((ASTComponent) e,  ast.getComponent().getHead().getGenericTypeParameters(), ast);
         CompPrinter.getPrinter().flushBuffer();
         String nameComp = ((ASTComponent)e).getName();
         String first = "";
         if (!Character.isUpperCase(astComp.getName().charAt(0))) {
           nameComp = nameComp.substring(0, 1).toUpperCase() + nameComp.substring(1);
         }
         String tmp1 = usageFolderPath+ folder + "\\"+nameComp+"."+MontiArcLanguage.FILE_ENDING;
         File f1 = new File(tmp1);
         
         BufferedWriter bw1 = null;
         FileWriter fw1 = null;
    
         try {
    
           fw1 = new FileWriter(tmp1);
           bw1 = new BufferedWriter(fw1);
           bw1.write(CompPrinter.getPrinter().getContent());
           
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
    return null;
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
  

  
}
