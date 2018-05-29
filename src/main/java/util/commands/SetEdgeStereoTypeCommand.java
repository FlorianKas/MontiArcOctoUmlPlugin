package util.commands;

import model.edges.ConnectorEdge;
import model.edges.Edge;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetEdgeStereoTypeCommand implements Command {
  
  private Edge edge;
  private String newName;
  private String oldName;
  
  public SetEdgeStereoTypeCommand(Edge pedge, String pNewName, String pOldName) {
    edge = pedge;
    newName = pNewName;
    oldName = pOldName;
  }
  
  @Override
  public void undo() {
    ((ConnectorEdge)edge).setStereoType(oldName);
  }
  
  @Override
  public void execute() {
    ((ConnectorEdge)edge).setStereoType(newName);
  }
  
  public Edge getNode() {
    return edge;
  }
  
  public String getNewStereoType() {
    return newName;
  }
  
  public String getOldStereoType() {
    return oldName;
  }
}
