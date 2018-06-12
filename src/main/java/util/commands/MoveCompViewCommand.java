/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package util.commands;

import model.nodes.AbstractNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import model.GraphElement;

/**
 * Stores the moving of a node.
 * @author EJBQ
 */
public class MoveCompViewCommand implements Command
{
  private ComponentNode aGraphElement;
  private double aDX;
  private double aDY;
  
  /**
   * Creates the command.
   * @param pNode The node being moved
   * @param pDX The amount moved horizontally
   * @param pDY The amount moved vertically
   */
  public MoveCompViewCommand(ComponentNode pNode, double pDX, double pDY)
  {
    aGraphElement = pNode;
    aDX = pDX;
    aDY = pDY;
    System.out.println("After construction the move");
  }
  
  /**
   * Undoes the command and moves the node back where it came from.
   */
  public void undo() 
  {
    aGraphElement.setTranslateX(aGraphElement.getTranslateX()-aDX);
    aGraphElement.setTranslateY(aGraphElement.getTranslateY()-aDY);
    for (PortNode p: aGraphElement.getPorts()) {
      p.setTranslateX(p.getTranslateX()-aDX);
      p.setTranslateY(p.getTranslateY()-aDY);
    }
  }

  /**
   * Performs the command and moves the node.
   */
  public void execute() 
  {
    aGraphElement.setTranslateX(aGraphElement.getTranslateX()+aDX);
    aGraphElement.setTranslateY(aGraphElement.getTranslateY()+aDY);
    for (PortNode p: aGraphElement.getPorts()) {
      System.out.println("p TranslateX" + p.getTranslateX());
      p.setTranslateX(p.getTranslateX()+aDX);
      System.out.println("p TranslateX after" + p.getTranslateX());
      p.setTranslateY(p.getTranslateY()+aDY);
    }
    
  }

  public GraphElement getGraphElement() {
    return aGraphElement;
  }

  public double getStartX(){
    return aGraphElement.getTranslateX()-aDX;
  }

  public double getStartY(){
    return aGraphElement.getTranslateY()-aDY;
  }

  public double getEndX(){
    return aGraphElement.getTranslateX();
  }

  public double getEndY(){
    return aGraphElement.getTranslateY();
  }
}
