package model.edges;

import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.Node;
import model.nodes.PortNode;

public class ConnectorEdge extends AbstractEdge{
  private PortNode startPort;
  private PortNode endPort;
  
  public ConnectorEdge(ComponentNode startNode, ComponentNode endNode, PortNode startPort, PortNode endPort) {
    super(startNode, endNode);
    this.startPort = startPort;
    this.endPort = endPort;
  }

  @Override
  public Edge copy(AbstractNode startNodeCopy, AbstractNode endNodeCopy) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setTranslateX(double x) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setTranslateY(double y) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setScaleX(double x) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setScaleY(double y) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public double getTranslateX() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getTranslateY() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getScaleY() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getScaleX() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public PortNode getStartPort() {
    return startPort;
  }
  
  public PortNode getEndPort() {
    return endPort;
  }

  
}
