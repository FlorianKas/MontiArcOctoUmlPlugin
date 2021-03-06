package model.edges;

import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.Node;
import model.nodes.PortNode;
import util.ConstantsMonti;

public class ConnectorEdge extends AbstractEdge{
  private String dataType = "";
  private String stereoType = "";
  
  public ConnectorEdge(PortNode startPort, PortNode endPort) {
    super(startPort, endPort);
  }

  @Override
  public Edge copy(AbstractNode startNodeCopy, AbstractNode endNodeCopy) {
    // TODO Auto-generated method stub
    return new ConnectorEdge((PortNode)startNodeCopy, (PortNode)endNodeCopy);
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
    return (PortNode) this.getStartNode();
  }
  
  public PortNode getEndPort() {
    return (PortNode) this.getEndNode();
  }
  
  public String getDataType() {
    return dataType;
  }
  
  public void setDataType(String pDataType) {
    changes.firePropertyChange(ConstantsMonti.changeConnectorDataType, dataType, pDataType);
    remoteChanges.firePropertyChange(ConstantsMonti.changeConnectorDataType, dataType, pDataType);
    dataType = pDataType;
  }
  
  public void remoteSetDataType(String pDataType) {
    changes.firePropertyChange(ConstantsMonti.changeConnectorDataType, dataType, pDataType);
    dataType = pDataType;
  }

  public String getType() {
    return "Connector";
  }

  public String getStereoType() {
    // TODO Auto-generated method stub
    return stereoType;
  }
  
  public void setStereoType(String pStereoType) {
    stereoType = pStereoType;
  }
  
}
