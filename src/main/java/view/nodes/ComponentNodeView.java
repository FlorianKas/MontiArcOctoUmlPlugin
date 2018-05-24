package view.nodes;

import view.nodes.PortNodeView;
import model.nodes.ClassNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import model.nodes.AbstractNode;
import util.ConstantsMonti;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ComponentNodeView extends AbstractNodeView {
  // Visuell Representation of a ComponentNode
  
  private Label type;
  private Label name;
  private Label stereotype;
  
  private Rectangle rectangle;
  private ArrayList<Rectangle> portViewArray;
  
  private GridPane container;
  private VBox vbox;
  private Pane PortLeft;
//  private Pane PortTop;
//  private Pane PortBottom;
  private Pane PortRight;
  
  private final int STROKE_WIDTH = 1;
  
  protected static final double PORT_WIDTH = 40; 
  protected static final double PORT_HEIGHT = 40;
  HashMap<PortNodeView, PortNode> nodeCompMap = new HashMap<>();
  ArrayList<PortNodeView> portViews;
  HashMap<PortNode, PortNodeView> nodeToViewMap = new HashMap<>();
  
  public ComponentNodeView(ComponentNode node) {
    super(node);
    // setChangeListeners();
    container = new GridPane();
    vbox = new VBox();
    
    System.out.println("Wir sind in ComponentNodeViewConstructor");
    System.out.println("Ports " + node.getPorts());
    
    initGrid(node.getHeight(), node.getWidth(), PORT_HEIGHT, PORT_WIDTH);
    createRectangles(node);
    
    initVBox(node);
    
    // For ports choose normal panes to put them at correct position
    PortLeft = new Pane();
    PortRight = new Pane();
//    PortTop = new Pane();
//    PortBottom = new Pane();
    
//    createRectangles(node);
    createRectanglesPort(node);

    container.add(vbox, 2, 1, 1, 1);
    container.add(PortLeft, 0, 0, 1, 2);
    container.add(PortRight, 3, 0, 1, 2);
    
//    container.add(vbox, 2, 1, 1, 1);
//    container.add(PortLeft, 0, 0, 1, 1);
//    container.add(PortTop, 0, 0, 4, 1);
//    container.add(PortBottom, 0, 3, 4, 1);
//    container.add(PortRight, 3, 0, 1, 4);
    
    
    changeHeight(node.getHeight());
    changeWidth(node.getWidth());
    container.setGridLinesVisible(true);
    System.out.println("Container Input " + container.getChildren().toString());
    this.getChildren().add(container);
    System.out.println("Children " + this.getChildren().toString());
    
    this.setTranslateX(node.getTranslateX());
    this.setTranslateY(node.getTranslateY());
    
  }
  
//  public ComponentNodeView() {
//    
//  }
    
  
  
  private void initGrid(double compHeight, double compWidth, double portHeight, double portWidth) {
    ColumnConstraints column1 = new ColumnConstraints(portWidth / 2);
    ColumnConstraints column2 = new ColumnConstraints(portWidth / 2);
    ColumnConstraints column3 = new ColumnConstraints(compWidth - portWidth);
    ColumnConstraints column4 = new ColumnConstraints(portWidth / 2);
    ColumnConstraints column5 = new ColumnConstraints(portWidth / 2);
    RowConstraints row1 = new RowConstraints(portHeight / 2);
    RowConstraints row2 = new RowConstraints(portHeight / 2);
    RowConstraints row3 = new RowConstraints(compHeight - portWidth);
    RowConstraints row4 = new RowConstraints(portHeight / 2);
//    RowConstraints row5 = new RowConstraints(portHeight / 2);
    container.getColumnConstraints().addAll(column1, column2, column3, column4, column5);
//    container.getRowConstraints().addAll(row1, row2, row3, row4, row5);
    container.getRowConstraints().addAll(row2, row3, row4);
     
  }
  
  private void createRectangles(ComponentNode node) {
    rectangle = new Rectangle(node.getX(), node.getY(), node.getWidth(), node.getHeight());
    initLooks();
//    container.add(rec, 1, 1, 3, 3);
    container.add(rectangle, 1, 1, 3, 1);
  }
  
  private void createRectanglesPort(ComponentNode node) {
    nodeCompMap = new HashMap();
    portViews = new ArrayList();
    ArrayList<PortNode> Ports = node.getPorts();
    Label titlePort = new Label();
    Label DataTypePort = new Label();
    if(!node.getPorts().isEmpty()) {
      System.out.println("Node infos: getX(), getY()"+ node.getX() + node.getY() );
      
      for (PortNode p : Ports) {
        PortNodeView portView = new PortNodeView(p);
        System.out.println("Port: " + p.getXDraw() + " " + p.getYDraw());
        System.out.println("Vergleich");
        System.out.println(p.getXDraw() + p.getWidth());
        System.out.println(p.getYDraw() + p.getHeight());
        System.out.println(node.getX() + 3 / 5 * node.getWidth());
        System.out.println(node.getY() + 3 / 5 * node.getHeight());
        System.out.println(p.getYDraw());
        System.out.println(node.getY());
        
//        if (p.getX() + p.getWidth() < node.getX() + node.getWidth() && p.getX() < node.getX()) {
        if (Math.abs(node.getX() - p.getX()) < Math.abs(node.getX() + node.getWidth() - p.getX())) {
//          PortNodeView portView = new PortNodeView(p);
          System.out.println("LeftPane");
          System.out.println("yDraw" + p.getYDraw());
          Pane portPane = portView.createPortPane(0, p.getYDraw() - node.getY(), PORT_HEIGHT, PORT_WIDTH);
          PortLeft.getChildren().add(portPane);
          
          // set PortName
          titlePort = portView.setPortTitle(p.getTitle(), 2, p.getYDraw() - node.getY() +10);
          PortLeft.getChildren().add(titlePort);
          // set PortDataType
          DataTypePort = portView.setPortDataType(p.getPortType(), 2, p.getYDraw() - node.getY()-12);
          PortLeft.getChildren().add(DataTypePort);
        }
        /*else if (p.getY() < node.getY()) {
//          PortNodeView portView = new PortNodeView(p);
          System.out.println("We are in ComponentNodeView Top");
          Pane portPane = portView.createPortPane(p.getXDraw() - node.getX(), 0, PORT_HEIGHT, PORT_WIDTH);
          PortTop.getChildren().add(portPane);
          
          // set PortName
          titlePort = portView.setPortTitle(p.getTitle(), p.getXDraw() - node.getX() + 2, 10);
          PortTop.getChildren().add(titlePort);
          // set PortDataType
          DataTypePort = portView.setPortDataType(p.getPortType(), p.getXDraw() - node.getX() + 2, -12);
          PortTop.getChildren().add(DataTypePort);
        }*/
//        else if ((p.getX() + p.getWidth() > node.getX()) && (p.getY() + p.getHeight() < node.getY() + node.getHeight()) && (p.getYDraw() > node.getY())) {
//          PortNodeView portView = new PortNodeView(p);
        else {  
          portView.setX(portView.getX());
          Pane portPane = portView.createPortPane(0 , p.getYDraw() - node.getY(), PORT_HEIGHT, PORT_WIDTH);
          System.out.println("PortPane " + portPane.getBoundsInParent());
          PortRight.getChildren().add(portPane);
          // set PortName
          titlePort = portView.setPortTitle(p.getTitle(),2,p.getYDraw() - node.getY() +10 );
          PortRight.getChildren().add(titlePort);
          // set PortDataType
          DataTypePort = portView.setPortDataType(p.getPortType(), 2, p.getYDraw() - node.getY()-12);
          PortRight.getChildren().add(DataTypePort);
          
        }
        /*else {
//          PortNodeView portView = new PortNodeView(p);
          System.out.println("ComponentNodeView Bottom");
          Pane portPane = portView.createPortPane(p.getXDraw() - node.getX(), 0, PORT_HEIGHT, PORT_WIDTH);
          PortBottom.getChildren().add(portPane);
          
          // set PortName
          titlePort = portView.setPortTitle(p.getTitle(), p.getXDraw() - node.getX() + 2, 10);
          PortBottom.getChildren().add(titlePort);
          // set PortDataType
          DataTypePort = portView.setPortDataType(p.getPortType(), p.getXDraw() - node.getX() + 2, -12);
          PortBottom.getChildren().add(DataTypePort);
          
        }*/
        nodeCompMap.put(portView, p);
        nodeToViewMap.put(p, portView);
        portViews.add(portView);
      }
    }
  }
  
//  private Label setPortAttr(String attr, double PositionX, double PositionY) {
//    Label portLabel = new Label();
//    portLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
//    System.out.println("We are going to set a title" + attr);
//    if(attr != null) {
//      portLabel.setText(attr);
//    }
//    portLabel.setAlignment(Pos.TOP_CENTER);
//    portLabel.setTranslateX(PositionX);
//    portLabel.setTranslateY(PositionY);
//    portLabel.setFont(new Font("Arial", 10));
//    return portLabel;
//  }
  
  private void changeHeight(double height) {
    setHeight(height);
    rectangle.setHeight(height);
  }
  
  private void changeHeightPort(double height, Rectangle rec) {
    rec.setHeight(height);
    setHeight(height);
  }
  
  private void changeWidth(double width) {
    setWidth(width);
    rectangle.setWidth(width);
    container.setMaxWidth(width);
    container.setPrefWidth(width);
    
    vbox.setMaxWidth(width);
    vbox.setPrefWidth(width);
    System.out.println("Width is " + width);
    System.out.println("type" + type);
    
    name.setMaxWidth(width); 
    name.setPrefWidth(width);
    
    stereotype.setMaxWidth(width); 
    stereotype.setPrefWidth(width);
    
    type.setMaxWidth(width); 
    type.setPrefWidth(width);
    
  }
  
  private void changeWidthPort(double width, Rectangle rec) {
    // setWidth(width);
    rec.setWidth(width);
    
    /*
     * title.setMaxWidth(width); title.setPrefWidth(width);
     * attributes.setMaxWidth(width); attributes.setPrefWidth(width);
     * operations.setMaxWidth(width); operations.setPrefWidth(width);
     */
  }
  
  private void initVBox(ComponentNode node) {
    type = new Label();
    name = new Label();
    stereotype = new Label();
    
    vbox.setPadding(new Insets(0, 20, 0, 0));
    vbox.setSpacing(5);
    
    type.setFont(Font.font("Verdana", FontWeight.LIGHT, 10));
    name.setFont(Font.font("Verdana", FontWeight.LIGHT, 10));
    stereotype.setFont(Font.font("Verdana", FontWeight.LIGHT, 10));
    System.out.println("We are going to set a title" + node.getComponentType());
    if(node.getComponentType() != null) {
      type.setText(node.getComponentType());
    }
    if(node.getTitle() != null) {
      name.setText(node.getTitle());
    }
    if(node.getStereotype() != null) {
      stereotype.setText(node.getStereotype());
    }
    type.setAlignment(Pos.TOP_CENTER);
    name.setAlignment(Pos.TOP_CENTER);
    stereotype.setAlignment(Pos.TOP_CENTER);
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.getChildren().addAll(stereotype, type, name);
  }
  
  private void initLooks() {
    rectangle.setStrokeWidth(STROKE_WIDTH);
    rectangle.setFill(Color.LIGHTSKYBLUE);
    rectangle.setStroke(Color.BLACK);
  }
  
  public void setSelected(boolean selected) {
    if (selected) {
      rectangle.setStrokeWidth(2);
      setStroke(ConstantsMonti.selected_color);
    }
    else {
      rectangle.setStrokeWidth(1);
      setStroke(Color.BLACK);
    }
  }
  
  public void setStrokeWidth(double scale) {
    rectangle.setStrokeWidth(scale);
    if (portViewArray != null) {
      for (Rectangle rec : portViewArray) {
        rec.setStrokeWidth(scale);
      }
    }
  }
  
  public void setFill(Paint p) {
    rectangle.setFill(p);
    if(!portViewArray.isEmpty()) {
      for (Rectangle rec : portViewArray) {
        rec.setFill(p);
      }
    }
  }
  
  public void setStroke(Paint p) {
    rectangle.setStroke(p);
    if(portViewArray != null) {
      for (Rectangle rec : portViewArray) {
        rec.setStroke(p);
      }
    }
  }
  
  public Bounds getBounds() {
    return container.getBoundsInParent();
  }
  
  public HashMap<PortNodeView, PortNode> getNodeCompMap() {
    return nodeCompMap;
  }
  
  public HashMap<PortNode, PortNodeView> getNodeToViewMap() {
    return nodeToViewMap;
  }
  
  public ArrayList<PortNodeView> getPortNodeViews() {
    return portViews;
  }
  
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    
    super.propertyChange(evt);
    if (evt.getPropertyName().equals(ConstantsMonti.changeNodeX)) {
      setX((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(ConstantsMonti.changeNodeY)) {
      setY((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(ConstantsMonti.changeNodeWidth)) {
      changeWidth((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(ConstantsMonti.changeNodeHeight)) {
      changeHeight((double) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(ConstantsMonti.changeNodeTitle)) {
      name.setText((String) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(ConstantsMonti.changeComponentNodeDataType)) {
      type.setText((String) evt.getNewValue());
    }
    else if (evt.getPropertyName().equals(ConstantsMonti.changeComponentStereotype)) {
      stereotype.setText((String) evt.getNewValue());
    }
  }
 
  
}
