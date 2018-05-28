package util.commands;

import model.nodes.Node;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetNodeGenericsCommand implements Command {
  
  private Node node;
  private String newGenerics;
  private String oldGenerics;
  
  public SetNodeGenericsCommand(Node pNode, String pNewGenerics, String pOldGenerics) {
    node = pNode;
    newGenerics = pNewGenerics;
    oldGenerics = pOldGenerics;
  }
  
  @Override
  public void undo() {
    node.setTitle(oldGenerics);
  }
  
  @Override
  public void execute() {
    node.setTitle(newGenerics);
  }
  
  public Node getNode() {
    return node;
  }
  
  public String getNewGenerics() {
    return newGenerics;
  }
  
  public String getOldGenerics() {
    return oldGenerics;
  }
}
