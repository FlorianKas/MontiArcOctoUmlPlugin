package util.commands;

import model.nodes.Infos;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetInfoGenericCommand implements Command {
  
  private Infos node;
  private String newName;
  private String oldName;
  
  public SetInfoGenericCommand(Infos pNode, String pNewName, String pOldName) {
    node = pNode;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    node.setGenerics(oldName);
  }
  
  @Override
  public void execute() {
    node.setGenerics(newName);
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
