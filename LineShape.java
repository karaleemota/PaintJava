package PaintMota;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LineShape extends Line {
	Canvas canvas;
	Color color;

	public LineShape(Canvas canvas, double startX, double startY, double endX, double endY) {
		super(startX, startY, endX, endY);
		this.canvas = canvas;

		// select this line as soon as its made
		color = canvas.toolBox.colorPicker.getValue();
		setStroke(Color.GRAY);
		setStrokeWidth(5);

		canvas.root.getChildren().add(this);
		canvas.toolBox.toFront();
	}
	
	public void setColor(Color c) {
		color = c;
	}
}
