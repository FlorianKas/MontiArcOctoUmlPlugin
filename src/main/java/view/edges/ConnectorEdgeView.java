package view.edges;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import model.edges.AbstractEdge;
import model.edges.ConnectorEdge;

import model.nodes.PortNode;
import util.Constants;
import util.ConstantsMonti;
import view.edges.AbstractEdgeView.Position;
import view.nodes.AbstractNodeView;
import view.nodes.ComponentNodeView;
import view.nodes.PortNodeView;

public class ConnectorEdgeView extends AbstractEdgeView{
  private ConnectorEdge refEdge;
  private PortNodeView startNode;
  private PortNodeView endNode;
  private ArrayList<Line> arrowHeadLines = new ArrayList<>();
  
  private Text dataType = new Text();
  private Label StereoType = new Label();
  
  
  public ConnectorEdgeView(ConnectorEdge edge, PortNodeView startNode, PortNodeView endNode) {
    super(edge, startNode, endNode);
    this.refEdge = edge;
    this.startNode = startNode;
    this.endNode = endNode;
    System.out.println("refEdge " + edge.getStartPort().getX());
    System.out.println("startNode " + startNode.getX());
    this.setStrokeWidth(super.STROKE_WIDTH);
    this.setStroke(Color.BLACK);
    setPosition();
    draw();

    
    
    
    
    
//    super(edge, startNode, endNode);
//    startNode.setHeight(((PortNode)edge.getStartNode()).getPortHeight());
//    startNode.setWidth(((PortNode)edge.getStartNode()).getPortWidth());
//    endNode.setHeight(((PortNode)edge.getEndNode()).getPortHeight());
//    endNode.setWidth(((PortNode)edge.getEndNode()).getPortWidth());
//    ((PortNode)edge.getStartNode()).getX();
//    this.refEdge = edge;
//    this.startNode = startNode;
//    this.endNode = endNode;
//    this.setStrokeWidth(super.STROKE_WIDTH);
//    this.setStroke(Color.BLACK);
//    start = ((ConnectorEdge) this.refEdge).getStartPort();
//    ende = ((ConnectorEdge) this.refEdge).getEndPort();
//    setPosition();
//    draw();
//    this.getChildren().add(dataType);
//    
//    refEdge.addPropertyChangeListener(this);
//    if(startNode != null){
//        startNode.getRefNode().addPropertyChangeListener(this);
//    }
//    endNode.getRefNode().addPropertyChangeListener(this);
  }
  
//  public ConnectorEdge getRefEdge() {
//    return this.refEdge;
//  }
  protected void draw() {
//    System.out.println("Now in draw");
//    AbstractEdge.Direction direction = refEdge.getDirection();
//    PortNode start = ((ConnectorEdge) this.refEdge).getStartPort();
//    PortNode ende = ((ConnectorEdge) this.refEdge).getEndPort();
////    System.out.println("xDraw" + start.getXDraw());
//    System.out.println("x" + start.getX());
//    getChildren().clear();
//    getChildren().add(getStartLine());
//    getChildren().add(getMiddleLine());
//    getChildren().add(getEndLine());
//    super.draw();
//    this.getChildren().add(super.getEndMultiplicity());
//    this.getChildren().add(super.getStartMultiplicity());
//    System.out.println("Startline End x " + getStartLine().getEndX());
//    System.out.println("Startline End y " + getStartLine().getEndY());
//    System.out.println("Startline Start x " + getStartLine().getStartX());
//    System.out.println("Startline Start y " + getStartLine().getStartY());
//    this.getChildren().add(drawArrowHead(getEndLine().getEndX(), getEndLine().getEndY(), getEndLine().getStartX(), getEndLine().getStartY()));
//    if(((ConnectorEdge)refEdge).getStereoType() != null) {
//      StereoType.setText(((ConnectorEdge)refEdge).getStereoType());
//    }
//    
//    StereoType.setLayoutX((startNode.getX() + endNode.getX()) / 2);
//    StereoType.setLayoutY(middleLine.getEndY() + 20);
//    StereoType.toFront();
//    this.getChildren().add(StereoType);
    getChildren().clear();
    System.out.println("StartNode Translate X " + startNode.getTranslateX() );
    System.out.println("StartNode Translate X " + ((PortNode)startNode.getRefNode()).getTranslateX());

    System.out.println("StartNode Translate Y " + startNode.getTranslateY() );
    System.out.println("StartNode Translate Y " + ((PortNode)startNode.getRefNode()).getTranslateY());
    getStartLine().setStartX(getStartLine().getStartX()+20);
    getEndLine().setEndX(getEndLine().getEndX()+20);
    getChildren().add(getStartLine());
    System.out.println("getStartLine " + getStartLine().getStartX());
    System.out.println("getStartLine Y" + getStartLine().getStartY());
    getChildren().add(getMiddleLine());
    getChildren().add(getEndLine());
    super.draw();
    this.getChildren().add(super.getEndMultiplicity());
    this.getChildren().add(super.getStartMultiplicity());

    //Draw arrows.
    this.getChildren().add(drawArrowHead(getEndLine().getEndX(), getEndLine().getEndY(), getEndLine().getStartX(), getEndLine().getStartY()));
    
  }
  
  public void setSelected(boolean selected) {
//    super.setSelected(selected);
//    System.out.println("Set Selected is choosen and selected is " + selected);
//    System.out.println("ArrowHeadLines looks as follows " + arrowHeadLines.toString());
//    if (selected) {
//      for (Line l : arrowHeadLines) {
//        l.setStroke(Constants.selected_color);
//      }
//      startLine.setStroke(Constants.selected_color);
//      middleLine.setStroke(Constants.selected_color);
//      endLine.setStroke(Constants.selected_color);
//    }
//    else {
//      for (Line l : arrowHeadLines) {
//        l.setStroke(Color.BLACK);
//      }
//    }
    super.setSelected(selected);
    if(selected){
        for(Line l : arrowHeadLines){
            l.setStroke(Constants.selected_color);
        }
    } else {
        for (Line l : arrowHeadLines) {
            l.setStroke(Color.BLACK);
        }
    }
  }
  
//  public double getStartY() {
//    return startLine.getStartY();
//  }
  
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
  
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
//    super.propertyChange(evt);
//    String propertyName = evt.getPropertyName();
//    if (propertyName.equals(ConstantsMonti.changeEdgeStereoType)) {
//      draw();
//    }
//    if (propertyName.equals(Constants.changeMessageStartX) ){
//      startX = (Double)evt.getNewValue();
////      setPositionNoStartNode();
//      draw();
//    } else if (propertyName.equals(Constants.changeMessageStartY)){
//      startY = (Double)evt.getNewValue();
////      if(startNode != null){
////          setPosition2();
////      } else {
////          setPositionNoStartNode();
////      }
////      drawTitle(title.getText());
//      draw();
//    }
//    
//    if(evt.getPropertyName().equals(Constants.changeNodeTranslateX) || evt.getPropertyName().equals(Constants.changeNodeTranslateY)) {
//      System.out.println("SOMETHING CHANGED!!!");
//      setPosition();
//      //draw();
//    }
    
    super.propertyChange(evt);
    if(evt.getPropertyName().equals(Constants.changeNodeTranslateX) || evt.getPropertyName().equals(Constants.changeNodeTranslateY) ||
      evt.getPropertyName().equals(Constants.changeEdgeDirection) || evt.getPropertyName().equals(ConstantsMonti.changePortNodeTranslateX)
    || evt.getPropertyName().equals(ConstantsMonti.changePortNodeTranslateY)){
      setPosition();
      draw();
    }
  }
    
  
  
//  protected void setPosition() {
//    System.out.println("StrartNode X " + startNode.getTranslateX());
//    System.out.println("StrartNode Width " + startNode.getWidth());
//    if (start.getX() + start.getWidth() <= ende.getX()) { // Straight
//                                                                                       // line
//                                                                                       // if
//                                                                                       // height
//                                                                                       // difference
//                                                                                       // is
//                                                                                       // small
//      if (Math.abs(start.getX() + (start.getPortHeight() / 2) - (ende.getY() + (ende.getPortHeight() / 2))) < 0) {
//        startLine.setStartX(start.getX() + start.getPortWidth() + 0.5*start.getPortWidth());
//        startLine.setStartY(start.getY() + (start.getPortHeight() / 2));
//        startLine.setEndX(ende.getX() + 0.5*ende.getPortWidth());
//        startLine.setEndY(ende.getY() + (ende.getPortHeight() / 2));
//        
//        middleLine.setStartX(0);
//        middleLine.setStartY(0);
//        middleLine.setEndX(0);
//        middleLine.setEndY(0);
//        
//        endLine.setStartX(0);
//        endLine.setStartY(0);
//        endLine.setEndX(0);
//        endLine.setEndY(0);
//      }
//      else {
//        startLine.setStartX(start.getX() + start.getPortWidth() + 0.5*start.getPortWidth());
//        startLine.setStartY(start.getY() + (start.getPortHeight() / 2));
//        startLine.setEndX(start.getX() + start.getPortWidth() + ((ende.getX() - (start.getX() + start.getPortWidth())) / 2));
//        startLine.setEndY(start.getY() + (start.getPortHeight() / 2));
//        
//        middleLine.setStartX(start.getX() + start.getPortWidth() + ((ende.getX() - (start.getX() + start.getPortWidth())) / 2));
//        middleLine.setStartY(start.getY() + (start.getPortHeight() / 2));
//        middleLine.setEndX(start.getX() + start.getPortWidth() + ((ende.getX() - (start.getX() + start.getPortWidth())) / 2));
//        middleLine.setEndY(ende.getY() + (ende.getPortHeight() / 2));
//        
//        endLine.setStartX(start.getX() + start.getPortWidth() + ((ende.getX() - (start.getX() + start.getPortWidth())) / 2));
//        endLine.setStartY(ende.getY() + (ende.getPortHeight() / 2));
//        endLine.setEndX(ende.getX() + 0.5*ende.getPortWidth());
//        endLine.setEndY(ende.getY() + (ende.getPortHeight() / 2));
//      }
//      
//      position = Position.RIGHT;
//    }
//    // If end node is to the left of startNode:
//    else if (start.getX() > ende.getX() + ende.getPortWidth()) {
//      if (Math.abs(start.getY() + (start.getPortHeight() / 2) - (ende.getY() + (ende.getPortHeight() / 2))) < 0) {
//        startLine.setStartX(start.getX() - 0.5*start.getPortWidth());
//        startLine.setStartY(start.getY() + (start.getPortHeight() / 2));
//        startLine.setEndX(ende.getX() + ende.getPortWidth() + 0.5*ende.getPortWidth());
//        startLine.setEndY(ende.getY() + (ende.getPortHeight() / 2));
//        
//        middleLine.setStartX(0);
//        middleLine.setStartY(0);
//        middleLine.setEndX(0);
//        middleLine.setEndY(0);
//        
//        endLine.setStartX(0);
//        endLine.setStartY(0);
//        endLine.setEndX(0);
//        endLine.setEndY(0);
//      }
//      else {
//        startLine.setStartX(start.getX() + 0.5*start.getPortWidth());
//        startLine.setStartY(start.getY() + (start.getPortHeight() / 2));
//        startLine.setEndX(ende.getX() + ende.getPortWidth() + ((start.getX() - (ende.getX() + ende.getPortWidth())) / 2));
//        startLine.setEndY(start.getY() + (start.getPortHeight() / 2));
//        
//        middleLine.setStartX(ende.getX() + ende.getPortWidth() + ((start.getX() - (ende.getX() + ende.getPortWidth())) / 2));
//        middleLine.setStartY(start.getY() + (start.getPortHeight() / 2));
//        middleLine.setEndX(ende.getX() + ende.getPortWidth() + ((start.getX() - (ende.getX() + ende.getPortWidth())) / 2));
//        middleLine.setEndY(ende.getY() + (ende.getPortHeight() / 2));
//        
//        endLine.setStartX(ende.getX() + ende.getPortWidth() + ((start.getX() - (ende.getX() + ende.getPortWidth())) / 2));
//        endLine.setStartY(ende.getY() + (ende.getPortHeight() / 2));
//        endLine.setEndX(ende.getX() + ende.getPortWidth() + 0.5*ende.getPortWidth());
//        endLine.setEndY(ende.getY() + (ende.getPortHeight() / 2));
//      }
//      
//      position = Position.LEFT;
//    }
//    // If end node is below startNode:
//    else if (start.getY() + start.getPortHeight() < ende.getY()) {
//      if (Math.abs(start.getX() + (start.getPortWidth() / 2) - (ende.getX() + (ende.getPortWidth() / 2))) < 0) {
//        startLine.setStartX(start.getX() + (start.getPortWidth() / 2));
//        startLine.setStartY(start.getY() + start.getPortHeight());
//        startLine.setEndX(ende.getX() + (ende.getPortWidth() / 2));
//        startLine.setEndY(ende.getY());
//        
//        middleLine.setStartX(0);
//        middleLine.setStartY(0);
//        middleLine.setEndX(0);
//        middleLine.setEndY(0);
//        
//        endLine.setStartX(0);
//        endLine.setStartY(0);
//        endLine.setEndX(0);
//        endLine.setEndY(0);
//      }
//      else {
//        startLine.setStartX(start.getX() + (start.getPortWidth() / 2));
//        startLine.setStartY(start.getY() + start.getPortHeight());
//        startLine.setEndX(start.getX() + (start.getPortWidth() / 2));
//        startLine.setEndY(start.getY() + start.getPortHeight() + ((ende.getY() - (start.getY() + start.getPortHeight())) / 2));
//        
//        middleLine.setStartX(start.getX() + (start.getPortWidth() / 2));
//        middleLine.setStartY(start.getY() + start.getPortHeight() + ((ende.getY() - (start.getY() + start.getPortHeight())) / 2));
//        middleLine.setEndX(ende.getX() + (ende.getPortWidth() / 2));
//        middleLine.setEndY(start.getY() + start.getPortHeight() + ((ende.getY() - (start.getY() + start.getPortHeight())) / 2));
//        
//        endLine.setStartX(ende.getX() + (ende.getPortWidth() / 2));
//        endLine.setStartY(start.getY() + start.getPortHeight() + ((ende.getY() - (start.getY() + start.getPortHeight())) / 2));
//        endLine.setEndX(ende.getX() + (ende.getPortWidth() / 2));
//        endLine.setEndY(ende.getY());
//      }
//      
//      position = Position.BELOW;
//    }
//    // If end node is above startNode:
//    else if (start.getY() >= ende.getY() + ende.getPortHeight()) {
//      if (Math.abs(start.getX() + (start.getPortWidth() / 2) - (ende.getX() + (ende.getPortWidth() / 2))) < 0) {
//        startLine.setStartX(start.getX() + (start.getPortWidth() / 2));
//        startLine.setStartY(start.getY());
//        startLine.setEndX(ende.getX() + (ende.getPortWidth() / 2));
//        startLine.setEndY(ende.getY() + ende.getPortHeight());
//        
//        middleLine.setStartX(0);
//        middleLine.setStartY(0);
//        middleLine.setEndX(0);
//        middleLine.setEndY(0);
//        
//        endLine.setStartX(0);
//        endLine.setStartY(0);
//        endLine.setEndX(0);
//        endLine.setEndY(0);
//      }
//      else {
//        startLine.setStartX(start.getX() + (start.getPortWidth() / 2));
//        startLine.setStartY(start.getY());
//        startLine.setEndX(start.getX() + (start.getPortWidth() / 2));
//        startLine.setEndY(ende.getY() + ende.getPortHeight() + ((start.getY() - (ende.getY() + ende.getPortHeight())) / 2));
//        
//        middleLine.setStartX(start.getX() + (start.getPortWidth() / 2));
//        middleLine.setStartY(ende.getY() + ende.getPortHeight() + ((start.getY() - (ende.getY() + ende.getPortHeight())) / 2));
//        middleLine.setEndX(ende.getX() + (ende.getPortWidth() / 2));
//        middleLine.setEndY(ende.getY() + ende.getPortHeight() + ((start.getY() - (ende.getY() + ende.getPortHeight())) / 2));
//        
//        endLine.setStartX(ende.getX() + (ende.getPortWidth() / 2));
//        endLine.setStartY(ende.getY() + ende.getPortHeight() + ((start.getY() - (ende.getY() + ende.getPortHeight())) / 2));
//        endLine.setEndX(ende.getX() + (ende.getPortWidth() / 2));
//        endLine.setEndY(ende.getY() + ende.getPortHeight());
//      }
//      
//      position = Position.ABOVE;
//    }
//    // TODO Handle when the nodes are overlapping.
//  }
  
}
