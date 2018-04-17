package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class POMEditor {
  private static final POMEditor pomEdit = new POMEditor();
  
  private List<String> oldLines = new ArrayList<String>();
  private List<String> newLines = new ArrayList<String>();
  private int insertIndex = 0;
  
  private POMEditor() {
  }
  
  public void addDependency(String dependencyText) {
    fillOldContent();
    // TODO
    for (int i = 0; i < oldLines.size(); i++) {
      String line = oldLines.get(i);
      if (line.contains("</dependencies>")) {
        insertIndex = i;
      }
    }
    for (int i = 0; i < insertIndex; i++) {
      newLines.add(oldLines.get(i));
    }
    newLines.add(dependencyText);
    for (int i = insertIndex; i < oldLines.size(); i++) {
      newLines.add(oldLines.get(i));
    }
    rewritePOM();
  }
  
  private void rewritePOM() {
    FileWriter fw;
    try {
      fw = new FileWriter("path");
      BufferedWriter bw = new BufferedWriter(fw);
      for (String s : newLines) {
        bw.write(s);
      }
      bw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void fillOldContent() {
    File currentDirFile = new File(".");
    String currentResourceDir = currentDirFile.getAbsolutePath().substring(0, currentDirFile.getAbsolutePath().length() - 1);
    File pomFile = new File(currentResourceDir + "pom.xml");
    
    try {
      BufferedReader br = new BufferedReader(new FileReader(pomFile));
      for (String line; (line = br.readLine()) != null;) {
        oldLines.add(line);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static POMEditor getInstance() {
    return pomEdit;
  }
}
