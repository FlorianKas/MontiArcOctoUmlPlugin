package controller;

import edu.tamu.core.sketch.BoundingBox;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import model.*;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;
import model.edges.AssociationEdge;
import model.nodes.AbstractNode;
import model.nodes.ClassNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import model.nodes.Node;
import util.commands.AddDeleteEdgeCommand;
import util.commands.AddDeleteNodeCommand;
import util.commands.Command;
import util.commands.CompoundCommand;
import view.edges.AbstractEdgeView;
import view.nodes.AbstractNodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used by MainController for handling the recognition of drawn shapes and transforming them in to UML-notations.
 */
public class RecognizeController {
  private Pane aDrawPane;
  private AbstractDiagramController diagramController;
  private PaleoSketchRecognizer recognizer;
  private Graph graph;
  HashMap<BoundingBox, Sketch> sketchMap = new HashMap<>();
  

  public RecognizeController(Pane pDrawPane, AbstractDiagramController pController) {
      aDrawPane = pDrawPane;
      diagramController = pController;
      graph = diagramController.getGraphModel();
      //TODO Find a nicer solution for this:
      //This is to load the recognizer when starting app, not when starting to draw.

      recognizer = new PaleoSketchRecognizer(PaleoConfig.allOn());
      Stroke init = new Stroke();
      init.addPoint(new Point(0,1));
      recognizer.setStroke(init);
      recognizer.recognize().getBestShape();
  }

  public synchronized void recognize(List<Sketch> sketches) {
    ArrayList<AbstractNode> recognizedNodes = new ArrayList();
    ArrayList<Sketch> sketchesToBeRemoved = new ArrayList<>();
    ArrayList<AbstractEdge> recognizedEdges = new ArrayList<>();
    ArrayList<BoundingBox> boxes = new ArrayList();
    CompoundCommand recognizeCompoundCommand = new CompoundCommand();

        
    //Go through all sketches to find Nodes.
    for (Sketch s : sketches) {
      if (s.getStroke() != null && s.getStroke().getPoints() != null && !s.getStroke().getPoints().isEmpty()) {
        //TODO This sometimes throws IndexOutOfBoundsException...
        recognizer.setStroke(s.getStroke());
        Shape bestMatch = recognizer.recognize().getBestShape();
        String bestMatchString = bestMatch.getInterpretation().label;
        if (bestMatchString.equals("Square") || bestMatchString.equals("Rectangle")) {
          BoundingBox box = s.getStroke().getBoundingBox();
          ClassNode node = new ClassNode(box.getX(), box.getY(),box.getWidth(), box.getHeight());
          s.setRecognizedElement(node);
          recognizedNodes.add(node);
          sketchesToBeRemoved.add(s);
          graph.addNode(node, false); //Really don't want to do this until mainController.createNodeView but need to because of graph.findNode.
        }

      }
    }
        
        
    //Go through all sketches to find edges. Edges need to be recognized after nodes
    for (Sketch s : sketches) {
      if (s.getStroke() != null) {
        recognizer.setStroke(s.getStroke());
        String bestMatchString = recognizer.recognize().getBestShape().getInterpretation().label;
        if (bestMatchString.equals("Line") || bestMatchString.startsWith("Polyline") || bestMatchString.equals("Arc") ||
          bestMatchString.equals("Curve") || bestMatchString.equals("Arrow")){
          Point2D startPoint = new Point2D(s.getStroke().getFirstPoint().getX(), s.getStroke().getFirstPoint().getY());
          Point2D endPoint = new Point2D(s.getStroke().getLastPoint().getX(), s.getStroke().getLastPoint().getY());
          Node startNode = graph.findNode(startPoint);
          Node endNode = graph.findNode(endPoint);

          //For arrows, which don't have an endpoint
          List<Point> points = s.getStroke().getPoints();
          for (int i = points.size()-1; i > points.size()/2; i--) {
            Point2D point = new Point2D(points.get(i).getX(), points.get(i).getY());
            if (graph.findNode(point) != null) {
              endNode = graph.findNode(point);
              break;
            }
          }

          if (startNode != null && endNode != null && !startNode.equals(endNode)) {
            AssociationEdge newEdge = new AssociationEdge(startNode, endNode);
            if (bestMatchString.equals("Arrow")) {
              newEdge.setDirection(AbstractEdge.Direction.START_TO_END);
            }
            s.setRecognizedElement(newEdge);
            sketchesToBeRemoved.add(s);
            recognizedEdges.add(newEdge);
          }
        }
      }
    }

    // Ab hier müsste es wieder für alle gleich sein. 
        
    for(AbstractNode node : recognizedNodes){
      AbstractNodeView nodeView = diagramController.createNodeView(node, false);
      recognizeCompoundCommand.add(new AddDeleteNodeCommand(diagramController, graph, nodeView, node, true));
    }
    for(AbstractEdge edge : recognizedEdges){
      AbstractEdgeView edgeView = diagramController.addEdgeView(edge, false);
      if (edgeView != null) {
        recognizeCompoundCommand.add(new AddDeleteEdgeCommand(diagramController, edgeView, edge, true));
      }
    }
    for(Sketch sketch : sketchesToBeRemoved){
      diagramController.deleteSketch(sketch, recognizeCompoundCommand, false);
    }
    if(recognizeCompoundCommand.size() > 0){
      diagramController.getUndoManager().add(recognizeCompoundCommand);
    }

  }
  
  public List<Sketch> removeDuplicates(List<Sketch> sketches) {
    ArrayList<Sketch> toBeRemovedSketches = new ArrayList();
    for(Sketch s1: sketches) {
      for(Sketch s2: sketches) {
        System.out.println("transX S1 " + s1.getTranslateX());
        System.out.println("transX S2 " + s2.getTranslateX());
        System.out.println("transX S2 " + s2.getId());
        if((s1.getTranslateX() == s2.getTranslateX()) && (s1.getTranslateY() == s2.getTranslateY()) && (s1.getId() != s2.getId()) ) {
          toBeRemovedSketches.add(s2);
        }
      }
    }
    if(toBeRemovedSketches != null) {
      for(Sketch s: toBeRemovedSketches) {
        sketches.remove(s);
      }
    }
    return (List)sketches;
  }

  public synchronized void recognizeMonti(List<Sketch> sketches) {	
    ArrayList<AbstractNode> recognizedNodes = new ArrayList();
    ArrayList<Sketch> sketchesToBeRemoved = new ArrayList<>();
    ArrayList<AbstractEdge> recognizedEdges = new ArrayList<>();
    ArrayList<BoundingBox> boxes = new ArrayList();
    CompoundCommand recognizeCompoundCommand = new CompoundCommand();
    System.out.println("Sketches" + sketches);
    
//    sketches = removeDuplicates(sketches);
    
    
    System.out.println("Sketches" + sketches);
    for (Sketch s : sketches) {
      if (s.getStroke() != null && s.getStroke().getPoints() != null && !s.getStroke().getPoints().isEmpty()) {
        // //TODO This sometimes throws IndexOutOfBoundsException...
        System.out.println("Sketch " + s);
        recognizer.setStroke(s.getStroke());
        Shape bestMatch = recognizer.recognize().getBestShape();
        String bestMatchString = bestMatch.getInterpretation().label;
        if (bestMatchString.equals("Square") || bestMatchString.equals("Rectangle")) {
          BoundingBox box = s.getStroke().getBoundingBox();
          if( !boxes.contains(box)) {
            // just boxes that are not recognized need to be modeled
            boxes.add(box);
            System.out.println("Box " + box );
            sketchMap.put(box, s);    
          }
        }
        else {
          System.out.println("Sketch " + s + " could not be recogniezd as a box");
          // here, we could add some error Messages and a call of coloring the not recognized elements in red
          // here, we first need to check for arrows and if the sketch is not recognized as arrow as well,
          // then we need to do some error message as described above
        }
      }
    }    
    System.out.println("Boex: "+ boxes);
            
 // Now, we recognized the sketches and want to create/modify the model
    for (BoundingBox b: boxes) {
     	if(boxes.size() > 1) {
	      for (BoundingBox bb: boxes) {
	        if (!b.equals(bb) && b.intersects(bb)) {
	        	if(b.getWidth()*b.getHeight() > 2*bb.getWidth()*bb.getHeight()) {
	        				
	        		// bb ist Port von b
	        	  String direction = "";
	        	  PortNode tmpPort = new PortNode();
	        	  double x,y,xDraw,yDraw = 0;
//	        	  PortNode port = new PortNode(bb.getX(), bb.getY(), bb.getHeight(), bb.getWidth());
              System.out.println("Bounding Box of Node has position x " + bb.getX() + " " + bb.getY() + " " + bb.getHeight() + " " + bb.getWidth());
              System.out.println("Bounding Box of Port has position x " + b.getX() + " " + b.getY() + " " + b.getHeight() + " " + b.getWidth());
//	        	  if (bb.getX() + bb.getWidth() < b.getX() + b.getWidth() && bb.getX() < b.getX()) {
	        	  if(Math.abs(b.getX() - bb.getX()) < Math.abs(b.getX() + b.getWidth() - bb.getX()) && (bb.getY() + bb.getHeight() < b.getY() + b.getHeight())) {
              // left Port
	        	    System.out.println("LEFTTTTTTT");
	        	    xDraw = b.getX() - 0.5*tmpPort.getPortWidth();
	        	    x = bb.getX();
//	        	    System.out.println("Port_Width" + port.getPortWidth());
//	        	    System.out.println("x Val of node"+ b.getX());
//	        	    System.out.println("x Val of port" + port.getX());
	        	    yDraw = bb.getY();
	        	    y = bb.getY();
	        	    direction = "in";
	        	  }
//	        	  else if (bb.getY() < b.getY()) {
//	        	    // top
//	        	    System.out.println("We are in top");
//	        	    xDraw = bb.getX();
//	        	    x = (bb.getX());
//	        	    yDraw = (b.getY() - 0.5*tmpPort.getHeight());
//	        	    y = (bb.getY());
//	        	  }
	        	  else if (Math.abs(b.getX() - bb.getX()) > Math.abs(b.getX() + b.getWidth() - bb.getX()) && (bb.getY() + bb.getHeight() < b.getY() + b.getHeight()) ) {
//	            right
	        	    System.out.println("righttttttttt");
	        	    xDraw = (b.getX() + b.getWidth() - 0.5*tmpPort.getPortWidth());
                x = (bb.getX());
                yDraw = (bb.getY());
                y = (bb.getY());
                direction = "out";
  	          }
  	          else {
  //	          bottom
//  	            System.out.println("We are in bottom");
//  	            xDraw = (bb.getX());
//                x = (bb.getX());
//                yDraw = (b.getY() + b.getHeight() - 0.5*tmpPort.getHeight());
//                y = (bb.getY());
  	            System.out.println("Some Ports are not at the right or left");
  	            // Hier Ausgabe auf dem Screen
  	            break;
  	          }
	        	  PortNode port = new PortNode(x,y,bb.getHeight(), bb.getWidth());
	        	  port.setXDraw(xDraw);
	        	  port.setYDraw(yDraw);
	        	  if (!graph.getAllNodes().contains(port)) {
	              graph.addNode(port, false);
	        	  }  
//		       		port.setTitle("testport");
//		       		port.setPortType("Integer");
		        	//System.out.println("Ports: " + ports);
		        	Point2D tmpPoint = new Point2D(b.getX(),b.getY());
		        	if(graph.findNode(tmpPoint) == null) {
		        	  ArrayList<PortNode> ports = new ArrayList<>();
		        	  ports.add(port);
		        	  ComponentNode node = new ComponentNode(b.getX(), b.getY(), b.getHeight(), b.getWidth(), ports);
		        	  for (PortNode p : ports) {
	                p.setComponentNode(node);
	                p.setPortDirection(direction);
	              }
		        	  graph.addNode(node, false); 
		        	  Sketch s1 = sketchMap.get(b);
		        	  Sketch s2 = sketchMap.get(bb);
		        	  s1.setRecognizedElement(node);
		        	  s2.setRecognizedElement(port);
		        	  sketchesToBeRemoved.add(s1);
                sketchesToBeRemoved.add(s2);
		        	  
	              if (!recognizedNodes.contains(node)) {
	                recognizedNodes.add(node);  
	              }
	              if(!recognizedNodes.contains(port)) {
	                recognizedNodes.add(port);
	              }
	              
		        	}
		        	else {
		        	  ((ComponentNode)graph.findNode(tmpPoint)).addPort(port);
		        	  port.setComponentNode((ComponentNode)graph.findNode(tmpPoint));
		        	  if(!recognizedNodes.contains(port)) {
	                recognizedNodes.add(port);
	              }
//		        	  Sketch s1 = sketchMap.get(b);
	              Sketch s2 = sketchMap.get(bb);
	              
//	              s1.setRecognizedElement((ComponentNode)graph.findNode(tmpPoint));
	              s2.setRecognizedElement(port);
	              
//	              sketchesToBeRemoved.add(s1);
	              sketchesToBeRemoved.add(s2);
		        	}
		        	
		       	}
	        }  	
	      }
      } 
     	else {
        ArrayList<PortNode> ports = new ArrayList<>();
        ComponentNode node = new ComponentNode(b.getX(), b.getY(), b.getHeight(), b.getWidth(), ports);
        if (!graph.getAllNodes().contains(node)) {
          graph.addNode(node, false);
        }
        Sketch s = sketchMap.get(b);
        s.setRecognizedElement(node);
        if (!recognizedNodes.contains(node)) {
          recognizedNodes.add(node);  
        }
        sketchesToBeRemoved.add(s);
        //System.out.println("Node" + node);

      }        
    }    
      
    System.out.println("Graph " + graph.getAllNodes().toString());
    for (Sketch s : sketches) {
      if (s.getStroke() != null) {
        recognizer.setStroke(s.getStroke());
        String bestMatchString = recognizer.recognize().getBestShape().getInterpretation().label;
        if (bestMatchString.equals("Line") || bestMatchString.startsWith("Polyline") || bestMatchString.equals("Arc") ||
          bestMatchString.equals("Curve") || bestMatchString.equals("Arrow")){
          Point2D startPoint = new Point2D(s.getStroke().getFirstPoint().getX(), s.getStroke().getFirstPoint().getY());
          Point2D endPoint = new Point2D(s.getStroke().getLastPoint().getX(), s.getStroke().getLastPoint().getY());
          System.out.println("We have found a start and end point" + startPoint + endPoint);
          
          
          
          PortNode startNode = new PortNode(); 
          Node tmpNode = graph.findNode(startPoint);
          if (tmpNode instanceof ComponentNode) {
//            graph.removeNode(tmpNode, false);
            Graph tmpStart = new Graph();
            tmpStart = graph;
            tmpStart.removeNode(tmpNode, false);
            Node secTmpNode = tmpStart.findNode(startPoint);
            startNode = (PortNode) secTmpNode;
//            graph.addNode((AbstractNode) tmpNode, false);
          }
          else {
            startNode = (PortNode) tmpNode; 
          }
          if(startNode.getPortDirection() != "out") {
            System.out.println("You tried to start an edge from an ingoing Port");
            // hier muss dann auch eine Fehlerausgabe hin
            break;  
          }
          
          System.out.println("startNode" + startNode);
//          graph.removeNode(startNode, false);
//          startNode.setPortDirection("in");
//          graph.addNode(startNode, false);
          // nun muessen wir auch im zugehoerigen ComponentNode den Port setzen denke ich
          ComponentNode nodeIn = startNode.getComponentNode();
//          graph.removeNode(nodeIn, false);
//          graph.addNode(nodeIn, false);
          
          
          
          PortNode endNode = new PortNode(); 
          Node tmpOutNode = graph.findNode(endPoint);
          System.out.println("Graph" + graph.getAllNodes());
          if (tmpOutNode instanceof ComponentNode) {
            Graph tmp = new Graph();
            tmp = graph;
            tmp.removeNode(tmpOutNode, false);
            Node secTmpOutNode = tmp.findNode(endPoint);
//            while (secTmpOutNode instanceof ComponentNode) {
//              tmp.removeNode(secTmpOutNode, false);
//              secTmpOutNode = tmp.findNode(endPoint);
//            }
            System.out.println("Hab EndNode gefunden" + secTmpOutNode);
//            graph.get
            endNode = (PortNode) secTmpOutNode;
          }
          else {
            endNode = (PortNode) tmpOutNode; 
          }
          
        //For arrows, which don't have an endpoint
          List<Point> points = s.getStroke().getPoints();
          for (int i = points.size()-1; i > points.size()/2; i--) {
            Point2D point = new Point2D(points.get(i).getX(), points.get(i).getY());
            if (graph.findNode(point) != null) {
              endNode = (PortNode) graph.findNode(point);
              break;
            }
          }
//          graph.removeNode(endNode, false);
          if(endNode.getPortDirection() != "in") {
            System.out.println("You tried to end an edge from an outgoing Port");
            // hier muss dann auch eine Fehlerausgabe hin
            break;  
          }
          //          graph.addNode(endNode, false);
          // nun muessen wir auch im zugehoerigen ComponentNode den Port setzen denke ich
          ComponentNode nodeOut = endNode.getComponentNode();
//          graph.removeNode(nodeOut, false);
//          graph.addNode(nodeOut, false);
          
          
          
          
//          PortNode endPort = new PortNode();
//          PortNode endNode = (PortNode) graph.findNode(endPoint);
//          System.out.println("We are in Arrows");
//          //For arrows, which don't have an endpoint
//          List<Point> points = s.getStroke().getPoints();
//          for (int i = points.size()-1; i > points.size()/2; i--) {
//            Point2D point = new Point2D(points.get(i).getX(), points.get(i).getY());
//            if (graph.findNode(point) != null) {
//              Node endTmp = graph.findNode(point);
//              if(endTmp instanceof ComponentNode) {
//                
//                
//              }
//              else if (endTmp instanceof PortNode) {
//                
//              }
//              for (PortNode p : ((ComponentNode)graph.findNode(point)).getPorts()) {
//                if(p.getBounds().contains(point)) {
//                  p.setPortType("in");
//                  System.out.println("Port recognized as ingoing Port");
//                  endPort = p;
//                  break;  
//                }
//              }
//              endNode = (ComponentNode) graph.findNode(point);
//              System.out.println("Found a end node " + endNode);
//              break;
//            }
//          }

          if (startNode != null && endNode != null && !startNode.equals(endNode)) {
            System.out.println("We are going to create a ConnectorEdge");
            ConnectorEdge newEdge = new ConnectorEdge(nodeOut, nodeIn, startNode, endNode);
            System.out.println("Could draw an Arrow");
            System.out.println("ConnectorEdge looks as follows " + newEdge.getStartNode() + newEdge.getStartPort() + newEdge.getEndNode() + newEdge.getEndPort());
            newEdge.setDirection(AbstractEdge.Direction.START_TO_END);
            s.setRecognizedElement(newEdge);
            sketchesToBeRemoved.add(s);
            recognizedEdges.add(newEdge);
          }
        }
      }
    }
    System.out.println("RecognizedNodes " + recognizedNodes.toString());
    for(AbstractNode node : recognizedNodes){
     	System.out.println("Node for creating a view is " + node.toString());
      AbstractNodeView nodeView = diagramController.createNodeView(node, false);
      System.out.println("NodeView is" + nodeView);
      recognizeCompoundCommand.add(new AddDeleteNodeCommand(diagramController, graph, nodeView, node, true));
      System.out.println("In Loop End");
      
    }
    System.out.println("recognizeCOMPONUNDCOMMAND" +recognizeCompoundCommand.getCommands().toString()); 
    System.out.println("After loop");
    for(AbstractEdge edge : recognizedEdges){
      System.out.println("Create the View");
      AbstractEdgeView edgeView = diagramController.addEdgeView(edge, false);
      if (edgeView != null) {
        recognizeCompoundCommand.add(new AddDeleteEdgeCommand(diagramController, edgeView, edge, true));
      }
    }
    for(Sketch sketch : sketchesToBeRemoved){
      diagramController.deleteSketch(sketch, recognizeCompoundCommand, false);
    }
    if(recognizeCompoundCommand.size() > 0){
      System.out.println("This should be added: " + recognizeCompoundCommand.getCommands());
      diagramController.getUndoManager().add(recognizeCompoundCommand);
    }        
  }
}
    
