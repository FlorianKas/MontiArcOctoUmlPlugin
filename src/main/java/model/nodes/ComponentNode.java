package model.nodes;

import util.Constants;
import view.nodes.AbstractNodeView;

import java.io.Serializable;
import java.util.ArrayList;

public class ComponentNode extends AbstractNode implements Serializable {
  private static final String TYPE = "COMPONENT";
  private ArrayList<PortNode> Ports = new ArrayList<>();
  
  // hier ein Array von Ports
  private String Subcomponents; // TODO: Wieso String und nicht besser ein
                                // Array?
  
  // ueberlegen, ob hier nicht noch was rein muss. Wenn ja, brauchen wir auch
  // setter/getter und muessen das in der Copy ber�cksichtigen
  
  public ComponentNode(double x, double y, double width, double height, ArrayList<PortNode> ports) {
    // irgendwas fuer Ports. Vielleicht auch einfach ein Array von Ports
    super(x, y, width, height);
    this.width = width < COMPONENT_MIN_WIDTH ? COMPONENT_MIN_WIDTH : width;
    this.height = height < COMPONENT_MIN_HEIGHT ? COMPONENT_MIN_HEIGHT : height;
    this.Ports = ports;
  }
  
  @Override
  public ComponentNode copy() {
    ComponentNode newCopy = new ComponentNode(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getPorts());
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
    this.height = height < COMPONENT_MIN_HEIGHT ? COMPONENT_MIN_HEIGHT : height;
    super.setHeight(height);
  }
  
  @Override
  public void setWidth(double width) {
    this.width = width < COMPONENT_MIN_WIDTH ? COMPONENT_MIN_WIDTH : width;
    super.setWidth(width);
  }
  
  @Override
  public void remoteSetHeight(double height) {
    this.height = height < COMPONENT_MIN_HEIGHT ? COMPONENT_MIN_HEIGHT : height;
    super.remoteSetHeight(height);
  }
  
  @Override
  public void remoteSetWidth(double width) {
    this.width = width < COMPONENT_MIN_WIDTH ? COMPONENT_MIN_WIDTH : width;
    super.remoteSetWidth(width);
  }
  
  public String getType() {
    return TYPE;
  }
  
  public ArrayList<PortNode> getPorts() {
    return this.Ports;
  }
  
  public void addPort(PortNode node) {
    this.Ports.add(node);
  }
  
}
