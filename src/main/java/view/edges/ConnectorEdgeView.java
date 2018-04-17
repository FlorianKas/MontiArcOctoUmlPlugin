package view.edges;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;

import model.nodes.PortNode;

import util.Constants;

import view.nodes.AbstractNodeView;

public class ConnectorEdgeView extends AbstractEdgeView{
  private AbstractEdge refEdge;
  private AbstractNodeView startNode;
  private AbstractNodeView endNode;
  private ArrayList<Line> arrowHeadLines = new ArrayList<>();
  
  public ConnectorEdgeView(AbstractEdge edge, AbstractNodeView startNode, AbstractNodeView endNode) {
    super(edge, startNode, endNode);
    System.out.println("Here we are");
    this.refEdge = edge;
    this.startNode = startNode;
    this.endNode = endNode;
    this.setStrokeWidth(super.STROKE_WIDTH);
    this.setStroke(Color.BLACK);
    setPosition();
    draw();
  }
  protected void draw() {
    System.out.println("Now in draw");
    AbstractEdge.Direction direction = refEdge.getDirection();
    PortNode start = ((ConnectorEdge) this.refEdge).getStartPort();
    PortNode ende = ((ConnectorEdge) this.refEdge).getEndPort();
//    if (start.getX() )
//    getStartLine().setStartX(this.startNode.getX() + this.startNode.getWidth() + 1/2*start.getDefaultPortWidth());  
//    getStartLine().setStartY(start.getY() + 1/2*start.getDefaultPortHeight());
//    getEndLine().setEndX(this.endNode.getX() + 1/2*ende.getDefaultPortWidth());
//    getEndLine().setEndY(ende.getY() + 1/2*ende.getDefaultPortHeight());
//    getStartLine().setStartX(getStartLine().getStartX() + start.getDefaultPortWidth());
    getStartLine().setStartX(startNode.getX() + start.getDefaultPortWidth());
//    getStartLine().setStartY(getStartLine().getStartY() - start.getDefaultPortHeight());
    getStartLine().setStartY(start.getY() + 1/2*start.getDefaultPortHeight());
//    getEndLine().setEndY(getEndLine().getEndY() - ende.getDefaultPortHeight());
//    getStartLine().setEndX(getEndLine().getEndX());
    getStartLine().setEndX(endNode.getX() - 1/2*ende.getDefaultPortWidth());
//    getStartLine().setEndY(getEndLine().getEndY());
    getStartLine().setEndY(ende.getY() + 1/2*ende.getDefaultPortHeight());
    getChildren().clear();
    getChildren().add(getStartLine());
//    getChildren().add(getMiddleLine());
//    getChildren().add(getEndLine());
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
    
    this.getChildren().add(drawArrowHead(getStartLine().getEndX(), getStartLine().getEndY(), getStartLine().getStartX(), getStartLine().getStartY()));
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
}
