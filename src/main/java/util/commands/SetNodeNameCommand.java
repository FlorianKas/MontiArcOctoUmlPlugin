package util.commands;

import model.nodes.Node;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetNodeNameCommand implements Command {
  
  private Node node;
  private String newName;
  private String oldName;
  
  public SetNodeNameCommand(Node pNode, String pNewName, String pOldName) {
    node = pNode;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    node.setTitle(oldName);
  }
  
  @Override
  public void execute() {
    node.setTitle(newName);
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
