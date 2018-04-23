package util.commands;

import model.nodes.Node;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetNodeStereotypeCommand implements Command {
  
  private Node node;
  private String newStereotype;
  private String oldStereotype;
  
  public SetNodeStereotypeCommand(Node pNode, String pNewStereotype, String pOldStereotype) {
    node = pNode;
    newStereotype = pNewStereotype;
    oldStereotype = pOldStereotype;
  }
  
  @Override
  public void undo() {
    node.setTitle(oldStereotype);
  }
  
  @Override
  public void execute() {
    node.setTitle(newStereotype);
  }
  
  public Node getNode() {
    return node;
  }
  
  public String getNewStereotype() {
    return newStereotype;
  }
  
  public String getOldStereotype() {
    return oldStereotype;
  }
}
