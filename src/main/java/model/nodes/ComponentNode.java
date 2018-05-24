package model.nodes;

import util.ConstantsMonti;
import view.nodes.AbstractNodeView;

import java.io.Serializable;
import java.util.ArrayList;

public class ComponentNode extends AbstractNode implements Serializable {
  private static final String TYPE = "COMPONENT";
 protected static final double COMPONENT_MIN_WIDTH = 120;
 protected static final double COMPONENT_MIN_HEIGHT = 100;
 
  // pflicht, title is optional
  private String componentType = "";
  private String stereotype = "";
  private ArrayList<PortNode> Ports = new ArrayList<>();
  
//  TODO: Future Work
//  private String Subcomponents; 
  
  public ComponentNode(double x, double y, double height, double width, ArrayList<PortNode> ports) {
    super(x, y, height, width);
    this.width = width < COMPONENT_MIN_WIDTH ? COMPONENT_MIN_WIDTH : width;
    this.height = height < COMPONENT_MIN_HEIGHT ? COMPONENT_MIN_HEIGHT : height;
    this.Ports = ports;
  }
  
  public ComponentNode() {
    
  }
  
  @Override
  public ComponentNode copy() {
    ComponentNode newCopy = new ComponentNode(this.getX(), this.getY(), this.getHeight(), this.getWidth(), this.getPorts());
    newCopy.setTranslateX(this.getTranslateX());
    newCopy.setTranslateY(this.getTranslateY());
    newCopy.setScaleX(this.getScaleX());
    newCopy.setScaleY(this.getScaleY());
    
    if (this.getTitle() != null) {
      newCopy.setTitle(this.getTitle());
      
    }
    if (this.getComponentType() != null) {
      newCopy.setComponentType(this.getComponentType());
    }
    if (this.getStereotype() != null) {
      newCopy.setStereotype(this.getStereotype());
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
  
  
  public void setStereotype(String pStereotype) {
    stereotype = pStereotype;
    changes.firePropertyChange(ConstantsMonti.changeComponentStereotype, null, pStereotype);
    remoteChanges.firePropertyChange(ConstantsMonti.changeComponentStereotype, null, pStereotype);
  }
  
  public void remoteSetSterotype(String pStereotype) {
    stereotype = pStereotype;
    changes.firePropertyChange(ConstantsMonti.changeComponentStereotype, null, pStereotype);
  }
  
  public String getStereotype() {
    return stereotype;  
  }
  
  public void setComponentType(String pcomponentType) {
    componentType = pcomponentType;
    changes.firePropertyChange(ConstantsMonti.changeComponentNodeDataType, null, pcomponentType);
    remoteChanges.firePropertyChange(ConstantsMonti.changeComponentNodeDataType, null, pcomponentType);
  }
  
  public void remoteSetComponentType(String pcomponentType) {
    componentType = pcomponentType;
    changes.firePropertyChange(ConstantsMonti.changeComponentNodeDataType, null, pcomponentType);
  }
  
  public String getComponentType() {
    return componentType;  
  }
  
}
