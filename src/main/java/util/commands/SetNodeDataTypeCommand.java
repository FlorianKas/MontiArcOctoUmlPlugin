package util.commands;

import model.nodes.PortNode;
import model.nodes.Node;

  /**
   * Created by chalmers on 2016-08-29.
   */
public class SetNodeDataTypeCommand implements Command {
  private PortNode node;
  private String newDataType;
  private String oldDataType;
    
  public SetNodeDataTypeCommand(PortNode pNode, String pNewDataType, String pOldDataType) {
    node = pNode;
    newDataType = pNewDataType;
    oldDataType = pOldDataType;
  }
   
  @Override
  public void undo() {
    node.setPortType(oldDataType);
  }
    
  @Override
  public void execute() {
    node.setPortType(newDataType);
  }
    
  public Node getNode() {
    return node;
  }
    
  public String getNewDataType() {
    return newDataType;
  }
    
  public String getOldDataType() {
    return oldDataType;
  }
}




