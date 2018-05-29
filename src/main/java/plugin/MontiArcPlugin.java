package plugin;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import controller.AbstractDiagramController;
import controller.MAPrettyPrinter;
import controller.MontiArcController;
//import controller.MontiCorePlugIn;

//import controller.AbstractDiagramController;

import javafx.stage.Stage;

import montiarc._ast.ASTMontiArcNode;
import view.nodes.AbstractNodeView;
import montiarc._ast.ASTMACompilationUnit;
import de.monticore.ast.ASTNode;

import de.monticore.common.common._ast.*;
import de.monticore.common.common._ast.ASTStereotype.Builder;
import de.monticore.types.types._ast.*;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

import de.monticore.lang.montiarc.common._ast.ASTParameter;
import de.monticore.lang.montiarc.montiarc._ast.*;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.prettyprint.IndentPrinter;

import de.monticore.java.javadsl._ast.ASTCompilationUnit;
import de.monticore.java.javadsl._ast.JavaDSLNodeFactory;

import model.Graph;
import model.GraphElement;
import model.edges.ConnectorEdge;
import model.edges.Edge;
import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;

public class MontiArcPlugin implements MontiCorePlugIn {
  private String usageFolderPath;
  private static final MontiArcPlugin plugIn = new MontiArcPlugin();
  
  public MontiArcPlugin(){
    
  }
  public AbstractDiagramController getController() {
    try {
      Class c = getClass().getClassLoader().loadClass("controller.MontiArcController");
      return (MontiArcController)c.newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }return null;
  }
  
  public static MontiArcPlugin getInstance() {
    return plugIn;
  }
  
  public void setUsageFolderPath(String path) {
    usageFolderPath = path;
  }
  
  public String getView() {
	  String view= "view/fxml/montiArcView.fxml";
	  return view;
  }
  
  public String getDSLName() {
	  String DSLName = "MontiArc";
	  return DSLName;
  }
  
  public String getFileEnding() {
	  return ".arc";
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
	  return null;  
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
      System.out.println("g" + g);
   // create generic type params
      String name = "";
      ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType> upperBounds = 
          new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
      System.out.println("Length of g" + g.split("extends").length );
      if (g.split("extends").length > 1) {
        name = (g.split("extends")[0]).replaceAll("\\s+","");
        System.out.println("Name " + name);
        System.out.println("Before findExtends " + g.split("extends")[1]);
        ArrayList<String> supClasses = findExtends(g.split("extends")[1]);
        System.out.println("supClasses " + supClasses.toString());
        
//        String[] supClasses = g.split("extends")[1].split("\\;");
//        System.out.println("supClasses" + supClasses.length);
        for(String sup : supClasses) {
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
          
          List<de.monticore.types.types._ast.ASTSimpleReferenceType> firstList = 
              new ArrayList<de.monticore.types.types._ast.ASTSimpleReferenceType>();
          List<de.monticore.types.types._ast.ASTComplexReferenceType> upper= 
              new ArrayList<de.monticore.types.types._ast.ASTComplexReferenceType>();
          
          for (String u : firstSplit) {
            // divide supClasses at '.'
            String[] partsSmaller = u.split("\\<");
            System.out.println("partsSmaller" + partsSmaller.length);
            System.out.println("parts Smaller " + partsSmaller);
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
            String[] without = withoutFirst.split("\\>");
            String lastElement = without[without.length-1];
            String partsGreater[] = withoutFirst.split("\\>");
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
            String[] partsTmp = withoutLast.split("\\,");
            String firstElement = partsSmaller[0];
            System.out.println("First Element " + firstElement);
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
          
          de.monticore.types.types._ast.ASTTypeVariableDeclaration varDec = 
              TypesNodeFactory.createASTTypeVariableDeclaration(name, upper);
          genericsTypes.add(varDec);
        }   
      }  
    }
    return genericsTypes;    
  }
  
  protected ArrayList<de.monticore.lang.montiarc.common._ast.ASTParameter> genASTTypes(ArrayList<String> astTypes){
  //create types param
    ArrayList<de.monticore.lang.montiarc.common._ast.ASTParameter> parameters = new ArrayList<ASTParameter>();
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
          de.monticore.lang.montiarc.common._ast.ASTParameter parameter = 
              MontiArcNodeFactory.createASTParameter(varDec, typeName, null);
          parameters.add(parameter);    
        }
      }
    }
    return parameters;
  }
  
  
  @Override
  public ASTNode shapeToAST(Graph arg0, List<String> arg1) {
    
    ArrayList<String> generics = MontiArcController.genericsArray;
    ArrayList<String> astTypes = MontiArcController.types;
    String packageDec = MontiArcController.packageName;
    ArrayList<String> imports = MontiArcController.importStatements;
    // temporary
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
//    ASTQualifiedName packageName = TypesNodeFactory.createASTQualifiedName(packageParts);
//    List<de.monticore.java.javadsl._ast.ASTAnnotation> annotations = 
//        new ArrayList<de.monticore.java.javadsl._ast.ASTAnnotation>();
//    de.monticore.java.javadsl._ast.ASTPackageDeclaration packageDeclaration = 
//        JavaDSLNodeFactory.createASTPackageDeclaration(annotations, packageName);
    
    
    ArrayList<ASTTypeVariableDeclaration> genericsTypes = new ArrayList<ASTTypeVariableDeclaration>();
    if (!generics.isEmpty()) {
      genericsTypes = createGenerics(generics);
    } 
    // Generics, needs to be added by construction of componentHead
    // Darf auch leer sein. Dann muss ja eigentlich doch ein Optional zurueckgegeben werden der leer ist oder?
    de.monticore.types.types._ast.ASTTypeParameters typeParams = 
        TypesNodeFactory.createASTTypeParameters(genericsTypes);
    
    
    //create types param
    
    ArrayList<de.monticore.lang.montiarc.common._ast.ASTParameter> parameters = genASTTypes(astTypes);
    
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTElement> elements = new ArrayList<ASTElement>();
    de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComponent = null;
  
      // create outermost component head
    de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead astHead = 
        MontiArcNodeFactory.createASTComponentHead(typeParams, parameters, null);
      
    // create params for outermost component body
    for (AbstractNode node : arg0.getAllNodes()) {
      if (node instanceof ComponentNode) {
        de.monticore.common.common._ast.ASTStereotype.Builder builder = new Builder();
        ASTStereoValue value = CommonNodeFactory.createASTStereoValue(((ComponentNode) node).getStereotype(), null);
        ArrayList<ASTStereoValue> valueList = new ArrayList<ASTStereoValue>();
        valueList.add(value);
        builder.values(valueList);
        de.monticore.common.common._ast.ASTStereotype stereotype = builder.build();
        String gen = ((ComponentNode) node).getGenerics();
        String[] gen1 = gen.split("\\;");
        ArrayList<String> gens = new ArrayList<String>();
        for (String s: gen1) {
          gens.add(s);
        }
        ArrayList<ASTTypeVariableDeclaration> genComp = createGenerics(gens);
        de.monticore.types.types._ast.ASTTypeParameters typeGenComp = 
            TypesNodeFactory.createASTTypeParameters(genComp);
        String tyComp = ((ComponentNode) node).getComponentType();
        String[] tyComp1 = tyComp.split("\\,");
        System.out.println("Types Splitted Size " + tyComp1.length);
        ArrayList<String> tyComp2 = new ArrayList<String>();
        for (String s : tyComp1) {
          tyComp2.add(s);
        }
        System.out.println("tyComp2 size " + tyComp2.size());
        ArrayList<de.monticore.lang.montiarc.common._ast.ASTParameter> typesComp = genASTTypes(tyComp2); 
        ArrayList<String> names = new ArrayList<String>();
        de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead head = 
            MontiArcNodeFactory.createASTComponentHead(typeGenComp,typesComp, null);
        ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTElement> bodyElements = new ArrayList<ASTElement>();
        
        ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> astPorts = new ArrayList<ASTPort>();
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
          System.out.println("astPort " +astPort.toString());
          astPorts.add(astPort);
        }
        
        // create astInterface (containing the ports) for each ComponentNode
        // TODO add stereotype for each port
        de.monticore.lang.montiarc.montiarc._ast.ASTInterface astInterface = 
            MontiArcNodeFactory.createASTInterface(null, astPorts);
        System.out.println("astINterface  " + astInterface);
        // add astInterface to bodyList of current astComponent
        bodyElements.add(astInterface);
        System.out.println("bodyelements " + bodyElements);
        
        
        // add Connectors for this Component
        ArrayList<ConnectorEdge> edges = new ArrayList<ConnectorEdge>();
        for (Edge e: arg0.getAllEdges()) {
          if (e instanceof ConnectorEdge) {
            if (((PortNode)e.getStartNode()).getComponentNode() == node ||
                ((PortNode)e.getEndNode()).getComponentNode() == node) {
                edges.add((ConnectorEdge)e);
            }
          }
        }
        ArrayList<PortNode> visited = new ArrayList<PortNode>();
        for (ConnectorEdge c : edges) {
          if (!visited.contains(c)) {
            ArrayList<String> title = new ArrayList<String>();
            String portName = c.getStartNode().getTitle();
            if (portName.split(".").length > 0) {
              for (String s : portName.split(".")) {
                title.add(s);
              }
            }
            else {
              title.add(((PortNode)c.getStartNode()).getComponentNode().getTitle());
              title.add(portName);
            }
            ASTQualifiedName source = TypesNodeFactory.createASTQualifiedName(title);
            ArrayList<ASTQualifiedName> targets = new ArrayList<ASTQualifiedName>();
            visited.add(c.getStartPort());
            ArrayList<String> firstTarget = new ArrayList<String>();
            firstTarget.add(((PortNode)c.getEndNode()).getComponentNode().getTitle());
            firstTarget.add(c.getEndNode().getTitle());
            ASTQualifiedName astTargetFirst = TypesNodeFactory.createASTQualifiedName(firstTarget);
            targets.add(astTargetFirst);
            for (ConnectorEdge e: edges) {
              if (c.getStartNode() == e.getStartNode() && c != e && !visited.contains(e)) {
                // create all targets
                ArrayList<String> target = new ArrayList<String>();
                String portNameTarget = e.getEndNode().getTitle();
                if (portNameTarget.split(".").length > 0) {
                  for (String s : portNameTarget.split(".")) {
                    target.add(s);
                  }
                }
                else {
                  target.add(((PortNode)e.getEndNode()).getComponentNode().getTitle());
                  target.add(portName);
                }
                ASTQualifiedName astTarget = TypesNodeFactory.createASTQualifiedName(target);
                targets.add(astTarget);
              }  
            }
            de.monticore.lang.montiarc.montiarc._ast.ASTConnector astConnector =
                MontiArcNodeFactory.createASTConnector(null, source, targets);
            bodyElements.add(astConnector);
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
       // add each astComponent to elementsList (the list for the body of the outermost component)
        elements.add(astComponent);
     }    
   }
    
    // create componentBody of outermost component
   de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody = 
       MontiArcNodeFactory.createASTComponentBody(elements);
   // create outermost component
   
//   List<de.monticore.common.common._ast.ASTStereoValue> tmp = new ArrayList<de.monticore.common.common._ast.ASTStereoValue>();
//   tmp.add(CommonNodeFactory.createASTStereoValue("tmp", "test"));
//   de.monticore.common.common._ast.ASTStereotype stereo = CommonNodeFactory.createASTStereotype(tmp);
   de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComp = 
       MontiArcNodeFactory.createASTComponent(null, arg1.get(0), astHead, null, null, astBody);
   // create AST
   de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit ast = 
       MontiArcNodeFactory.createASTMACompilationUnit(packageParts, importDecls, astComp);
    
  // PrettyPrinting
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
   ArrayList<ASTImportStatement> importSt = new ArrayList<ASTImportStatement>();
   for (ASTImportStatement s : ast.getImportStatements()) {
     importSt.add(s);
   }
   MaPrinter.printImport(importSt); 
   MaPrinter.printComponent(ast.getComponent());    
   MaPrinter.getPrinter().flushBuffer();
   System.out.println(MaPrinter.getPrinter().getContent());
   
   usageFolderPath = "C:\\Users\\Flo\\Desktop\\usage\\test.txt";
   File f = new File(usageFolderPath);
   boolean blnCreated = false;
   try
   {
     blnCreated = f.createNewFile();
   }
   catch(IOException ioe)
   {
     System.out.println("Error while creating a new empty file :" + ioe);
   }
   System.out.println("Was file " + f.getPath() + " created ? : " + blnCreated);
   
   
   
     BufferedWriter bw = null;
     FileWriter fw = null;

     try {

       String content = MaPrinter.getPrinter().getContent();

       fw = new FileWriter(usageFolderPath);
       bw = new BufferedWriter(fw);
       bw.write(content);

       System.out.println("Done");

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

   
   
   return ast;
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
  public List<MontiCoreException> check(ASTNode arg0, HashMap<AbstractNodeView, AbstractNode> arg1) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public List<String> showContainerInfoDialog(Stage arg0, List<String> arg1) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
