package exceptions;

import java.util.ArrayList;
import java.util.List;
import plugin.MontiCoreException;

public class MontiArcPlugInErrors {
  
  private static final MontiArcPlugInErrors singleError = new MontiArcPlugInErrors();
  private ArrayList<MontiCoreException> allErrors = new ArrayList();
  
  public static MontiArcPlugInErrors getInstance()
  {
    return singleError;
  }

  public void addLog(MontiCoreException pExc) {
    this.allErrors.add(pExc);
  }

  public List<MontiCoreException> getAllLogs() {
    return this.allErrors;
  }

  static enum ExceptionType
  {
    OUTER_COMPONENT_NAME_MISSING, INNER_COMPONENT_NAME_MISSING, INNER_COMPONENT_EXTENDS_MISSING, CONNECTOR_SOUCRE_IS_MISSING, NO_CONNECTOR_TARGET_AVAILABLE, PORT_IS_MISSING, PORT_DIRECTION_NOT_SET, PORT_TYPE_MISSING, OUTER_COMPONENT_EXTENDS_MISSING, IMPORT_ERROR, INNER_COMPONENT_LOW, PACKAGE_NAME_WRONG, PACKAGE_DIRECTORY_WRONG, EXTENDS_OUTER_MISSING, GEN_OUTER_VAR_WRONG, GEN_SPLIT_WRONG, GEN_TYPE_WRONG_EXCEPTION, OUTER_COMPONENT_GEN_WRONG, GEN_TYPE_WRONG_PARAM_EXCEPTION, INNER_COMPONENT_GEN_PARAM_WRONG, COCO_EXCEPTION, ALL_FINE;
  }
}




