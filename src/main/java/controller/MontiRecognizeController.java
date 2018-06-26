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
public class MontiRecognizeController{
  private Pane aDrawPane;
  private MontiArcController diagramController;
  private PaleoSketchRecognizer recognizer;
  private Graph graph;
  HashMap<BoundingBox, Sketch> sketchMap = new HashMap<>();
  

  public MontiRecognizeController(Pane pDrawPane, MontiArcController pController) {
//    super(pDrawPane, pController);
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
    boolean probs = false;
    ArrayList<AbstractNode> recognizedNodes = new ArrayList();
    ArrayList<Sketch> sketchesToBeRemoved = new ArrayList<>();
    ArrayList<AbstractEdge> recognizedEdges = new ArrayList<>();
    ArrayList<BoundingBox> boxes = new ArrayList();
    CompoundCommand recognizeCompoundCommand = new CompoundCommand();
    
    for (Sketch s : sketches) {
      if (s.getStroke() != null && s.getStroke().getPoints() != null && !s.getStroke().getPoints().isEmpty()) {
        recognizer.setStroke(s.getStroke());
        Shape bestMatch = recognizer.recognize().getBestShape();
        String bestMatchString = bestMatch.getInterpretation().label;
        if (bestMatchString.equals("Square") || bestMatchString.equals("Rectangle")) {
          BoundingBox box = s.getStroke().getBoundingBox();
          if( !boxes.contains(box)) {
            boxes.add(box);
            sketchMap.put(box, s);    
          }
        }
        else {
          System.out.println("Sketch " + s + " could not be recogniezd as a box");
          probs = true;
        }
      }
    }    
            
 // Now, we recognized the sketches and want to create/modify the model
    for (BoundingBox b: boxes) {
      if(boxes.size() > 1) {
        for (BoundingBox bb: boxes) {
          if (!b.equals(bb) && b.intersects(bb)) {
            if(b.getWidth()*b.getHeight() > 2*bb.getWidth()*bb.getHeight()) {
                  
              // bb ist Port von b
              String direction = "";
              PortNode tmpPort = new PortNode();
              double x = 0,y = 0,xDraw = 0,yDraw = 0;
              System.out.println("Bounding Box of Node has position x " + bb.getX() + " " + bb.getY() + " " + bb.getHeight() + " " + bb.getWidth());
              System.out.println("Bounding Box of Port has position x " + b.getX() + " " + b.getY() + " " + b.getHeight() + " " + b.getWidth());
              if(Math.abs(b.getX() - bb.getX()) < Math.abs(b.getX() + b.getWidth() - bb.getX()) && (bb.getY() + bb.getHeight() < b.getY() + b.getHeight())) {
              // left Port
                System.out.println("LEFTTTTTTT");
                xDraw = b.getX() - 0.5*tmpPort.getPortWidth();
                x = bb.getX();
                yDraw = bb.getY();
                y = bb.getY();
                direction = "in";
              }
              else if (Math.abs(b.getX() - bb.getX()) > Math.abs(b.getX() + b.getWidth() - bb.getX()) && (bb.getY() + bb.getHeight() < b.getY() + b.getHeight()) ) {
//              right
                System.out.println("righttttttttt");
                xDraw = (b.getX() + b.getWidth() - 0.5*tmpPort.getPortWidth());
                x = (bb.getX());
                yDraw = (bb.getY());
                y = (bb.getY());
                direction = "out";
              }
//              else {
//                System.out.println("Some Ports are not at the right or left");
//                break;
//              }
              PortNode port = new PortNode(xDraw,yDraw,bb.getHeight(), bb.getWidth());
              // contains the sketch values
              port.createPortNodeSketch(x, y, bb.getHeight(), bb.getHeight());
//              port.setXDraw(xDraw);
//              port.setYDraw(yDraw);
              if (!graph.getAllNodes().contains(port)) {
                graph.addNode(port, false);
              }  
              Point2D tmpPoint = new Point2D(b.getX(),b.getY());
              if(findNode(graph,tmpPoint) == null) {
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
                ((ComponentNode)findNode(graph, tmpPoint)).addPort(port);
                port.setComponentNode((ComponentNode)findNode(graph,tmpPoint));
                if(!recognizedNodes.contains(port)) {
                  recognizedNodes.add(port);
                }

                Sketch s2 = sketchMap.get(bb);
                s2.setRecognizedElement(port);
                sketchesToBeRemoved.add(s2);
              }
              
            }
          }   
        }
      } 
//      else {
//        ArrayList<PortNode> ports = new ArrayList<>();
//        ComponentNode node = new ComponentNode(b.getX(), b.getY(), b.getHeight(), b.getWidth(), ports);
//        if (!graph.getAllNodes().contains(node)) {
//          graph.addNode(node, false);
//        }
//        Sketch s = sketchMap.get(b);
//        s.setRecognizedElement(node);
//        if (!recognizedNodes.contains(node)) {
//          recognizedNodes.add(node);  
//        }
//        sketchesToBeRemoved.add(s);
//      }        
    }    
    if (probs == false) { 
//    recognize edges
      for (Sketch s : sketches) {
        if (s.getStroke() != null) {
          recognizer.setStroke(s.getStroke());
          boolean found = false;
          String bestMatchString = recognizer.recognize().getBestShape().getInterpretation().label;
          if (bestMatchString.equals("Line") || bestMatchString.startsWith("Polyline") || bestMatchString.equals("Arc") ||
            bestMatchString.equals("Curve") || bestMatchString.equals("Arrow")) {
            Point2D startPoint = new Point2D(s.getStroke().getFirstPoint().getX(), s.getStroke().getFirstPoint().getY());
            Point2D endPoint = new Point2D(s.getStroke().getLastPoint().getX(), s.getStroke().getLastPoint().getY());
            System.out.println("endpoint " + endPoint);
            System.out.println("Graph looks as follows " + graph.getAllGraphElements().toString());
            PortNode endNode = new PortNode();
            for (AbstractNode n : graph.getAllNodes()) {
              if (n instanceof PortNode) {
                System.out.println("PortNodeSketch Pos " + ((PortNode)n).getPortNodeSketch().getX()
                    + ((PortNode)n).getPortNodeSketch().getY()
                    + ((PortNode)n).getPortNodeSketch().getWidth()
                    + ((PortNode)n).getPortNodeSketch().getHeight());
                if(((PortNode) n).getPortNodeSketch().getX() + ((PortNode) n).getPortNodeSketch().getWidth() + 50 > endPoint.getX() 
                    && ((PortNode) n).getPortNodeSketch().getX() < endPoint.getX() 
                    && ((PortNode) n).getPortNodeSketch().getY() < endPoint.getY() 
                    && ((PortNode) n).getPortNodeSketch().getY() + ((PortNode) n).getPortNodeSketch().getHeight() > endPoint.getY()) {
                  System.out.println("One Port is found as endPort");
                  endNode = (PortNode) n;
                }
              }
            }
            if (Math.abs(startPoint.getX() - endPoint.getX()) < 20) {
              sketchesToBeRemoved.add(s);
            }
            else {
              PortNode startNode = new PortNode(); 
              
              Node tmpNode = findNode(graph,startPoint);
              System.out.println("Potential startNode " + tmpNode);
              if (tmpNode instanceof ComponentNode) {
                Graph tmpStart = new Graph();
                tmpStart = graph;
                tmpStart.removeNode(tmpNode, false);
                Node secTmpNode = findNode(tmpStart, startPoint);
                startNode = (PortNode) secTmpNode;
                System.out.println("StartNode found was CompNode and now we select corr. port " + startNode);
              }
              else if (tmpNode instanceof PortNode){
                startNode = (PortNode) tmpNode;
                System.out.println("StartNode is portNode");
              }
              else {
                System.out.println("Havent found a startnode yet");
                // if we do not find a startpoint, to the left needs to be one
                for (AbstractNode n : graph.getAllNodes()) {
                  if (n instanceof PortNode) {
  //                  This should be the correct version
                    if(((PortNode) n).getPortNodeSketch().getX() + ((PortNode) n).getPortNodeSketch().getWidth() + 50 > startPoint.getX() 
                        && ((PortNode) n).getPortNodeSketch().getX() < startPoint.getX() 
                        && ((PortNode) n).getPortNodeSketch().getY() < startPoint.getY() 
                        && ((PortNode) n).getPortNodeSketch().getY() + ((PortNode) n).getPortNodeSketch().getHeight() > startPoint.getY()) {
                    
  //                  if(((PortNode) n).getX() + ((PortNode) n).getPortHeight() + 50 > startPoint.getX() 
  //                      && ((PortNode) n).getX() < startPoint.getX() 
  //                      && ((PortNode) n).getY() < startPoint.getY() 
  //                      && ((PortNode) n).getY() + ((PortNode) n).getPortWidth() > startPoint.getY()) {
  //                    startNode = (PortNode) n;
  //                  }
                      startNode = (PortNode) n;
                      found = true;
                    }
                  }
                }
              }
              if(startNode.getPortDirection() != "out") {
                System.out.println("You tried to start an edge from an ingoing Port");
              }
              
              ComponentNode nodeIn = startNode.getComponentNode();
    //            PortNode endNode = new PortNode(); 
              if (found == false) {
                Node tmpOutNode = findNode(graph,endPoint);
                System.out.println("TmpOutNode " + tmpOutNode);
                if (tmpOutNode instanceof ComponentNode) {
                  Graph tmp = graph;
                  tmp.removeNode(tmpOutNode, false);
                  Node secTmpOutNode = findNode(tmp,endPoint);
                  endNode = (PortNode) secTmpOutNode;
                }
                else {
                  endNode = (PortNode) tmpOutNode; 
                }
                
              //For arrows, which don't have an endpoint
                List<Point> points = s.getStroke().getPoints();
                for (int i = points.size()-1; i > points.size()/2; i--) {
                  Point2D point = new Point2D(points.get(i).getX(), points.get(i).getY());
                  if (findNode(graph, point) != null) {
                    endNode = (PortNode)findNode(graph, point);
                    break;
                  }
                }
                System.out.println("EndNode " + endNode);
                if(endNode.getPortDirection() != "in") {
                  System.out.println("You tried to end an edge from an outgoing Port");
                  break;  
                }
              }
              if (startNode != null && endNode != null && !startNode.equals(endNode)) {
                ConnectorEdge newEdge = new ConnectorEdge(startNode, endNode);
                newEdge.setDirection(AbstractEdge.Direction.START_TO_END);
                s.setRecognizedElement(newEdge);
                sketchesToBeRemoved.add(s);
                recognizedEdges.add(newEdge);
              }
            }
          }
        }
      }
    }
    
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
    
    for (Sketch s : sketches) {
      if(!(sketchesToBeRemoved.contains(s))) {
        diagramController.deleteSketch(s, recognizeCompoundCommand, false);
      }
    }
    
    if(recognizeCompoundCommand.size() > 0){
      diagramController.getUndoManager().add(recognizeCompoundCommand);
    }        
  }
  
  public Node findNode(Graph g, Point2D point) {
    for (AbstractNode node : g.getAllNodes()) {
      if (node instanceof ComponentNode) {
        return g.findNode(point);
      }
      else if (node instanceof PortNode) {
        if (((PortNode) node).getPortNodeSketch().getBounds().contains(point.getX(),point.getY())) {
          return node;
        }
      }
    }
    return null;
  }
}
    
