package util.commands;

import model.nodes.Infos;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetInfoPackageCommand implements Command {
  
  private Infos node;
  private String newName;
  private String oldName;
  
  public SetInfoPackageCommand(Infos pNode, String pNewName, String pOldName) {
    node = pNode;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    node.setPackageName(oldName);
  }
  
  @Override
  public void execute() {
    node.setPackageName(newName);
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
