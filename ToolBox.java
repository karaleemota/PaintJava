package PaintMota;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ToolBox extends FlowPane {
	final int width = 105;
	final int buttonHeight = 35;
	Canvas canvas;
	// tells which button was last pressed, what action we should perform
	/*
	 * 0 = rect 1 = oval 2 = line 3 = select -1 = no drawing mode selected (other)
	 */
	int mode;
	Button rectBtn;
	Button ovalBtn;
	Button lineBtn;
	Button selectBtn;
	Button saveBtn;
	Button loadBtn;
	Button deleteBtn;
	ColorPicker colorPicker;
	TextField textField;

	public ToolBox(Canvas canvas) {
		this.canvas = canvas;
		mode = 0;
		setPrefWidth(width);
		setPrefHeight(Canvas.canvasHeight);

		rectBtn = new Button("Rect");
		rectBtn.setPrefSize(width, buttonHeight);
		rectBtn.setOnAction(e -> {
			mode = 0;
			canvas.selectShapeObject(null);
		});
		getChildren().add(rectBtn);

		ovalBtn = new Button("Oval");
		ovalBtn.setPrefSize(width, buttonHeight);
		ovalBtn.setOnAction(e -> {
			mode = 1;
			canvas.selectShapeObject(null);
		});
		getChildren().add(ovalBtn);

		lineBtn = new Button("Line");
		lineBtn.setPrefSize(width, buttonHeight);
		lineBtn.setOnAction(e -> {
			mode = 2;
			canvas.selectShapeObject(null);
		});
		getChildren().add(lineBtn);

		selectBtn = new Button("Select");
		selectBtn.setPrefSize(width, buttonHeight);
		selectBtn.setOnAction(e -> {
			mode = 3;
			canvas.selectShapeObject(null);
		});
		getChildren().add(selectBtn);

		saveBtn = new Button("SAVE");
		saveBtn.setPrefSize(width, buttonHeight);
		saveBtn.setOnAction(e -> {
			saveAction();
		});
		getChildren().add(saveBtn);

		loadBtn = new Button("LOAD");
		loadBtn.setPrefSize(width, buttonHeight);
		loadBtn.setOnAction(e -> {
			openAction();
		});
		getChildren().add(loadBtn);

		deleteBtn = new Button("DELETE");
		deleteBtn.setPrefSize(width, buttonHeight);
		deleteBtn.setOnAction(e -> {
			mode = -1;
			deleteAction();
		});
		getChildren().add(deleteBtn);

		colorPicker = new ColorPicker();
		colorPicker.setPrefSize(width, buttonHeight);
		colorPicker.setOnAction((ActionEvent e) -> {
			colorPickerAction();
		});
		getChildren().add(colorPicker);

		textField = new TextField();
		textField.setPrefSize(width, buttonHeight);
		textField.setOnAction(e -> {
			textAction();
		});
		getChildren().add(textField);
	}

	public void colorPickerAction() {
		Shape shape = canvas.selectedShape;
		if (shape != null) {
			if (shape instanceof Line) {
				// change line color
				shape.setStroke(colorPicker.getValue());
				((LineShape) shape).setColor(colorPicker.getValue());
			} else {
				shape.setFill(colorPicker.getValue());
			}
		}
	}

	// when user enters text field, the selected shape will contain that text
	public void textAction() {
		Shape shape = canvas.selectedShape;
		if (shape instanceof Rect) {
			((Rect) shape).setText(textField.getText());
		} else if (shape instanceof Oval) {
			((Oval) shape).setText(textField.getText());
		}
	}

	public void deleteAction() {
		if (canvas.selectedShape != null) {
			if(canvas.selectedShape instanceof Rect) {
				canvas.root.getChildren().remove(((Rect)canvas.selectedShape).text);
			} else if(canvas.selectedShape instanceof Oval) {
				canvas.root.getChildren().remove(((Oval)canvas.selectedShape).text);
			}
			canvas.root.getChildren().remove(canvas.selectedShape);
			canvas.shapes.remove(canvas.selectedShape);
			canvas.selectedShape = null;
		}
	}

	public void saveAction() {
		// select a file name
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose File to Save");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter(".txt Files", "*.txt"));
		File selectedFile = fileChooser.showSaveDialog(null);
		if (selectedFile != null) {
			// dialog closed by selecting a file to save the data to
			// write data here yourself, e.g.
			try {
				PrintWriter writer = new PrintWriter(selectedFile.getAbsolutePath(), "UTF-8");
				// iterate thru all shapes and add to a txt file
				for (Shape shape : canvas.shapes) {
					if (shape instanceof Rect) {
						Rect rect = (Rect) shape;
						// shape x y width height r g b text
						String info = "rect " + rect.getX() + " " + rect.getY() + " " + rect.getWidth() + " "
								+ rect.getHeight() + " " + ((Color) (rect.getFill())).getRed() + " "
								+ ((Color) (rect.getFill())).getGreen() + " " + ((Color) (rect.getFill())).getBlue()
								+ " " + rect.text.getText();
						writer.println(info);
					} else if (shape instanceof Oval) {
						Oval oval = (Oval) shape;
						// shape x y xradius y radius r g b text
						String info = "oval " + oval.getCenterX() + " " + oval.getCenterY() + " " + oval.getRadiusX()
								+ " " + oval.getRadiusY() + " " + ((Color) (oval.getFill())).getRed() + " "
								+ ((Color) (oval.getFill())).getGreen() + " " + ((Color) (oval.getFill())).getBlue()
								+ " " + oval.text.getText();
						writer.println(info);
					} else if (shape instanceof LineShape) {
						LineShape line = (LineShape) shape;
						// shape startx starty endx endy r g b
						String info = "line " + line.getStartX() + " " + line.getStartY() + " " + line.getEndX() + " "
								+ line.getEndY() + " " + ((Color) (line.color)).getRed() + " "
								+ ((Color) (line.color)).getGreen() + " " + ((Color) (line.color)).getBlue();
						writer.println(info);
					}
				}
				writer.close();
			} catch (UnsupportedEncodingException e) {
			} catch (FileNotFoundException e) {
			}

		}
	}

	public void openAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose File to Open");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter(".txt Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()));
				String line = reader.readLine();
				// remove everything from canvas
				for (Shape child : canvas.shapes) {
					if(child instanceof Rect) {
						canvas.root.getChildren().remove(((Rect)child).text);
					} else if(child instanceof Oval) {
						canvas.root.getChildren().remove(((Oval)child).text);
					}
					canvas.root.getChildren().remove(child);
				}
				canvas.shapes.clear();
				canvas.selectedShape = null;
				while (line != null) {
					String[] data = line.split(" ");
					String type = data[0];
					double x = Double.parseDouble(data[1]);
					double y = Double.parseDouble(data[2]);
					double width = Double.parseDouble(data[3]);
					double height = Double.parseDouble(data[4]);
					double r = Double.parseDouble(data[5]);
					double g = Double.parseDouble(data[6]);
					double b = Double.parseDouble(data[7]);
					String text = (data.length == 9) ? data[8] : "";
					if (type.equals("rect")) {
						// make a rectangle
						Rect rect = new Rect(this.canvas, width, height);
						rect.setCoords(x,y);
						rect.setFill(new Color(r, g, b, 1));
						rect.setText(text);
						canvas.shapes.add(rect);
					} else if (type.equals("oval")) {
						// mkae oval
						Oval oval = new Oval(canvas, width, height);
						oval.setCenter(x, y);
						oval.setFill(new Color(r, g, b, 1));
						oval.setText(text);
						canvas.shapes.add(oval);
					} else if (type.equals("line")) {
						LineShape lineShape = new LineShape(canvas, x, y, width, height);
						lineShape.setColor(new Color(r, g, b, 1));
						lineShape.setStroke(new Color(r, g, b, 1));
						canvas.shapes.add(lineShape);
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				System.out.println(e);
			}

		}
	}
}
