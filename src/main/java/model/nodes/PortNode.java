package model.nodes;

import util.Constants;

public class PortNode extends AbstractNode {
  
  private static final String TYPE = "PORT";
  private String portType = "";
  
  public PortNode(double x, double y, double width, double height) {
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
  
  @Override
  public PortNode copy() {
    PortNode newCopy = new PortNode(this.getX(), this.getY(), this.getWidth(), this.getHeight());
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
    changes.firePropertyChange(Constants.changePortNodeDataType, null, portType);
    remoteChanges.firePropertyChange(Constants.changePortNodeDataType, null, portType);
  }
  
  public void remoteSetPortType(String pportType) {
    portType = pportType;
    changes.firePropertyChange(Constants.changePortNodeDataType, null, portType);
  }
  
    
  public String getType() {
    return TYPE;
  }
  
  public String getPortType() {
    return portType;  
  }
  
  public double getDefaultPortWidth() {
    return PORT_WIDTH;
  }
  
  public double getDefaultPortHeight() {
    return PORT_HEIGHT;
  }
}
