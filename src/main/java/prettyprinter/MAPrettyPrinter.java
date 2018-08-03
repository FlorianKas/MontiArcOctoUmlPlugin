package prettyprinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.TypesNodeFactory;
import montiarc._ast.*;
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
        getPrinter().print(".*");
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
    printComponentHead(astComp);
    printComponentBody(astComp.getBody());
  }
  
  public void printComponentInner(ASTComponent astComp, Optional<ASTTypeParameters> optional, ASTMACompilationUnit ast) {
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
    printComponentHeadInner(astComp.getHead(), optional, ast);
    printComponentBody(astComp.getBody());
  }
  
  public void printComponentOuter(ASTComponent astComp) {
//    if(astComp.getStereotype().isPresent()) {
//      System.out.println("StereoType " + astComp.getStereotype());	
//      if (!astComp.getStereotype().get().getValues().get(0).getName().equals("")) {
//        getPrinter().print(" " + astComp.getStereotype().get().getValues().get(0).getName() + " ");
//        stereo = false;
//      }
//    }
//    else {
//      stereo = true;
//    }
    stereo = true;
    getPrinter().print("component ");
    ArrayList<ASTTypeVariableDeclaration> allGens = new ArrayList<ASTTypeVariableDeclaration>(); 
//    for ( ASTElement e : astComp.getBody().getElements()) {
//      if (e instanceof ASTComponent) {
//    	if (((ASTComponent) e).getHead().getGenericTypeParameters().isPresent() 
//    	  && ((ASTComponent) e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations() != null
//    	  && !((ASTComponent) e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty()
//        ){
//    	  for (ASTTypeVariableDeclaration typeVar : ((ASTComponent) e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations()) {
//    	    if (!allGens.contains(typeVar)) {
//    	      allGens.add( typeVar);	
//    	    }
//    	  }
//        }
//      }
//    }
//    ASTTypeParameters allGensTo = TypesNodeFactory.createASTTypeParameters(allGens);
//    handle(allGensTo);
    getPrinter().print(astComp.getName() + " ");
    printComponentHead(astComp);
    List<ASTParameter> params = astComp.getHead().getParameters();
    printComponentBodyOuter(astComp.getBody(),params);
//    printConnector(astComp.getBody());
  }
  
  public void printComponentHead(ASTComponent astComp) {
	ASTComponentHead astHead = astComp.getHead();
	ASTComponentBody astBody = astComp.getBody();
	List<ASTElement> astElements = astBody.getElements();
	ArrayList<ASTTypeVariableDeclaration> typeDecs = new ArrayList<ASTTypeVariableDeclaration>();
	if (astHead.getGenericTypeParameters().isPresent() 
	  && astHead.getGenericTypeParameters().get().getTypeVariableDeclarations() != null
	  && !astHead.getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty()
	) {
	  System.out.println("Generics are Present " + astHead.getGenericTypeParameters().get().getTypeVariableDeclarations() + 
	    astHead.getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty());
	  for (ASTTypeVariableDeclaration typeDec: astHead.getGenericTypeParameters().get().getTypeVariableDeclarations()) {
	  	typeDecs.add(typeDec);  
	  }
	}
	    
//	for (ASTElement e : astElements) {
//	  if (e instanceof ASTComponent) {
//	    if(((ASTComponent)e).getHead().getGenericTypeParameters().isPresent() 
//	      && ((ASTComponent)e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations() != null
//	      && !((ASTComponent)e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty()
//	    ) {
//		  for(ASTTypeVariableDeclaration typeDec: ((ASTComponent)e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations()) {
//		    typeDecs.add(typeDec);  	 
//		  }
//	    }
//	  }
//	}
	if (typeDecs.size()>0) {
	  ASTTypeParameters typeParams = TypesNodeFactory.createASTTypeParameters(typeDecs);
	  handle(typeParams);
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
      if (!((ASTSimpleReferenceType)astHead.getSuperComponent().get()).getNames().isEmpty()) {
    	getPrinter().print("extends");
        handle(astHead.getSuperComponent().get());
      }
      
    } 
//    getPrinter().flushBuffer();
//    System.out.println("Test "+ getPrinter().getContent());
  }
  
  
  public void printComponentHeadInner(ASTComponentHead astHead, Optional<ASTTypeParameters> optional, ASTMACompilationUnit ast) {
    List<ASTTypeVariableDeclaration> compGen = new ArrayList<ASTTypeVariableDeclaration>();
    if (astHead.getGenericTypeParameters().isPresent() && astHead.getGenericTypeParameters().get().getTypeVariableDeclarations() != null
        && !astHead.getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty()
        ) {
      compGen.addAll(astHead.getGenericTypeParameters().get().getTypeVariableDeclarations());
    }
    ASTTypeParameters tmp = TypesNodeFactory.createASTTypeParameters(compGen);
    System.out.println("tmp " + tmp.toString());
    handle(tmp);
  
    int paramSize = astHead.getParameters().size();
    int k = 1;
    if(!astHead.getParameters().isEmpty() && astHead.getParameters() != null) {
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
      if (!((ASTSimpleReferenceType)astHead.getSuperComponent().get()).getNames().isEmpty()) {
        getPrinter().print("extends");
        handle(astHead.getSuperComponent().get());
      }
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
  
  public void printComponentBodyOuter(ASTComponentBody astBody, List<ASTParameter> params) {
    System.out.println("params " + params.toString());
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
        
        // print Generics here
        if (((ASTComponent) e).getHead().getGenericTypeParameters().isPresent() && 
          ((ASTComponent) e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations() != null
          && !((ASTComponent) e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty()
        ) {
//              System.out.println("Generics are Present " + astHead.getGenericTypeParameters().get().getTypeVariableDeclarations() + 
//                  astHead.getGenericTypeParameters().get().getTypeVariableDeclarations().isEmpty());
          ArrayList<String> extendNames = new ArrayList<String>();
          for (ASTTypeVariableDeclaration typeDec : ((ASTComponent) e).getHead().getGenericTypeParameters().get().getTypeVariableDeclarations()) {
        	extendNames.add(typeDec.getName());  
          }
          
          String first = extendNames.remove(0);
          if (extendNames.size()>0) {
            String last = extendNames.remove(extendNames.size()-1);
            getPrinter().print("<"+first+",");
            for (String t : extendNames) {
              getPrinter().print(t+",");	
            }
            getPrinter().print(last+">");
          }
          else {
        	getPrinter().print("<"+first+">");  
          }
          
        }
        ArrayList<ASTParameter> allParams = new ArrayList<ASTParameter>();
        allParams.addAll(params);
        allParams.addAll(((ASTComponent)e).getHead().getParameters());
        System.out.println("allParams " + allParams.toString());
        for (ASTParameter p : ((ASTComponent)e).getHead().getParameters()) {
          if(p.getDefaultValue().isPresent()) {
            i = i + 1;
          }
        }
        i = i + params.size();
        int j = 0;
        if (i != j) {
          for (ASTParameter p : allParams) {
            System.out.println("Parameter p " + p.toString());
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
            else {
              if (params.contains(p)) {
                System.out.println("We detected contains");
                j = j + 1;
                if (j == 1 && j == i) {
                  getPrinter().print("(" + p.getName() + ")");
                  break;
                } 
                else if (j == 1) {
                  getPrinter().print("(" + p.getName() + ",");
                }
                else if (i == j) {
                  getPrinter().print(p.getName() + ")");
                }
                else {
                  getPrinter().print(p.getName() + ",");
                }
              }
            }
          }
        }
        String compName = "";
        if ( ((ASTComponent)e).getInstanceName().isPresent() && !(((ASTComponent)e).getInstanceName().get().equals(""))) {
          String tmpName = ((ASTComponent)e).getInstanceName().get();
          if(Character.isUpperCase(tmpName.charAt(0))) {
            compName = tmpName.substring(0, 1).toLowerCase() + tmpName.substring(1);
          }
          else {
            compName = tmpName;
          }
        }
        else {
          compName = ((ASTComponent)e).getName();
          if(Character.isUpperCase(compName.charAt(0))) {
            compName = compName.substring(0, 1).toLowerCase() + compName.substring(1);
          }
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
        printConnector(connector, astBody);
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
  
  public void printConnector(ASTConnector astConnector, ASTComponentBody astBody) {
    getPrinter().print("  ");
    if (astConnector.getStereotype().isPresent()) {
      if(!astConnector.getStereotype().get().getValues().isEmpty()) {
    	for (ASTStereoValue val: astConnector.getStereotype().get().getValues()) {
    	  if (!val.getName().equals("")) {
    		getPrinter().print(val.getName());  
    	  }
    	}
      }
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
