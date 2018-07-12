package util.commands;

import model.nodes.ComponentNode;
import model.nodes.Node;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetNodeSubNameCommand implements Command {
  
  private Node node;
  private String newName;
  private String oldName;
  
  public SetNodeSubNameCommand(Node pNode, String pNewName, String pOldName) {
    node = pNode;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    ((ComponentNode)node).setSubName(oldName);
  }
  
  @Override
  public void execute() {
    ((ComponentNode)node).setSubName(newName);
  }
  
  public Node getNode() {
    return node;
  }
  
  public String getNewName() {
    return newName;
  }
  
  public String getOldName() {
    return oldName;
  }
}
