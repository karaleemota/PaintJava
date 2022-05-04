package PaintMota;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Canvas extends Application {
	final static int canvasWidth = 800;
	final static int canvasHeight = 600;
	ToolBox toolBox;
	Pane root;

	List<Shape> shapes;
	Point[] corners = new Point[2];
	Rect rect;
	Oval oval;
	LineShape line;
	Shape selectedShape;
	boolean mouseIsPressed = false;
	// to keep track of movement of dragging shape
	double selectedShapeX = 0;
	double selectedShapeY = 0;
	// only for moving lines
	double selectedShapeXLineEnd = 0;
	double selectedShapeYLineEnd = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Paint");
		root = new Pane();
		Scene scene = new Scene(root, canvasWidth, canvasHeight);
		stage.setScene(scene);
		stage.show();
		shapes = new ArrayList<Shape>();

		toolBox = new ToolBox(this);
		toolBox.setLayoutX(0);
		toolBox.setLayoutY(0);
		root.getChildren().add(toolBox);

		root.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent m) -> {
			// if select mode is on select a shape
			if (toolBox.mode == 3) {
				selectShapeMouse(m);
				m.getX();
			}
		});
		// mouse pressed makes the box
		root.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent m) -> {
			mouseIsPressed = true;
			corners[0] = new Point(m);
			if (toolBox.mode == 0) {
				// rectangle
				rect = new Rect(this, 10, 10);
				rect.setFill(toolBox.colorPicker.getValue());
				rect.setCoords(corners[0].getX(), corners[0].getY());
				shapes.add(rect);
				selectShapeObject(rect);
			} else if (toolBox.mode == 1) {
				// oval
				oval = new Oval(this, 10, 10);
				oval.setFill(toolBox.colorPicker.getValue());
				oval.setCenter(corners[0].getX(), corners[0].getY());
				shapes.add(oval);
				selectShapeObject(oval);
			} else if (toolBox.mode == 2) {
				line = new LineShape(this, corners[0].getX(), corners[0].getY(), corners[0].getX(), corners[0].getY());
				line.setStroke(toolBox.colorPicker.getValue());
				line.setColor(toolBox.colorPicker.getValue());
				shapes.add(line);
				selectShapeObject(line);
			} else if (toolBox.mode == 3) {
				// select the shape and allow dragging it
				selectShapeMouse(m);
			}
		});

		// mouse released does nothing (except note that mouse is up)
		root.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent m) -> {
			mouseIsPressed = false;
		});

		root.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent m) -> {
			corners[1] = new Point(m);
			if (toolBox.mode == 0) {
				double xdif = corners[0].xdif(corners[1]);
				double ydif = corners[0].ydif(corners[1]);
				rect.setWidth(xdif);
				rect.setHeight(ydif);
				// put these lines back in to get tracking in all 4 quadrants
				rect.setCoords(min(corners[0].getX(), corners[1].getX()), min(corners[0].getY(), corners[1].getY()));
			} else if (toolBox.mode == 1) {
				double xdif = corners[0].xdif(corners[1]);
				double ydif = corners[0].ydif(corners[1]);
				oval.setRadiusX(xdif);
				oval.setRadiusY(ydif);
				// put these lines back in to get tracking in all 4 quadrants
				oval.setCenter(min(corners[0].getX(), corners[1].getX()), min(corners[0].getY(), corners[1].getY()));
			} else if (toolBox.mode == 2) {
				line.setEndX(corners[1].getX());
				line.setEndY(corners[1].getY());
			} else if (toolBox.mode == 3) {
				// drag the selected shape
					if (selectedShape instanceof Rect) {
						Rect shape = (Rect) selectedShape;
						shape.setCoords(selectedShapeX + corners[1].getX() - corners[0].getX(),
								selectedShapeY + corners[1].getY() - corners[0].getY());
					} else if (selectedShape instanceof Oval) {
						Oval shape = (Oval) selectedShape;
						shape.setCenter(selectedShapeX + corners[1].getX() - corners[0].getX(),
								selectedShapeY + corners[1].getY() - corners[0].getY());
					} else if (selectedShape instanceof Line) {
						((Line) selectedShape).setStartX(selectedShapeX + corners[1].getX() - corners[0].getX());
						((Line) selectedShape).setStartY(selectedShapeY + corners[1].getY() - corners[0].getY());
						((Line) selectedShape).setEndX(selectedShapeXLineEnd + corners[1].getX() - corners[0].getX());
						((Line) selectedShape).setEndY(selectedShapeYLineEnd + corners[1].getY() - corners[0].getY());
					}
			}
		});
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				KeyCode code = ke.getCode();
				if(code == KeyCode.DELETE) {
					toolBox.deleteAction();
				}
			}
		});

	}

	public double min(double x, double y) {
		return (x < y) ? x : y;
	}

	public void selectShapeMouse(MouseEvent m) {
		// find most recent shape that overlaps with these coords
		m.consume();
		// check if clicked on shape
		selectShapeObject(m.getTarget());
	}

	public void selectShapeObject(Object shape) {
		selectShapeHelper(shape);
	}

	public void selectShapeHelper(Object shape) {
		// un highlight old selected shape
		if (selectedShape instanceof Line) {
			selectedShape.setStroke(((LineShape)selectedShape).color);
		} else if (selectedShape instanceof Rect || selectedShape instanceof Oval) {
			selectedShape.setStrokeWidth(0);
		}
		// object clicked is not a shape
		if (!(shape instanceof Shape)) {
			selectedShape = null;
			return;
		}

		// we have selected a shape
		selectedShape = (Shape) shape;
		// update color picker
		if (selectedShape instanceof Line) {
			selectedShape.setStroke(Color.GRAY);
			toolBox.colorPicker.setValue((Color) ((LineShape)selectedShape).color);
			selectedShapeX = ((Line) selectedShape).getStartX();
			selectedShapeY = ((Line) selectedShape).getStartY();
			selectedShapeXLineEnd = ((Line) selectedShape).getEndX();
			selectedShapeYLineEnd = ((Line) selectedShape).getEndY();
		} else if (selectedShape instanceof Rect || selectedShape instanceof Oval) {
			// is rect or oval
			toolBox.colorPicker.setValue((Color) selectedShape.getFill());
			selectedShape.setStrokeWidth(4); // highlight selected box with border
			if (selectedShape instanceof Rect) {
				selectedShapeX = ((Rect) selectedShape).getX();
				selectedShapeY = ((Rect) selectedShape).getY();
			} else if (selectedShape instanceof Oval) {
				selectedShapeX = ((Oval) selectedShape).getCenterX();
				selectedShapeY = ((Oval) selectedShape).getCenterY();
			}
		}
	}
}
