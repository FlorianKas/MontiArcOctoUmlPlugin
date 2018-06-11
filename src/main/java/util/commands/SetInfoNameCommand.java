package util.commands;

import model.nodes.Infos;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetInfoNameCommand implements Command {
  
  private Infos node;
  private String newName;
  private String oldName;
  
  public SetInfoNameCommand(Infos pNode, String pNewName, String pOldName) {
    node = pNode;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    node.setName(oldName);
  }
  
  @Override
  public void execute() {
    node.setName(newName);
  }
  
  public Infos getNode() {
    return node;
  }
  
  public String getNewName() {
    return newName;
  }
  
  public String getOldName() {
    return oldName;
  }
}
