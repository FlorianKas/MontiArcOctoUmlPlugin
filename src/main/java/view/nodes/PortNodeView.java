package view.nodes;

import model.nodes.PortNode;
import util.Constants;

import java.beans.PropertyChangeEvent;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class PortNodeView extends AbstractNodeView {
  private Rectangle rectangle;
  private final int STROKE_WIDTH = 1;
  // private StackPane container;
  // private VBox vbox;
  
  public PortNodeView(PortNode node) {
    super(node);
    // setChangeListeners();
    
    // container = new StackPane();
    rectangle = new Rectangle();
    // vermutlich brauche ich das f�r Ports auch nicht
    // vbox = new VBox();
    // vermutlich brauche ich das f�r Ports nicht
    // container.getChildren().addAll(rectangle, vbox);
    
    // initVBox();
    createRectangles();
    changeHeight(node.getHeight());
    changeWidth(node.getWidth());
    initLooks();
    
    this.setTranslateX(node.getTranslateX());
    this.setTranslateY(node.getTranslateY());
    
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
    // container.setMaxWidth(width);
    // container.setPrefWidth(width);
    
    // vbox.setMaxWidth(width);
    // vbox.setPrefWidth(width);
    
  }
  
  private void initLooks() {
    rectangle.setStrokeWidth(STROKE_WIDTH);
    rectangle.setFill(Color.LIGHTSKYBLUE);
    rectangle.setStroke(Color.BLACK);
    // StackPane.setAlignment(title, Pos.CENTER);
    // VBox.setMargin(attributes, new Insets(5,0,0,5));
    // VBox.setMargin(operations, new Insets(5,0,0,5));
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
    // noch keine Ahnung, ob das hier richtig ist.
    return rectangle.getBoundsInParent();
  }
  
}
