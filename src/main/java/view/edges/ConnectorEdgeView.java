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
    this.setStrokeWidth(super.STROKE_WIDTH);
    this.setStroke(Color.BLACK);
    setPosition();
    draw();
  }
  
  protected void draw() {
    getChildren().clear();
    getStartLine().setStartX(getStartLine().getStartX()+20);
    getEndLine().setEndX(getEndLine().getEndX()+20);
    getChildren().add(getStartLine());
    getChildren().add(getMiddleLine());
    getChildren().add(getEndLine());
    //Draw arrows.
    this.getChildren().add(drawArrowHead(getEndLine().getEndX(), getEndLine().getEndY(), getEndLine().getStartX(), getEndLine().getStartY()));
    
  }
  
  public void setSelected(boolean selected) {
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
    super.propertyChange(evt);
//    if(evt.getPropertyName().equals(Constants.changeNodeTranslateX) || evt.getPropertyName().equals(Constants.changeNodeTranslateY) ||
//      evt.getPropertyName().equals(Constants.changeEdgeDirection) || evt.getPropertyName().equals(ConstantsMonti.changePortNodeTranslateX)
//    || evt.getPropertyName().equals(ConstantsMonti.changePortNodeTranslateY)){
    if(evt.getPropertyName().equals(ConstantsMonti.changePortNodeTranslateX)
      || evt.getPropertyName().equals(ConstantsMonti.changePortNodeTranslateY)){
      setPosition();
      draw();
    }
  }
}
