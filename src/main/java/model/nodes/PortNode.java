package model.nodes;

import java.io.Serializable;

import util.Constants;
import util.ConstantsMonti;

public class PortNode extends AbstractNode implements Serializable{
//  values of the PortNode
  private static final String TYPE = "PORT";
  private String portType = "";
  private String portDirection = "";
  private ComponentNode node;
  private static double PORT_WIDTH = 40; 
  private static double PORT_HEIGHT = 40;
//  private double xDraw;
//  private double yDraw;
//  private double xDiff;
//  private double yDiff;
  private PortNodeSketch pNodeSketch;
  
  public PortNode(double x, double y, double height, double width) {
    super(x, y, PORT_HEIGHT, PORT_WIDTH);
    // Don't accept nodes with size less than minWidth * minHeight.
//    this.width = PORT_WIDTH;
//    this.height = PORT_HEIGHT;
  }
  
  public PortNode() {
    super();
  }
  
  public void createPortNodeSketch(double x, double y, double height, double width) {
    // sketch x, y, height and width
    pNodeSketch = new PortNodeSketch(x,y,height, width);
  }
  
  public PortNodeSketch getPortNodeSketch() {
    return pNodeSketch;
  }
  
//  public void setXDraw(double pXDraw) {
//    xDraw = pXDraw;
//    this.translateX = xDraw;
//    
//  }
  
//  public void setYDraw(double pYDraw) {
//    yDraw = pYDraw;
//    this.translateY = yDraw;
//    
//  }
  
//  public double getXDraw() {
//    return xDraw;
//  }
  
//  public double getYDraw() {
//    return yDraw;
//  }
  
  public void setTranslateX(double x) {
    translateX = x;
    changes.firePropertyChange(ConstantsMonti.changePortNodeTranslateX, null, translateX);
    remoteChanges.firePropertyChange(ConstantsMonti.changePortNodeTranslateX, null, translateX);
  }
  
  public void setTranslateY(double y) {
    translateY = y;
    changes.firePropertyChange(ConstantsMonti.changePortNodeTranslateY, null, translateY);
    remoteChanges.firePropertyChange(ConstantsMonti.changePortNodeTranslateY, null, translateY);
  }
  
  
  public void setComponentNode(ComponentNode pComponentNode) {
    this.node = pComponentNode;
  }
  
  public ComponentNode getComponentNode() {
    return this.node;
  }
  
  public double getPortWidth() {
    return PORT_WIDTH;
  }

  public double getPortHeight() {
    return PORT_HEIGHT;
  }

  
  @Override
  public PortNode copy() {
    PortNode newCopy = new PortNode(this.getX(), this.getY(), this.getHeight(), this.getWidth());
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
    this.height = PORT_HEIGHT;
    super.setHeight(height);
  }
  
  @Override
  public void setWidth(double width) {
    this.width = PORT_WIDTH;
    // das macht Probleme, vielleicht auch auf PORT_WIDTH
    super.setWidth(width);
  }
  
  @Override
  public void remoteSetHeight(double height) {
    this.height = PORT_HEIGHT;
    super.remoteSetHeight(height);
  }
  
  @Override
  public void remoteSetWidth(double width) {
    this.width = PORT_WIDTH;
    super.remoteSetWidth(width);
  }
  
  
  public void setPortType(String pportType) {
    portType = pportType;
    changes.firePropertyChange(ConstantsMonti.changePortNodeDataType, null, portType);
    remoteChanges.firePropertyChange(ConstantsMonti.changePortNodeDataType, null, portType);
  }
  
  public void remoteSetPortType(String pportType) {
    portType = pportType;
    changes.firePropertyChange(ConstantsMonti.changePortNodeDataType, null, portType);
  }
  
    
  public String getType() {
    return TYPE;
  }
  
  public String getPortDirection() {
    return portDirection;
  }
  
  public void setPortDirection(String pPortDirection) {
    this.portDirection = pPortDirection;
  }
  
  public String getPortType() {
    return portType;  
  }
}
