package controller;

import java.util.ArrayList;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.TypesNodeFactory;

public class MAPrettyPrinter extends TypesPrettyPrinterConcreteVisitor{
  
  
  public MAPrettyPrinter(IndentPrinter printer) {
    super(printer);
    // TODO Auto-generated constructor stub
  }
  
  public void printImport(ArrayList<de.monticore.types.types._ast.ASTImportStatement> importDec) {
    if (importDec.isEmpty()) {
      System.out.println("ImportDec " + importDec.toString() + importDec.isEmpty());
      for (de.monticore.types.types._ast.ASTImportStatement imp : importDec) {
        de.monticore.types.types._ast.ASTQualifiedName imported =  TypesNodeFactory.createASTQualifiedName(imp.getImportList());
        getPrinter().print("import ");
        handle(imported);
        if (imp.isStar()) {
          getPrinter().print("*");
        }
        getPrinter().print(";");
        getPrinter().println("");
      }
    }
  }
  
  public void printPackageName(ArrayList<String> packageDeclaration) {
    getPrinter().print("package ");
    de.monticore.types.types._ast.ASTQualifiedName pack = TypesNodeFactory.createASTQualifiedName(packageDeclaration);
    handle(pack);
    getPrinter().print(";");
    getPrinter().addLine("");
  }
  
  public void printComponent(de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComp) {
    if (astComp.getStereotype().isPresent()) {
      getPrinter().print(" " + astComp.getStereotype().get().getValues().get(0).getName() + " ");
    }
    getPrinter().print("component ");
    getPrinter().print(astComp.getName() + " ");
    printComponentHead(astComp.getHead());
//    if (astComp.getInstanceName().isPresent()) {
//      getPrinter().print(astComp.getInstanceName() + " ");
//      if (astComp.getActualTypeArgument().isPresent()) {
//        handle(astComp.getActualTypeArgument().get()); 
//      }
//    }
    printComponentBody(astComp.getBody());
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printComponentOuter(de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComp) {
    if (astComp.getStereotype().isPresent()) {
      getPrinter().print(" " + astComp.getStereotype().get().getValues().get(0).getName() + " ");
    }
    getPrinter().print("component ");
    getPrinter().print(astComp.getName() + " ");
    printComponentHead(astComp.getHead());
//    if (astComp.getInstanceName().isPresent()) {
//      getPrinter().print(astComp.getInstanceName() + " ");
//      if (astComp.getActualTypeArgument().isPresent()) {
//        handle(astComp.getActualTypeArgument().get()); 
//      }
//    }
    printComponentBodyOuter(astComp.getBody());
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printComponentHead(de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead astHead) {
    System.out.println("generics empty? " + astHead.getGenericTypeParameters().isPresent());
    if (astHead.getGenericTypeParameters().isPresent() && astHead.getGenericTypeParameters().get().getTypeVariableDeclarations() != null
        && !astHead.getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty()
        ) {
      System.out.println("Generics are Present " + astHead.getGenericTypeParameters().get().getTypeVariableDeclarations() + 
          astHead.getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty());
      handle(astHead.getGenericTypeParameters().get());
    }
    int paramSize = astHead.getParameters().size();
    int k = 1;
    if(!astHead.getParameters().isEmpty() && astHead.getParameters() != null) {
      System.out.println("params " + astHead.getParameters().toString());
      getPrinter().print("(");
      for (de.monticore.lang.montiarc.common._ast.ASTParameter param : astHead.getParameters()) {
        // TODO build optional
        if (param != null) {
          handle((de.monticore.types.types._ast.ASTSimpleReferenceType)param.getType());
          getPrinter().print(" " + param.getName());
        }
        if (k != paramSize) {
          getPrinter().print(", ");
        }
        k++;
      }
      getPrinter().print(") ");
    }
    if (astHead.getSuperComponent().isPresent()) {
      getPrinter().print("extends");
      handle(astHead.getSuperComponent().get());
    } 
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printComponentBody(de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody) {
    getPrinter().println("{");
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> ports = 
        new ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort>();
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTConnector> connectors = 
        new ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTConnector>();
    
    for (de.monticore.lang.montiarc.montiarc._ast.ASTElement e : astBody.getElements()) {
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTInterface) {
        for (de.monticore.lang.montiarc.montiarc._ast.ASTPort p : ((de.monticore.lang.montiarc.montiarc._ast.ASTInterface) e).getPorts()) {
          ports.add((de.monticore.lang.montiarc.montiarc._ast.ASTPort)p);
        }
      }
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTConnector) {
        System.out.println("e is a connector ");
        connectors.add((de.monticore.lang.montiarc.montiarc._ast.ASTConnector)e);
      }
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTComponent) {
        getPrinter().print("");
        printComponent((de.monticore.lang.montiarc.montiarc._ast.ASTComponent)e);
      }
      
    }
    System.out.println("Ports " + ports.toString());
    printPorts(ports);
    System.out.println("Connectors " + connectors);
    
    if (connectors != null ) {
      System.out.println("Connectors " + connectors);
    
      for (de.monticore.lang.montiarc.montiarc._ast.ASTConnector connector : connectors) {
        printConnector(connector);
      }
    }

    getPrinter().println("  }");
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printComponentBodyOuter(de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody astBody) {
    getPrinter().println("{");
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> ports = 
        new ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort>();
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTConnector> connectors = 
        new ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTConnector>();
    
    for (de.monticore.lang.montiarc.montiarc._ast.ASTElement e : astBody.getElements()) {
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTInterface) {
        for (de.monticore.lang.montiarc.montiarc._ast.ASTPort p : ((de.monticore.lang.montiarc.montiarc._ast.ASTInterface) e).getPorts()) {
          ports.add((de.monticore.lang.montiarc.montiarc._ast.ASTPort)p);
        }
      }
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTConnector) {
        System.out.println("e is a connector ");
        connectors.add((de.monticore.lang.montiarc.montiarc._ast.ASTConnector)e);
      }
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTComponent) {
        getPrinter().print("");
        printComponent((de.monticore.lang.montiarc.montiarc._ast.ASTComponent)e);
      }
      
    }
    System.out.println("Ports " + ports.toString());
    printPorts(ports);
    getPrinter().println();
    System.out.println("Connectors " + connectors);
    
    if (connectors != null ) {
      System.out.println("Connectors " + connectors);
    
      for (de.monticore.lang.montiarc.montiarc._ast.ASTConnector connector : connectors) {
        printConnector(connector);
      }
    }

    getPrinter().println("}");
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printPorts(ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> astPorts) {
    if (astPorts.size() == 1) {
      getPrinter().println("    port");
    }
    else if(astPorts.size() > 1){
//      getPrinter().indent();
      getPrinter().println("    ports");
    }
    int numPorts = astPorts.size();
    int k = 1;
    if (numPorts > 0) {
      for (de.monticore.lang.montiarc.montiarc._ast.ASTPort port: astPorts) {
        // TODO needs to be changed to optionals
//        getPrinter().indent();
        if (port.getStereotype().isPresent()) {
          getPrinter().print("<<" + port.getStereotype() + ">>");
        }
        if (port.isIncoming()) {
          getPrinter().print("      in ");
        }
        else {
          getPrinter().print("      out ");
        }
        handle((de.monticore.types.types._ast.ASTSimpleReferenceType)port.getType());
        getPrinter().print(" ");
        //TODO optional
        if (port.getName().isPresent()) {
          getPrinter().print(port.getName().get());
        }
        if (k == numPorts) {
          getPrinter().println(";");
        }
        else {
          getPrinter().println(",");
        }
        k++;
      }
//      getPrinter().unindent();
    }
//    getPrinter().unindent();
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printConnector(de.monticore.lang.montiarc.montiarc._ast.ASTConnector astConnector) {
    getPrinter().print("    ");
    if (astConnector.getStereotype().isPresent()) {
      getPrinter().print(astConnector.getStereotype());
    }
//    getPrinter().indent();
    getPrinter().print("connect ");
    handle(astConnector.getSource());
    getPrinter().print(" -> ");
    int numTarget = astConnector.getTargets().size();
    int k = 1;
    System.out.println("target looks as follows " + astConnector.toString());
    for (de.monticore.types.types._ast.ASTQualifiedName target : astConnector.getTargets()) {
      handle(target);
      if (k == numTarget) {
        getPrinter().print(";");
//        getPrinter().unindent();
      }
      else {
        getPrinter().print(",");
//        getPrinter().unindent();
      }
      k++;
    }
    getPrinter().println();
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
}
