package PaintMota;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Oval extends Ellipse {
	Canvas canvas;
	Text text;

	public Oval(Canvas canvas, double radiusX, double radiusY) {
		super(radiusX, radiusY);
		this.canvas = canvas;

		text = new Text();
		this.canvas = canvas;
		text = new Text(50, 50, "");
		text.setFont(new Font(18));
		text.setX(getRadiusX() + radiusX / 2);
		text.setY(getRadiusY() + radiusY / 2);

		// select this oval as soon as its made
		setStrokeType(StrokeType.OUTSIDE);
		setStroke(Color.GRAY);

		canvas.root.getChildren().add(this);
		canvas.root.getChildren().add(text);
		canvas.toolBox.toFront();
	}

	public void setText(String txt) {
		text.setText(txt);
	}

	public void setCenter(double x, double y) {
		setCenterX(x);
		setCenterY(y);
		text.setX(getCenterX());
		text.setY(getCenterY());
	}
}
