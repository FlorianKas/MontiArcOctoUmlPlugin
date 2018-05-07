package view.edges;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;

import model.nodes.PortNode;

import util.Constants;
import view.edges.AbstractEdgeView.Position;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PortNodeView;

public class ConnectorEdgeView extends AbstractEdgeView{
  private AbstractEdge refEdge;
  private AbstractNodeView startNode;
  private AbstractNodeView endNode;
  private ArrayList<Line> arrowHeadLines = new ArrayList<>();
  private Text dataType = new Text();
  
  public ConnectorEdgeView(AbstractEdge edge, AbstractNodeView startNode, AbstractNodeView endNode) {
    super(edge, startNode, endNode);
    startNode.setHeight(((PortNode)edge.getStartNode()).getPortHeight());
    startNode.setWidth(((PortNode)edge.getStartNode()).getPortWidth());
    endNode.setHeight(((PortNode)edge.getEndNode()).getPortHeight());
    endNode.setWidth(((PortNode)edge.getEndNode()).getPortWidth());
    this.refEdge = edge;
    this.startNode = startNode;
    this.endNode = endNode;
    this.setStrokeWidth(super.STROKE_WIDTH);
    this.setStroke(Color.BLACK);
    PortNode start = ((ConnectorEdge) this.refEdge).getStartPort();
    PortNode ende = ((ConnectorEdge) this.refEdge).getEndPort();
    setPosition(start, ende);
    draw();
    this.getChildren().add(dataType);
  }
  protected void draw() {
    System.out.println("Now in draw");
    AbstractEdge.Direction direction = refEdge.getDirection();
    PortNode start = ((ConnectorEdge) this.refEdge).getStartPort();
    PortNode ende = ((ConnectorEdge) this.refEdge).getEndPort();
    System.out.println("xDraw" + start.getXDraw());
    System.out.println("x" + start.getX());
//    if (start.getX() )
//    getStartLine().setStartX(start.getXDraw() + start.getPortWidth() + 0.5*start.getPortWidth());  
//    getStartLine().setStartY(start.getYDraw() + 0.5*start.getPortHeight());
//    getEndLine().setEndX(this.endNode.getX() + 1/2*ende.getPortWidth());
//    getEndLine().setEndY(ende.getY() + 1/2*ende.getPortHeight());
//    getStartLine().setStartX(getStartLine().getStartX() + start.getPortWidth());
//    getStartLine().setStartX(startNode.getX() + start.getPortWidth());
//    getStartLine().setStartY(getStartLine().getStartY() - start.getPortHeight());
//    getStartLine().setStartY(start.getY() + 1/2*start.getPortHeight());
//    getEndLine().setEndY(getEndLine().getEndY() - ende.getPortHeight());
//    getStartLine().setEndX(getEndLine().getEndX());
//    getStartLine().setEndX(ende.getXDraw() + 0.5*ende.getPortWidth());
//    getStartLine().setEndY(getEndLine().getEndY());
//    getStartLine().setEndY(ende.getYDraw() + 0.5*ende.getPortHeight());
    getChildren().clear();
    getChildren().add(getStartLine());
    getChildren().add(getMiddleLine());
    getChildren().add(getEndLine());
    super.draw();
    this.getChildren().add(super.getEndMultiplicity());
    this.getChildren().add(super.getStartMultiplicity());
//    System.out.println("Endline End x " + getEndLine().getEndX());
//    System.out.println("Endline End y " + getEndLine().getEndY());
//    System.out.println("Endline Start x " + getEndLine().getStartX());
//    System.out.println("Endline Start y " + getEndLine().getStartY());
    System.out.println("Startline End x " + getStartLine().getEndX());
    System.out.println("Startline End y " + getStartLine().getEndY());
    System.out.println("Startline Start x " + getStartLine().getStartX());
    System.out.println("Startline Start y " + getStartLine().getStartY());
//    System.out.println("MiddleLine End x " + getMiddleLine().getEndX());
//    System.out.println("MiddleLine End y " + getMiddleLine().getEndY());
//    System.out.println("MiddleLine Start x " + getMiddleLine().getStartX());
//    System.out.println("MiddleLine Start y " + getMiddleLine().getStartY());
    
//    this.getChildren().add(drawArrowHead(getStartLine().getEndX(), getStartLine().getEndY(), getStartLine().getStartX(), getStartLine().getStartY()));
//    System.out.println("Going to draw an edge");
//    
//    AbstractEdge.Direction direction = refEdge.getDirection();
//    getChildren().clear();
//    System.out.println("StartLine " + getStartLine());
//    System.out.println("MiddleLine " + getMiddleLine());
//    System.out.println("EndLine " + getEndLine());
//    getChildren().add(getStartLine());
//    getChildren().add(getMiddleLine());
//    getChildren().add(getEndLine());
//    System.out.println("Before drAaw");
//    super.draw();
//    System.out.println("After drwa");
//    this.getChildren().add(super.getEndMultiplicity());
//    this.getChildren().add(super.getStartMultiplicity());
    this.getChildren().add(drawArrowHead(getEndLine().getEndX(), getEndLine().getEndY(), getEndLine().getStartX(), getEndLine().getStartY()));
    
    
  }
  
  public void setSelected(boolean selected) {
    super.setSelected(selected);
    if (selected) {
      for (Line l : arrowHeadLines) {
        l.setStroke(Constants.selected_color);
      }
    }
    else {
      for (Line l : arrowHeadLines) {
        l.setStroke(Color.BLACK);
      }
    }
  }
  
  /**
   * Draws an ArrowHead and returns it in a group. Based on code from
   * http://www.coderanch.com/t/340443/GUI/java/Draw-arrow-head-line
   * 
   * @param startX
   * @param startY
   * @param endX
   * @param endY
   * @return Group.
   */
  private Group drawArrowHead(double startX, double startY, double endX, double endY) {
    Group group = new Group();
    double phi = Math.toRadians(40);
    int barb = 20;
    double dy = startY - endY;
    double dx = startX - endX;
    double theta = Math.atan2(dy, dx);
    double x, y, rho = theta + phi;
    
    for (int j = 0; j < 2; j++) {
      x = startX - barb * Math.cos(rho);
      y = startY - barb * Math.sin(rho);
      Line arrowHeadLine = new Line(startX, startY, x, y);
      arrowHeadLine.setStrokeWidth(super.STROKE_WIDTH);
      arrowHeadLines.add(arrowHeadLine);
      if (super.isSelected()) {
        arrowHeadLine.setStroke(Constants.selected_color);
      }
      group.getChildren().add(arrowHeadLine);
      rho = theta - phi;
    }
    return group;
  }
  
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
    if (evt.getPropertyName().equals(Constants.changeNodeTranslateX) || evt.getPropertyName().equals(Constants.changeNodeTranslateY) || evt.getPropertyName().equals(Constants.changeEdgeDirection)) {
      draw();
    }
  } 
  
  
  protected void setPosition(PortNode start, PortNode ende) {
    System.out.println("StrartNode X " + startNode.getTranslateX());
    System.out.println("StrartNode Width " + startNode.getWidth());
    // If end node is to the right of startNode:
    if (start.getXDraw() + start.getWidth() <= ende.getXDraw()) { // Straight
                                                                                       // line
                                                                                       // if
                                                                                       // height
                                                                                       // difference
                                                                                       // is
                                                                                       // small
      if (Math.abs(start.getXDraw() + (start.getPortHeight() / 2) - (ende.getYDraw() + (ende.getPortHeight() / 2))) < 0) {
        startLine.setStartX(start.getXDraw() + start.getPortWidth() + 0.5*start.getPortWidth());
        startLine.setStartY(start.getYDraw() + (start.getPortHeight() / 2));
        startLine.setEndX(ende.getXDraw() + 0.5*ende.getPortWidth());
        startLine.setEndY(ende.getYDraw() + (ende.getPortHeight() / 2));
        
        middleLine.setStartX(0);
        middleLine.setStartY(0);
        middleLine.setEndX(0);
        middleLine.setEndY(0);
        
        endLine.setStartX(0);
        endLine.setStartY(0);
        endLine.setEndX(0);
        endLine.setEndY(0);
      }
      else {
        startLine.setStartX(start.getXDraw() + start.getPortWidth() + 0.5*start.getPortWidth());
        startLine.setStartY(start.getYDraw() + (start.getPortHeight() / 2));
        startLine.setEndX(start.getXDraw() + start.getPortWidth() + ((ende.getXDraw() - (start.getXDraw() + start.getPortWidth())) / 2));
        startLine.setEndY(start.getYDraw() + (start.getPortHeight() / 2));
        
        middleLine.setStartX(start.getXDraw() + start.getPortWidth() + ((ende.getXDraw() - (start.getXDraw() + start.getPortWidth())) / 2));
        middleLine.setStartY(start.getYDraw() + (start.getPortHeight() / 2));
        middleLine.setEndX(start.getXDraw() + start.getPortWidth() + ((ende.getXDraw() - (start.getXDraw() + start.getPortWidth())) / 2));
        middleLine.setEndY(ende.getYDraw() + (ende.getPortHeight() / 2));
        
        endLine.setStartX(start.getXDraw() + start.getPortWidth() + ((ende.getXDraw() - (start.getXDraw() + start.getPortWidth())) / 2));
        endLine.setStartY(ende.getYDraw() + (ende.getPortHeight() / 2));
        endLine.setEndX(ende.getXDraw() + 0.5*ende.getPortWidth());
        endLine.setEndY(ende.getYDraw() + (ende.getPortHeight() / 2));
      }
      
      position = Position.RIGHT;
    }
    // If end node is to the left of startNode:
    else if (start.getXDraw() > ende.getXDraw() + ende.getPortWidth()) {
      if (Math.abs(start.getYDraw() + (start.getPortHeight() / 2) - (ende.getYDraw() + (ende.getPortHeight() / 2))) < 0) {
        startLine.setStartX(start.getXDraw() - 0.5*start.getPortWidth());
        startLine.setStartY(start.getYDraw() + (start.getPortHeight() / 2));
        startLine.setEndX(ende.getXDraw() + ende.getPortWidth() + 0.5*ende.getPortWidth());
        startLine.setEndY(ende.getYDraw() + (ende.getPortHeight() / 2));
        
        middleLine.setStartX(0);
        middleLine.setStartY(0);
        middleLine.setEndX(0);
        middleLine.setEndY(0);
        
        endLine.setStartX(0);
        endLine.setStartY(0);
        endLine.setEndX(0);
        endLine.setEndY(0);
      }
      else {
        startLine.setStartX(start.getXDraw() + 0.5*start.getPortWidth());
        startLine.setStartY(start.getYDraw() + (start.getPortHeight() / 2));
        startLine.setEndX(ende.getXDraw() + ende.getPortWidth() + ((start.getXDraw() - (ende.getXDraw() + ende.getPortWidth())) / 2));
        startLine.setEndY(start.getYDraw() + (start.getPortHeight() / 2));
        
        middleLine.setStartX(ende.getXDraw() + ende.getPortWidth() + ((start.getXDraw() - (ende.getXDraw() + ende.getPortWidth())) / 2));
        middleLine.setStartY(start.getYDraw() + (start.getPortHeight() / 2));
        middleLine.setEndX(ende.getXDraw() + ende.getPortWidth() + ((start.getXDraw() - (ende.getXDraw() + ende.getPortWidth())) / 2));
        middleLine.setEndY(ende.getYDraw() + (ende.getPortHeight() / 2));
        
        endLine.setStartX(ende.getXDraw() + ende.getPortWidth() + ((start.getXDraw() - (ende.getXDraw() + ende.getPortWidth())) / 2));
        endLine.setStartY(ende.getYDraw() + (ende.getPortHeight() / 2));
        endLine.setEndX(ende.getXDraw() + ende.getPortWidth() + 0.5*ende.getPortWidth());
        endLine.setEndY(ende.getYDraw() + (ende.getPortHeight() / 2));
      }
      
      position = Position.LEFT;
    }
    // If end node is below startNode:
    else if (start.getYDraw() + start.getPortHeight() < ende.getYDraw()) {
      if (Math.abs(start.getXDraw() + (start.getPortWidth() / 2) - (ende.getXDraw() + (ende.getPortWidth() / 2))) < 0) {
        startLine.setStartX(start.getXDraw() + (start.getPortWidth() / 2));
        startLine.setStartY(start.getYDraw() + start.getPortHeight());
        startLine.setEndX(ende.getXDraw() + (ende.getPortWidth() / 2));
        startLine.setEndY(ende.getYDraw());
        
        middleLine.setStartX(0);
        middleLine.setStartY(0);
        middleLine.setEndX(0);
        middleLine.setEndY(0);
        
        endLine.setStartX(0);
        endLine.setStartY(0);
        endLine.setEndX(0);
        endLine.setEndY(0);
      }
      else {
        startLine.setStartX(start.getXDraw() + (start.getPortWidth() / 2));
        startLine.setStartY(start.getYDraw() + start.getPortHeight());
        startLine.setEndX(start.getXDraw() + (start.getPortWidth() / 2));
        startLine.setEndY(start.getYDraw() + start.getPortHeight() + ((ende.getYDraw() - (start.getYDraw() + start.getPortHeight())) / 2));
        
        middleLine.setStartX(start.getXDraw() + (start.getPortWidth() / 2));
        middleLine.setStartY(start.getYDraw() + start.getPortHeight() + ((ende.getYDraw() - (start.getYDraw() + start.getPortHeight())) / 2));
        middleLine.setEndX(ende.getXDraw() + (ende.getPortWidth() / 2));
        middleLine.setEndY(start.getYDraw() + start.getPortHeight() + ((ende.getYDraw() - (start.getYDraw() + start.getPortHeight())) / 2));
        
        endLine.setStartX(ende.getXDraw() + (ende.getPortWidth() / 2));
        endLine.setStartY(start.getYDraw() + start.getPortHeight() + ((ende.getYDraw() - (start.getYDraw() + start.getPortHeight())) / 2));
        endLine.setEndX(ende.getXDraw() + (ende.getPortWidth() / 2));
        endLine.setEndY(ende.getYDraw());
      }
      
      position = Position.BELOW;
    }
    // If end node is above startNode:
    else if (start.getYDraw() >= ende.getYDraw() + ende.getPortHeight()) {
      if (Math.abs(start.getXDraw() + (start.getPortWidth() / 2) - (ende.getXDraw() + (ende.getPortWidth() / 2))) < 0) {
        startLine.setStartX(start.getXDraw() + (start.getPortWidth() / 2));
        startLine.setStartY(start.getYDraw());
        startLine.setEndX(ende.getXDraw() + (ende.getPortWidth() / 2));
        startLine.setEndY(ende.getYDraw() + ende.getPortHeight());
        
        middleLine.setStartX(0);
        middleLine.setStartY(0);
        middleLine.setEndX(0);
        middleLine.setEndY(0);
        
        endLine.setStartX(0);
        endLine.setStartY(0);
        endLine.setEndX(0);
        endLine.setEndY(0);
      }
      else {
        startLine.setStartX(start.getXDraw() + (start.getPortWidth() / 2));
        startLine.setStartY(start.getYDraw());
        startLine.setEndX(start.getXDraw() + (start.getPortWidth() / 2));
        startLine.setEndY(ende.getYDraw() + ende.getPortHeight() + ((start.getYDraw() - (ende.getYDraw() + ende.getPortHeight())) / 2));
        
        middleLine.setStartX(start.getXDraw() + (start.getPortWidth() / 2));
        middleLine.setStartY(ende.getYDraw() + ende.getPortHeight() + ((start.getYDraw() - (ende.getYDraw() + ende.getPortHeight())) / 2));
        middleLine.setEndX(ende.getXDraw() + (ende.getPortWidth() / 2));
        middleLine.setEndY(ende.getYDraw() + ende.getPortHeight() + ((start.getYDraw() - (ende.getYDraw() + ende.getPortHeight())) / 2));
        
        endLine.setStartX(ende.getXDraw() + (ende.getPortWidth() / 2));
        endLine.setStartY(ende.getYDraw() + ende.getPortHeight() + ((start.getYDraw() - (ende.getYDraw() + ende.getPortHeight())) / 2));
        endLine.setEndX(ende.getXDraw() + (ende.getPortWidth() / 2));
        endLine.setEndY(ende.getYDraw() + ende.getPortHeight());
      }
      
      position = Position.ABOVE;
    }
    // TODO Handle when the nodes are overlapping.
  }
  
}
