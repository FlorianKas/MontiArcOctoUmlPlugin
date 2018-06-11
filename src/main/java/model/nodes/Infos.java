package model.nodes;

import java.beans.PropertyChangeSupport;

import util.ConstantsMonti;

public class Infos {
  private String name = "";
  private String imports = "";
  private String packageName = "";
  private String generics = "";
  private String arcParam = "";

  // Listened to by the view, is always fired.
  protected transient PropertyChangeSupport changes = new PropertyChangeSupport(this);
  // Listened to by the server/client, only fired when the change comes from
  // local interaction.
  protected transient PropertyChangeSupport remoteChanges = new PropertyChangeSupport(this);
  
  public Infos() {
    
  }
  public String getName() {
    return name;
  }
  public void setName(String pName) {
    name = pName;
    changes.firePropertyChange(ConstantsMonti.changeInfoName, null, pName);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoName, null, pName);
  }
  public void remoteSetName(String pName) {
    name = pName;
    changes.firePropertyChange(ConstantsMonti.changeInfoName, null, pName);
  }
  public String getImports() {
    return imports;
  }
  public void setImports(String pImports) {
    imports = pImports;
    changes.firePropertyChange(ConstantsMonti.changeInfoImport, null, pImports);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoImport, null, pImports);
  }
  public void remoteSetImports(String pImports) {
    imports = pImports;
    changes.firePropertyChange(ConstantsMonti.changeInfoImport, null, pImports);
  }
  public String getPackageName() {
    return packageName;
  }
  public void setPackageName(String pPackageName) {
    packageName = pPackageName;
    changes.firePropertyChange(ConstantsMonti.changeInfoPackage, null, pPackageName);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoPackage, null, pPackageName);
  }
  public void remoteSetPackageName(String pPackageName) {
    changes.firePropertyChange(ConstantsMonti.changeInfoPackage, null, pPackageName);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoPackage, null, pPackageName);
  }
  public String getGenerics() {
    return generics;
  }
  public void setGenerics(String pGenerics) {
    generics = pGenerics;
    changes.firePropertyChange(ConstantsMonti.changeInfoGeneric, null, pGenerics);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoGeneric, null, pGenerics);
  }
  public void remoteSetGenerics(String pGenerics) {
    changes.firePropertyChange(ConstantsMonti.changeInfoGeneric, null, pGenerics);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoGeneric, null, pGenerics);
  }
  
  public String getArcParam() {
    return arcParam;
  }
  public void setArcParam(String pArcParam) {
    arcParam = pArcParam;
    changes.firePropertyChange(ConstantsMonti.changeInfoArcParam, null, pArcParam);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoArcParam, null, pArcParam);
  }
  public void remoteSetArcParam(String pArcParam) {
    changes.firePropertyChange(ConstantsMonti.changeInfoArcParam, null, pArcParam);
    remoteChanges.firePropertyChange(ConstantsMonti.changeInfoArcParam, null, pArcParam);
  }

}
