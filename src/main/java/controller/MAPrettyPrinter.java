package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Optional;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;
//import ASTComponent;
//import ASTComponentHead;
//import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.TypesNodeFactory;
import montiarc._ast.*;
import montiarc._ast.ASTComponentBody;
import montiarc._ast.ASTComponentHead;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._parser.MontiArcParserTOP;
import plugin.MontiArcPlugin;

public class MAPrettyPrinter extends TypesPrettyPrinterConcreteVisitor{
  
  public boolean stereo = false;
  public MAPrettyPrinter(IndentPrinter printer) {
    super(printer);
    // TODO Auto-generated constructor stub
  }
  
  public void printImport(ArrayList<de.monticore.types.types._ast.ASTImportStatement> importDec) {
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
  
  public void printPackageName(ArrayList<String> packageDeclaration) {
    getPrinter().print("package ");
    de.monticore.types.types._ast.ASTQualifiedName pack = TypesNodeFactory.createASTQualifiedName(packageDeclaration);
    handle(pack);
    getPrinter().print(";");
    getPrinter().addLine("");
  }
  
  public void printComponent(ASTComponent astComp) {
    if (astComp.getStereotype().isPresent()) {
      if (!astComp.getStereotype().get().getValues().get(0).getName().isEmpty()) {
        getPrinter().print(" " + astComp.getStereotype().get().getValues().get(0).getName() + " ");
        stereo = false;
      }
      else {
        stereo = true;
      }
    }
    getPrinter().print("component ");
    getPrinter().print(astComp.getName() + " ");
    printComponentHead(astComp.getHead());
    printComponentBody(astComp.getBody());
  }
  
  public void printComponentOuter(ASTComponent astComp) {
    if(astComp.getStereotype().isPresent()) {
      if (!astComp.getStereotype().get().getValues().get(0).getName().isEmpty()) {
        getPrinter().print(" " + astComp.getStereotype().get().getValues().get(0).getName() + " ");
        stereo = false;
      }
    }
    else {
      stereo = true;
    }
    getPrinter().print("component ");
    getPrinter().print(astComp.getName() + " ");
    printComponentHead(astComp.getHead());
    printComponentBodyOuter(astComp.getBody());
//    printConnector(astComp.getBody());
  }
  
  public void printComponentHead(ASTComponentHead astHead) {
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
      for (ASTParameter param : astHead.getParameters()) {
        // TODO build optional
        if (param != null) {
          if (param.getType() instanceof ASTPrimitiveType) {
            handle((ASTPrimitiveType)param.getType());
          }
          else if(param.getType() instanceof ASTSimpleReferenceType) {
            handle((ASTSimpleReferenceType)param.getType());
          }
          else if(param.getType() instanceof ASTComplexReferenceType){
            handle((ASTComplexReferenceType)param.getType());
          }
          else {
            handle(param.getType());
          }
          System.out.println("NAME OF PARAM IS " + param.getName());
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
  
  public void printComponentBody(ASTComponentBody astBody) {
    getPrinter().println("{");
    ArrayList<ASTPort> ports = 
        new ArrayList<ASTPort>();
    for (ASTElement e : astBody.getElements()) {
      if (e instanceof ASTInterface) {
        for (ASTPort p : ((ASTInterface) e).getPorts()) {
          ports.add((ASTPort)p);
        }
      }
      if (e instanceof ASTComponent) {
        getPrinter().print("");
        printComponent((ASTComponent)e);
      }
      
    }
    System.out.println("Ports " + ports.toString());
    printPorts(ports);
    
    getPrinter().println("  }");
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printComponentBodyOuter(ASTComponentBody astBody) {
    ArrayList<ASTConnector> connectors = 
        new ArrayList<ASTConnector>();
    ArrayList<ASTComponent> comps = 
        new ArrayList<ASTComponent>();
    getPrinter().println("{");
    for (ASTElement e : astBody.getElements()) {
      if (e instanceof ASTComponent) {
        getPrinter().print("");
        getPrinter().print("  component " + ((ASTComponent)e).getName());
        int i = 0;
        for (ASTParameter p : ((ASTComponent)e).getHead().getParameters()) {
          if(p.getDefaultValue().isPresent()) {
            i = i + 1;
          }
        }
        int j = 0;
        if (i != j) {
          for (ASTParameter p : ((ASTComponent)e).getHead().getParameters()) {
            if (p.getDefaultValue().isPresent()) {
              ASTLiteral temp = p.getDefaultValue().get().getExpression().getPrimaryExpression().get().getLiteral().get();
              String mom = temp.toString();
              mom = mom.split("\\Source: ")[1];
              System.out.println("MOM " + mom);
              j = j + 1;
              if (j == 1 && j == i) {
                getPrinter().print("(" + mom + ")");
                break;
              } 
              else if (j == 1) {
                getPrinter().print("(" + mom + ",");
              }
              else if (i == j) {
                getPrinter().print(mom + ")");
              }
              else {
                getPrinter().print(mom + ",");
              }
            }
          }
        }
        String compName = ((ASTComponent)e).getName();
        if(Character.isUpperCase(compName.charAt(0))) {
          compName = compName.substring(0, 1).toLowerCase() + compName.substring(1);
        }
        getPrinter().print(" " +compName);
        getPrinter().println(";");
        comps.add((ASTComponent) e);
      }      
    }
    for (ASTComponent c : comps) {
      connectors.addAll(c.getConnectors());
    }
    if (connectors != null ) {
      System.out.println("Connectors " + connectors);
    
      for (ASTConnector connector : connectors) {
        printConnector(connector);
      }
    }
    getPrinter().println("}");
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  public void printPorts(ArrayList<ASTPort> astPorts) {
    int numPorts = astPorts.size();
    int k = 1;
    if (numPorts > 0) {
      if (stereo == true) {
        getPrinter().println("    port");
      }
      else {
        getPrinter().println("  port");
      }
      for (ASTPort port: astPorts) {
        // TODO needs to be changed to optionals
//        getPrinter().indent();
        System.out.println("port STEREOTYPE " + port.getStereotype().get().getValues().get(0).getName().isEmpty());
        if (port.getStereotype().get().getValues().get(0).getName().isEmpty()) {
          getPrinter().print(port.getStereotype().get().getValues().get(0).getName());
        }
        if (port.isIncoming()) {
          if (stereo == true) {
            getPrinter().print("      in ");
          }
          else {
            getPrinter().print("    in ");
          }
        }
        else {
          if (stereo == true) {
            getPrinter().print("      out ");
          }
          else {
            getPrinter().print("    out ");
          }
        }
        
        if (port.getType() instanceof ASTPrimitiveType) {
          handle((ASTPrimitiveType)port.getType());
        }
        else if(port.getType() instanceof ASTSimpleReferenceType) {
          handle((ASTSimpleReferenceType)port.getType());
        }
        else if(port.getType() instanceof ASTComplexReferenceType){
          handle((ASTComplexReferenceType)port.getType());
        }
        else {
          handle(port.getType());
        }
        getPrinter().print(" ");
        if (!port.getNames().isEmpty()) {
          getPrinter().print(port.getNames().get(0));
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
  
  public void printConnector(ASTConnector astConnector) {
    getPrinter().print("  ");
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
