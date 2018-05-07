package view.nodes;

import model.nodes.PortNode;
import util.Constants;

import java.beans.PropertyChangeEvent;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class PortNodeView extends AbstractNodeView {
  private Label title;
  private Label dataType;
  private Rectangle rectangle;
  private Pane portPane;
  private final int STROKE_WIDTH = 1;
  private double portX;
  double portY;
  double portHeight;
  double portWidth;

  public PortNodeView(PortNode node) {
    super(node);
    
    this.portX = node.getXDraw();
    this.portY = node.getYDraw();
    this.portHeight = node.getPortHeight();
    this.portWidth = node.getPortWidth();
    this.setTranslateX(node.getTranslateX());
    this.setTranslateY(node.getTranslateY());
  }
  
  
  public PortNodeView() {
    
  }
  public Pane createPortPane(double portX, double portY, double portHeight, double portWidth) {
    rectangle = new Rectangle(portX, portY, portHeight, portWidth);
    initLooks();
    portPane = new Pane();
    portPane.getChildren().add(rectangle);
    System.out.println("Left:" + rectangle);
    return portPane;
  }
  
  public Pane getPane() {
    return portPane;
  }
  
  public double getPortX() {
    return this.portX;
  }
  
  public double getPortY() {
    return this.portY;
  }
  
  public double getPortWidth() {
    return this.portWidth;
  }
  
  public double getPortHeight() {
    return this.portHeight;
  }
  
  public Label setPortTitle(String attr, double PositionX, double PositionY) {
//    title.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
    title = new Label();
    System.out.println("We are going to set a title" + attr);
    if(attr != null) {
      System.out.println("Before setting title, the title is of the form " + title);
      
      title.setText(attr);
      System.out.println("After setting title");
    }
    title.setAlignment(Pos.TOP_CENTER);
    title.setTranslateX(PositionX);
    title.setTranslateY(PositionY);
    title.setFont(new Font("Arial", 10));
    System.out.println("After setting properties");
    
    return title;
  }
  
  public Label setPortDataType(String attr, double PositionX, double PositionY) {
//    dataType.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
    dataType = new Label();
    System.out.println("We are going to set a title" + attr);
    if(attr != null) {
      dataType.setText(attr);
    }
    dataType.setAlignment(Pos.TOP_CENTER);
    dataType.setTranslateX(PositionX);
    dataType.setTranslateY(PositionY);
    dataType.setFont(new Font("Arial", 10));
    return dataType;
  }
  
  private void createRectangles() {
    PortNode node = (PortNode) getRefNode();
    changeHeight(node.getHeight());
    changeWidth(node.getWidth());
    rectangle.setX(node.getX());
    rectangle.setY(node.getY());
  }
  
  private void changeHeight(double height) {
    setHeight(height);
    rectangle.setHeight(height);
  }
  
  private void changeWidth(double width) {
    setWidth(width);
    rectangle.setWidth(width); 
    
    title.setMaxWidth(width);
    title.setPrefWidth(width);
    
    dataType.setMaxWidth(width);
    dataType.setMaxHeight(width);
  }
  
  private void initLooks() {
    rectangle.setStrokeWidth(STROKE_WIDTH);
    rectangle.setFill(Color.LIGHTSKYBLUE);
    rectangle.setStroke(Color.BLACK);
  }
  
  public void setSelected(boolean selected) {
    if (selected) {
      rectangle.setStrokeWidth(2);
      setStroke(Constants.selected_color);
    }
    else {
      rectangle.setStrokeWidth(1);
      setStroke(Color.BLACK);
    }
  }
  
  public void setStrokeWidth(double scale) {
    rectangle.setStrokeWidth(scale);
  }
  
  public void setFill(Paint p) {
    rectangle.setFill(p);
  }
  
  public void setStroke(Paint p) {
    rectangle.setStroke(p);
  }
  
  public Bounds getBounds() {
    return rectangle.getBoundsInParent();
  }
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    
    super.propertyChange(evt);
    if (evt.getPropertyName().equals(Constants.changeNodeX)) {
      setX((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(Constants.changeNodeY)) {
      setY((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(Constants.changeNodeWidth)) {
      changeWidth((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(Constants.changeNodeHeight)) {
      changeHeight((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(Constants.changeNodeTitle)) {
      title.setText((String) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(Constants.changePortNodeDataType)) {
      dataType.setText((String) evt.getNewValue());
    }
      // } else if
      // (evt.getPropertyName().equals(Constants.changeClassNodeAttributes)) {
      // attributes.setText((String) evt.getNewValue());
      // } else if
      // (evt.getPropertyName().equals(Constants.changeClassNodeOperations)) {
      // operations.setText((String) evt.getNewValue());
  }
}
