package PaintMota;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Rect extends Rectangle {
	Text text;
	Canvas canvas;

	public Rect(Canvas canvas, double width, double height) {
		super(width, height);
		this.canvas = canvas;
		text = new Text(50, 50, "");
		text.setFont(new Font(18));
		text.setX(getX() + width / 2);
		text.setY(getY() + height / 2);
		
		// select this rect as soon as its made
		setStrokeType(StrokeType.OUTSIDE);
		setStroke(Color.GRAY);
		
		canvas.root.getChildren().add(this);
		canvas.root.getChildren().add(text);
		canvas.toolBox.toFront();
	}

	public void setText(String txt) {
		text.setText(txt);
	}

	public void setCoords(double x, double y) {
		setX(x);
		setY(y);
		text.setX(getX() + getWidth() / 2);
		text.setY(getY() + getHeight() / 2);
	}
}
