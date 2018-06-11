package util.commands;

import model.nodes.Infos;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetInfoImportCommand implements Command {
  
  private Infos node;
  private String newName;
  private String oldName;
  
  public SetInfoImportCommand(Infos pNode, String pNewName, String pOldName) {
    node = pNode;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    node.setImports(oldName);
  }
  
  @Override
  public void execute() {
    node.setImports(newName);
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
