module JavaFXOpenCv {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires opencv;
	requires java.desktop;
	requires javafx.swing;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
}
