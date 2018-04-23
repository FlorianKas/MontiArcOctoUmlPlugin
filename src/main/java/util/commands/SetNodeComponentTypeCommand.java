package util.commands;

import model.nodes.ComponentNode;
import model.nodes.Node;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetNodeComponentTypeCommand implements Command {
  private ComponentNode node;
  private String newDataType;
  private String oldDataType;
    
  public SetNodeComponentTypeCommand(ComponentNode pNode, String pNewDataType, String pOldDataType) {
    node = pNode;
    newDataType = pNewDataType;
    oldDataType = pOldDataType;
  }
   
  @Override
  public void undo() {
    node.setComponentType(oldDataType);
  }
    
  @Override
  public void execute() {
    node.setComponentType(newDataType);
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




