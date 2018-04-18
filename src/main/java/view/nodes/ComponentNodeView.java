package view.nodes;

import view.nodes.PortNodeView;
import model.nodes.ClassNode;
import model.nodes.ComponentNode;
import model.nodes.PortNode;
import model.nodes.AbstractNode;
import util.Constants;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

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
  
  // Shape C = Shape.union(A, B);
  // Wir erzeugen ein Array von Ports, sorgen dafuer, dass die alle vernuenftig
  // ausschauen als Rechtecke
  // dann erzeugen wir das Rechteck fuer die Component
  // dann rufen wir UNION darauf auf und erhalten eine hoffentlich passende
  // Komponente
  // Problem koennte sein, dass die Linien nicht mehr gezeigt werden
  
  // dann koennen wir auch eine Klasse PortNodeView erzeugen. Die kann dann fast
  // das gleiche wie die ComponentView,
  // nur das in der ComponentView der Union stattfindet
  // private Label title;
  // private Label attributes;
  // private Label operations;
  
  private Label title;
  
  private Rectangle rectangle;
  private ArrayList<Rectangle> portViewArray;
  
  // private StackPane container;
  private GridPane container;
  private Pane titlePane;
  // private StackPane titlePane;
  private VBox vbox;
  private Pane PortLeft;
  private Pane PortTop;
  private Pane PortBottom;
  private Pane PortRight;
  
  private final int STROKE_WIDTH = 1;
  
  protected static final double PORT_WIDTH = 40; 
  protected static final double PORT_HEIGHT = 40;
  
  public ComponentNodeView(ComponentNode node) {
    super(node);
    // setChangeListeners();
    System.out.println("Wir sind in ComponentNodeViewConstructor");
    // container = new StackPane();
    container = new GridPane();
    System.out.println("Ports " + node.getPorts());
    if (!node.getPorts().isEmpty()) {
      initGrid(node.getHeight(), node.getWidth(), PORT_HEIGHT, PORT_WIDTH);
    }
    else {
      System.out.println("We have just a rectangle");
      PortNode p = new PortNode(0,0,0,0);
      initGrid(node.getHeight(), node.getWidth(), PORT_HEIGHT, PORT_WIDTH);
    }
    vbox = new VBox();
    initVBox(node);
//    container.add(titlePane, 3, 3);
    // Jetzt muessen wir ueber die aeusseren beiden Grids je eine Pane legen (eine
    // klassische),
    // um die Ports an den richtigen Stellen anordnen zu koennen
    PortLeft = new Pane();
    PortRight = new Pane();
    PortTop = new Pane();
    PortBottom = new Pane();
    // Rectangle recPort = new
    // Rectangle(node.getPorts().get(0).getX(),node.getPorts().get(0).getY(),
    // node.getPorts().get(0).getWidth(),node.getPorts().get(0).getHeight());
    // recPort.setStrokeWidth(STROKE_WIDTH);
    // recPort.setFill(Color.LIGHTSKYBLUE);
    // recPort.setStroke(Color.BLACK);
    createRectangles(node);
    createRectanglesPort(node);
    // Rectangle recPort = new Rectangle(0,node.getPorts().get(0).getY() -
    // node.getY(), 40,40);
    // recPort.setStrokeWidth(STROKE_WIDTH);
    // recPort.setFill(Color.LIGHTSKYBLUE);
    // recPort.setStroke(Color.BLACK);
    //
    // PortLeft.getChildren().add(recPort);
    
    container.add(PortLeft, 0, 0, 1, 1);
    container.add(PortTop, 0, 0, 4, 1);
    container.add(PortBottom, 0, 3, 4, 1);
    container.add(PortRight, 3, 0, 1, 4);
    // Rectangle recPort = new Rectangle(40,40);
    // recPort.setStrokeWidth(STROKE_WIDTH);
    // recPort.setFill(Color.LIGHTSKYBLUE);
    // recPort.setStroke(Color.BLACK);
    // container.add(recPort, 0, 2);
    
    // // pane = new Pane();
    //// container.getChildren().addAll(rectangle, vbox);
    
    // portViewArray = new ArrayList<>();
    // for (PortNode p : node.getPorts()) {
    // Rectangle rec = new
    // Rectangle(p.getX(),p.getY(),p.getWidth(),p.getHeight());
    // System.out.println("Rec" + rec);
    // //rec = createRectanglePort(p);
    //// Line top = new Line();
    //// top.startXProperty().bind(p.getWidth());
    // portViewArray.add(rec);
    // container.add(rec,4,4,1,0);
    //
    // }
    //// container.getChildren().addAll(rectangle, vbox);
    //// container.getChildren().add(pane);
    //// container.getChildren().add(new Rectangle(100,100,Color.BLUE));
    //
    //// container.getChildren().addAll( vbox);
    //// container.getChildren().addAll(rectangle, vbox);
    //
    //
    //// container.getChildren().addAll(RecUnion, vbox);
    //
    //
    // initVBox();
    // createRectangles();
    // container.add(rectangle, 3, 3);
    // container.add(vbox, 3, 3);
    container.setGridLinesVisible(true);
    // // createRectanglesPort();
    //// changeHeight(node.getHeight());
    //// changeWidth(node.getWidth());
    //// System.out.println("Rectangle: " + rectangle);
    //// System.out.println("Rectangle2: " + rectangle2);
    //// der Union Befehl richtet sich komisch aus
    //// RecUnion = Shape.union((Shape)rectangle, (Shape)rectangle2);
    //// RecUnion = Shape.intersect((Shape)rectangle, (Shape)rectangle2);
    //
    //// System.out.println("RecUnion: " + RecUnion);
    //
    // initLooks();
    //
    //
    //
    container.add(vbox, 2, 1, 1, 1);
    this.getChildren().add(container);
    
    this.setTranslateX(node.getTranslateX());
    this.setTranslateY(node.getTranslateY());
    
  }
  
  
  
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
    RowConstraints row5 = new RowConstraints(portHeight / 2);
    container.getColumnConstraints().addAll(column1, column2, column3, column4, column5);
    container.getRowConstraints().addAll(row1, row2, row3, row4, row5);
  }
  
  private void createRectangles(ComponentNode node) {
    Rectangle rec = new Rectangle(node.getWidth(), node.getHeight());
    initLooks(rec);
    container.add(rec, 1, 1, 3, 3);
  }
  
  private void createRectanglesPort(ComponentNode node) {
    ArrayList<PortNode> Ports = node.getPorts();
    if(!node.getPorts().isEmpty()) {
      System.out.println("Node infos: getX(), getY()"+ node.getX() + node.getY() );
      for (PortNode p : Ports) {
        System.out.println("Port: " + p.getX() + " " + p.getY());
        System.out.println("Vergleich");
        System.out.println(p.getX() + p.getWidth());
        System.out.println(p.getY() + p.getHeight());
        System.out.println(node.getX() + 3 / 5 * node.getWidth());
        System.out.println(node.getY() + 3 / 5 * node.getHeight());
        System.out.println(p.getY());
        System.out.println(node.getY());
        
        if (p.getX() < node.getX()) {
          Rectangle recPort = new Rectangle(0, p.getY() - node.getY(), PORT_HEIGHT, PORT_WIDTH);
          initLooks(recPort);
          PortLeft.getChildren().add(recPort);
          System.out.println("Left:" + recPort);
        }
        else if (p.getY() < node.getY()) {
          Rectangle recPort = new Rectangle(p.getX() - node.getX(), 0, PORT_HEIGHT, PORT_WIDTH);
          initLooks(recPort);
          PortTop.getChildren().add(recPort);
          System.out.println("Top:" + recPort);
        }
        else if ((p.getX() + p.getWidth() > node.getX()) && (p.getY() + p.getHeight() < node.getY() + node.getHeight()) && (p.getY() > node.getY())) {
          Rectangle recPort = new Rectangle(0 , p.getY() - node.getY(), PORT_HEIGHT, PORT_WIDTH);
          initLooks(recPort);
          PortRight.getChildren().add(recPort);
          System.out.println("Right:" + recPort);
        }
        else {
          Rectangle recPort = new Rectangle(p.getX() - node.getX(), 0, PORT_HEIGHT, PORT_WIDTH);
          initLooks(recPort);
          PortBottom.getChildren().add(recPort);
          System.out.println("Bottom:" + recPort);
        }
      }
    }
  }
  
  private void changeHeight(double height) {
    setHeight(height);
    rectangle.setHeight(height);
  }
  
  private void changeHeightPort(double height, Rectangle rec) {
    rec.setHeight(height);
    setHeight(height);
  }
  
  private void changeWidth(double width) {
    // setWidth(width);
    rectangle.setWidth(width);
    // container.setMaxWidth(width);
    // container.setPrefWidth(width);
    
    // vbox.setMaxWidth(width);
    // vbox.setPrefWidth(width);
    //
    title.setMaxWidth(width); 
    title.setPrefWidth(width);
    
  }
  
  private void changeWidthPort(double width, Rectangle rec) {
    // setWidth(width);
    rec.setWidth(width);
    
    // container.setMaxWidth(width);
    // container.setPrefWidth(width);
    //
    // vbox.setMaxWidth(width);
    // vbox.setPrefWidth(width);
    
    /*
     * title.setMaxWidth(width); title.setPrefWidth(width);
     * attributes.setMaxWidth(width); attributes.setPrefWidth(width);
     * operations.setMaxWidth(width); operations.setPrefWidth(width);
     */
  }
  
  private void initVBox(ComponentNode node) {
    vbox.setPadding(new Insets(5, 0, 5, 0));
    vbox.setSpacing(5);
    
    titlePane = new Pane();
    
    title = new Label();
    title.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
    System.out.println("We are going to set a title" + node.getTitle());
    if(node.getTitle() != null) {
      title.setText(node.getTitle());
    }
    title.setAlignment(Pos.CENTER);
    
    
//    titlePane.getChildren().add(title);
//    
//    System.out.println("TitlePane looks as follows" + titlePane);
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.getChildren().add(title);
  }
  
  private void initLooks(Rectangle rec) {
    rec.setStrokeWidth(STROKE_WIDTH);
    rec.setFill(Color.LIGHTSKYBLUE);
    rec.setStroke(Color.BLACK);
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
    for (Rectangle rec : portViewArray) {
      rec.setStrokeWidth(scale);
    }
  }
  
  public void setFill(Paint p) {
    rectangle.setFill(p);
    for (Rectangle rec : portViewArray) {
      rec.setFill(p);
    }
  }
  
  public void setStroke(Paint p) {
    rectangle.setStroke(p);
    for (Rectangle rec : portViewArray) {
      rec.setStroke(p);
    }
  }
  
  public Bounds getBounds() {
    return container.getBoundsInParent();
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
      // } else if (evt.getPropertyName().equals(Constants.changeNodeTitle)) {
      // title.setText((String) evt.getNewValue());
      // } else if
      // (evt.getPropertyName().equals(Constants.changeClassNodeAttributes)) {
      // attributes.setText((String) evt.getNewValue());
      // } else if
      // (evt.getPropertyName().equals(Constants.changeClassNodeOperations)) {
      // operations.setText((String) evt.getNewValue());
    }
  }
 
  
}
