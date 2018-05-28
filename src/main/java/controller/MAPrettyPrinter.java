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
    for (de.monticore.types.types._ast.ASTImportStatement imp : importDec) {
      handle(imp);
      
      getPrinter().println("");
    }
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printPackageName(ArrayList<String> packageDeclaration) {
    getPrinter().print("package ");
    de.monticore.types.types._ast.ASTQualifiedName pack = TypesNodeFactory.createASTQualifiedName(packageDeclaration);
    handle(pack);
    getPrinter().addLine("");
  }
  
  public void printComponent(de.monticore.lang.montiarc.montiarc._ast.ASTComponent astComp) {
    if (astComp.getStereotype().isPresent()) {
      getPrinter().print(" " + astComp.getStereotype().get().getValues().get(0).getName() + " ");
    }
    getPrinter().print("component ");
    getPrinter().print(astComp.getName() + " ");
    printComponentHead(astComp.getHead());
    if (astComp.getInstanceName().isPresent()) {
      getPrinter().print(astComp.getInstanceName() + " ");
      if (astComp.getActualTypeArgument().isPresent()) {
        handle(astComp.getActualTypeArgument().get()); 
      }
    }
    printComponentBody(astComp.getBody());
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printComponentHead(de.monticore.lang.montiarc.montiarc._ast.ASTComponentHead astHead) {
    System.out.println("generics empty? " + astHead.getGenericTypeParameters().isPresent());
    if (astHead.getGenericTypeParameters().isPresent()) {
      handle(astHead.getGenericTypeParameters().get());
    }
    int paramSize = astHead.getParameters().size();
    int k = 1;
    if(!astHead.getParameters().isEmpty()) {
      getPrinter().print(" (");
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
    printer.indent();
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> ports = 
        new ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort>();
    ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTConnector> connectors = 
        new ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTConnector>();
    
    for (de.monticore.lang.montiarc.montiarc._ast.ASTElement e : astBody.getElements()) {
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTPort) {
        ports.add((de.monticore.lang.montiarc.montiarc._ast.ASTPort)e);
      }
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTConnector) {
        connectors.add((de.monticore.lang.montiarc.montiarc._ast.ASTConnector)e);
      }
      if (e instanceof de.monticore.lang.montiarc.montiarc._ast.ASTComponent) {
        printComponent((de.monticore.lang.montiarc.montiarc._ast.ASTComponent)e);
      }
      
    }
    System.out.println("Ports " + ports.toString());
    printPorts(ports);
    for (de.monticore.lang.montiarc.montiarc._ast.ASTConnector connector : connectors) {
      printConnector(connector);
    }
    printer.unindent();
    getPrinter().println("}");
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printPorts(ArrayList<de.monticore.lang.montiarc.montiarc._ast.ASTPort> astPorts) {
    if (astPorts.size() == 1) {
      getPrinter().println("port");
    }
    else if(astPorts.size() > 1){
      getPrinter().println("ports");
    }
    printer.indent();
    int numPorts = astPorts.size();
    int k = 1;
    if (numPorts > 0) {
      for (de.monticore.lang.montiarc.montiarc._ast.ASTPort port: astPorts) {
        // TODO needs to be changed to optionals
        if (port.getStereotype().isPresent()) {
          getPrinter().print("<<" + port.getStereotype() + ">>");
        }
        if (port.isIncoming()) {
          getPrinter().print("in");
        }
        else {
          getPrinter().print("out");
        }
        handle(port.getType());
        //TODO optional
        if (port.getName().isPresent()) {
          getPrinter().print(port.getName());
        }
        if (k == numPorts) {
          getPrinter().print(";");
        }
        else {
          getPrinter().print(",");
        }
        k++;
      }
      printer.unindent();
    }
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printConnector(de.monticore.lang.montiarc.montiarc._ast.ASTConnector astConnector) {
    if (astConnector.getStereotype().isPresent()) {
      getPrinter().print(astConnector.getStereotype());
    }
    getPrinter().indent();
    getPrinter().print("connect ");
    handle(astConnector.getSource());
    getPrinter().print(" -> ");
    int numTarget = astConnector.getTargets().size();
    int k = 1;
    for (de.monticore.types.types._ast.ASTQualifiedName target : astConnector.getTargets()) {
      handle(target);
      if (k == numTarget) {
        getPrinter().print(";");
        printer.unindent();
      }
      else {
        getPrinter().print(",");
        printer.unindent();
      }
      k++;
    }
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
}
