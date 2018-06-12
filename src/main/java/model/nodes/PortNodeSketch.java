package model.nodes;

import util.ConstantsMonti;

public class PortNodeSketch extends AbstractNode {
  //values of the sketch
//  private double PORT_WIDTH = 40; 
//  private double PORT_HEIGHT = 40;
//  private double xDraw;
//  private double yDraw;
//  private double xDiff;
//  private double yDiff;
  
  public PortNodeSketch(double x, double y, double height, double width) {
    super(x, y, width, height);
    // Don't accept nodes with size less than minWidth * minHeight.
//    this.width = PORT_WIDTH;
//    this.height = PORT_HEIGHT;
//    this.width = width;
//    this.height = height;
//    this.xDiff = this.x - xDraw;
//    this.yDiff = this.y - yDraw;
  }
  
  public PortNodeSketch() {
    super();
  }
  
//  public void setXDraw(double pXDraw) {
//    xDraw = pXDraw;
//    this.translateX = xDraw;
//    
//  }
//  
//  public void setYDraw(double pYDraw) {
//    yDraw = pYDraw;
//    this.translateY = yDraw;
//    
//  }
  
  
  
//  public double getXDraw() {
//    return xDraw;
//  }
//  
//  public double getYDraw() {
//    return yDraw;
//  }
//  
//  public void setComponentNode(ComponentNode pComponentNode) {
//    this.node = pComponentNode;
//  }
  
//  public ComponentNode getComponentNode() {
//    return this.node;
//  }
//  
//  public double getPortWidth() {
//    return PORT_WIDTH;
//  }
//
//  public double getPortHeight() {
//    return PORT_HEIGHT;
//  }

  
  @Override
  public PortNodeSketch copy() {
    PortNodeSketch newCopy = new PortNodeSketch(this.getX(), this.getY(), this.getHeight(), this.getWidth());
    newCopy.setTranslateX(this.getTranslateX());
    newCopy.setTranslateY(this.getTranslateY());
    newCopy.setScaleX(this.getScaleX());
    newCopy.setScaleY(this.getScaleY());
    
    if (this.getTitle() != null) {
      newCopy.setTitle(this.getTitle());
      
    }
    newCopy.setTranslateX(this.getTranslateX());
    newCopy.setTranslateY(this.getTranslateY());
    return newCopy;
    
  }
  
  @Override
  public void setHeight(double height) {
    this.height = height;
    super.setHeight(height);
  }
  
  @Override
  public void setWidth(double width) {
    this.width = width;
    // das macht Probleme, vielleicht auch auf PORT_WIDTH
//    super.setWidth(width);
  }
  
  @Override
  public void remoteSetHeight(double height) {
    this.height = height;
    super.remoteSetHeight(height);
  }
  
  @Override
  public void remoteSetWidth(double width) {
    this.width = width;
    super.remoteSetWidth(width);
  }

  @Override
  public String getType() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  
  
}
