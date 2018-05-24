package model.nodes;

import util.ConstantsMonti;

public class PortNode extends AbstractNode {
  
  private static final String TYPE = "PORT";
  private String portType = "";
  private String portDirection = "";
  private ComponentNode node;
  private double PORT_WIDTH = 40; 
  private double PORT_HEIGHT = 40;
  private double xDraw;
  private double yDraw;
  
  public PortNode(double x, double y, double height, double width) {
    super(x, y, width, height);
    // Don't accept nodes with size less than minWidth * minHeight.
//    this.width = PORT_WIDTH;
//    this.height = PORT_HEIGHT;
    this.width = width;
    this.height = height;
  }
  
  public PortNode() {
    super();
  }
  
  public void setXDraw(double pXDraw) {
    xDraw = pXDraw;
  }
  
  public void setYDraw(double pYDraw) {
    yDraw = pYDraw;
  }
  
  public double getXDraw() {
    return xDraw;
  }
  
  public double getYDraw() {
    return yDraw;
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
