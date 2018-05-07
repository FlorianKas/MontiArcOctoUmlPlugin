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
import util.commands.CompoundCommand;
import view.edges.AbstractEdgeView;
import view.edges.ConnectorEdgeView;
import view.nodes.AbstractNodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used by MainController for handling the recognition of drawn shapes and transforming them in to UML-notations.
 */
public class MontiRecognizeController {
  private Pane aDrawPane;
  private MontiArcController diagramController;
  private PaleoSketchRecognizer recognizer;
  private Graph graph;
  HashMap<BoundingBox, Sketch> sketchMap = new HashMap<>();
  

  public MontiRecognizeController(Pane pDrawPane, MontiArcController pController) {
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

    public synchronized void recognizeMonti(List<Sketch> sketches) {	
    ArrayList<AbstractNode> recognizedNodes = new ArrayList();
    ArrayList<Sketch> sketchesToBeRemoved = new ArrayList<>();
    ArrayList<ConnectorEdge> recognizedEdges = new ArrayList<>();
    ArrayList<BoundingBox> boxes = new ArrayList();
    CompoundCommand recognizeCompoundCommand = new CompoundCommand();
        
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
	        if (!b.equals(bb) && b.intersects(bb)){
	        	if(b.getWidth()*b.getHeight() > 2*bb.getWidth()*bb.getHeight()) {
	        				
	        		// bb ist Port von b
	        	  ArrayList<PortNode> ports = new ArrayList<>();
		        	PortNode port = new PortNode(bb.getX(), bb.getY(), bb.getHeight(), bb.getWidth());
		        	graph.addNode(port, false);
//		       		port.setTitle("testport");
//		       		port.setPortType("Integer");
		        	ports.add(port);
		       		//System.out.println("Ports: " + ports);
		       		ComponentNode node = new ComponentNode(b.getX(), b.getY(), b.getHeight(), b.getWidth(), ports);
		       		for (PortNode p : ports) {
		       		  p.setComponentNode(node);
		       		}
//		       		node.setTitle("Test");
		       		//System.out.println("Node: " + node);
		       		graph.addNode(node, false);
		       		Sketch s1 = sketchMap.get(b);
		       		Sketch s2 = sketchMap.get(bb);
		       		sketchesToBeRemoved.add(s1);
		       		sketchesToBeRemoved.add(s2);
		       		s1.setRecognizedElement(node);
		       		s2.setRecognizedElement(port);
	        		recognizedNodes.add(node);
              recognizedNodes.add(port);
		       	}
	        }  	
	      }
      } 
     	else {
        ArrayList<PortNode> ports = new ArrayList<>();
        ComponentNode node = new ComponentNode(b.getX(), b.getY(), b.getHeight(), b.getWidth(), ports);
        graph.addNode(node, false);
        Sketch s = sketchMap.get(b);
        s.setRecognizedElement(node);
        recognizedNodes.add(node);
        sketchesToBeRemoved.add(s);
        //System.out.println("Node" + node);

      }        
    }    
      
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
            Node secTmpNode = graph.findNode(startPoint);
            startNode = (PortNode) secTmpNode;
//            graph.addNode((AbstractNode) tmpNode, false);
          }
          else {
            startNode = (PortNode) tmpNode; 
          }
//          graph.removeNode(startNode, false);
          startNode.setPortDirection("in");
//          graph.addNode(startNode, false);
          // nun muessen wir auch im zugehoerigen ComponentNode den Port setzen denke ich
          ComponentNode nodeIn = startNode.getComponentNode();
//          graph.removeNode(nodeIn, false);
          for (PortNode p : nodeIn.getPorts()) {
            if (p == startNode) {
              p.setPortDirection("in");
              break;
            }
          }
//          graph.addNode(nodeIn, false);
          
          
          
          PortNode endNode = new PortNode(); 
          Node tmpOutNode = graph.findNode(endPoint);
          if (tmpOutNode instanceof ComponentNode) {
            Graph tmp = new Graph();
            tmp = graph;
            tmp.removeNode(tmpOutNode, false);
            Node secTmpOutNode = tmp.findNode(endPoint);
//            graph.get
            endNode = (PortNode) secTmpOutNode;
          }
          else {
            endNode = (PortNode) tmpOutNode; 
          }
//          graph.removeNode(endNode, false);
          endNode.setPortDirection("out");
//          graph.addNode(endNode, false);
          // nun muessen wir auch im zugehoerigen ComponentNode den Port setzen denke ich
          ComponentNode nodeOut = endNode.getComponentNode();
//          graph.removeNode(nodeOut, false);
          for (PortNode p : nodeOut.getPorts()) {
            if (p == endNode) {
              p.setPortDirection("out");
              break;
            }
          }
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
    
    for(AbstractNode node : recognizedNodes){
     	//System.out.println(node);
      AbstractNodeView nodeView = diagramController.createNodeView(node, false);
      //System.out.println(nodeView);
      recognizeCompoundCommand.add(new AddDeleteNodeCommand(diagramController, graph, nodeView, node, true));
    }
    for(ConnectorEdge edge : recognizedEdges){
      System.out.println("Create the View");
      ConnectorEdgeView edgeView = diagramController.addEdgeView(edge, false);
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
}
    