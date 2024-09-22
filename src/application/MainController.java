/**
 * JavaFX - ProgrammingKnowledge
 */

package application;

import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import processing.RandomProcessing;

public class MainController {
	
	@FXML
	private Label label_maskImage;
	
	@FXML
	private Label label_originalImage;
	
	@FXML
	private ImageView originalImageView;
	
	@FXML
	private ImageView maskImageView;
	
	List<WritableImage> writableImageList = new ArrayList<>();
	RandomProcessing randomProcessing = new RandomProcessing();
	
	BufferedImage img = null;
	
	public void loadImage(ActionEvent event) throws IOException {
		
		final FileChooser filechooser = new FileChooser();
		filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg", "*.jpeg"));
		filechooser.setTitle("Open Image");
		File file = filechooser.showOpenDialog(new Stage());
		
		img = ImageIO.read(file);
		
		if (img != null) {
			display(img);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Image read Alert!");
			alert.setHeaderText(null);
			alert.setContentText("You need to choose an image file!");
		}
	}	
	
	public void applyFilter(){
		  BufferedImage grayImage = toGrayScale2(img);
		  WritableImage grayWritableImage = BufferedImage2WritableImage(grayImage);	
		  // Write tha image to the GUI using ImageView widget
		  maskImageView.setImage(grayWritableImage); 
		  label_maskImage.setText("Gray Image"); 
	  }
	
	public void applyGaussianBlur() {
		BufferedImage grayImage = toGrayScale2(img);
		BufferedImage blurImage = gaussianBlur(grayImage);
		WritableImage blurWritableImage = BufferedImage2WritableImage(blurImage);
		
		maskImageView.setImage(blurWritableImage); 
		label_maskImage.setText("GaussianBlur"); 
	}
	
	public void resizeImage() {
		BufferedImage grayImage = toGrayScale2(img);
		BufferedImage resizedImage = resizeGrayScaleImage(grayImage, 120);
		WritableImage resizedWritableImage = BufferedImage2WritableImage(resizedImage);
		
		maskImageView.setFitHeight(120);
		maskImageView.setFitWidth(120);
		maskImageView.setImage(resizedWritableImage);
		label_maskImage.setText("Resized Image: " + resizedImage.getWidth() + "x" + resizedImage.getHeight()); 
	}
	 
	// apply 2x2 pixelation to a grayscale image
	public void pixelate() {
		BufferedImage grayImage = toGrayScale2(img);
		BufferedImage pixImg = new BufferedImage(grayImage.getWidth(), grayImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0, p=0;
		for (int y=0; y<img.getHeight()-2; y+=2) {
			for (int x=0; x<img.getTileWidth()-2; x+=2) {
				pix = (int)((img.getRGB(x, y) & 0xFF)
				+ (img.getRGB(x+1, y) & 0xFF)
				+ (img.getRGB(x, y+1) & 0xFF)
				+ (img.getRGB(x+1, y+1) & 0xFF)) / 4;
				p = (255<<24) | (pix<<16) | (pix<<8) | pix;
				pixImg.setRGB(x, y, p);
				pixImg.setRGB(x+1, y, p);
				pixImg.setRGB(x, y+1, p);
				pixImg.setRGB(x+1, y+1, p);
			}
		}
		WritableImage pixelateWritableImage = BufferedImage2WritableImage(pixImg);
		maskImageView.setImage(pixelateWritableImage); 
		label_maskImage.setText("Pixelated Image"); 
	}
	
	public static BufferedImage resizeGrayScaleImage(BufferedImage img, int newHeight) {
		double scaleFactor = (double) newHeight / img.getHeight();
		BufferedImage scaledImg = new BufferedImage((int)(scaleFactor*img.getWidth()), newHeight, BufferedImage.TYPE_BYTE_GRAY);
		
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return scaleOp.filter(img, scaledImg);
	}

	// apply Gaussian Blur to a grayscale image
	public static BufferedImage gaussianBlur(BufferedImage img) {
		
		/**    y y1 y2
		 * x  [[1 2 1]
		 * x1 [2 4 2]
		 * x2 [1 2 1]]
		 */
		
		BufferedImage blurImg = new BufferedImage(img.getWidth()-2, img.getHeight()-2, BufferedImage.TYPE_BYTE_GRAY);
		int pix=0;
		for (int y=0; y<blurImg.getHeight(); y++) {
			for (int x=0; x<blurImg.getWidth(); x++) {
				pix = (int)(4*(img.getRGB(x+1, y+1) & 0xFF)
				+ 2*(img.getRGB(x+1, y) & 0xFF)
				+ 2*(img.getRGB(x+1, y+2) & 0xFF)
				+ 2*(img.getRGB(x, y+1) & 0xFF)
				+ 2*(img.getRGB(x+2, y+1) & 0xFF)
				+ (img.getRGB(x, y) & 0xFF)
				+ (img.getRGB(x, y+2) & 0xFF)
				+ (img.getRGB(x+2, y) & 0xFF)
				+ (img.getRGB(x+2, y+2) & 0xFF)) / 16;
				int p = (255<<24) | (pix<<16) | (pix<<8) | pix;
				blurImg.setRGB(x, y, p);
			}
		}	
		return blurImg;
	}
	
	public WritableImage Mat2WritableImage(Mat image) throws IOException {	      
	      //Encoding the image
	      MatOfByte matOfByte = new MatOfByte();
	      Imgcodecs.imencode(".jpg", image, matOfByte);
	      
	      //Storing the encoded Mat in a byte array
	      byte[] byteArray = matOfByte.toArray();
	      
	      //Displaying the image
	      InputStream in = new ByteArrayInputStream(byteArray);
	      BufferedImage bufImage = ImageIO.read(in);
	      System.out.println("Image Loaded");
	      WritableImage writableImage = SwingFXUtils.toFXImage(bufImage, null);  
	      return writableImage;
	}
	
	public WritableImage BufferedImage2WritableImage(BufferedImage img) {
		WritableImage writableImage = SwingFXUtils.toFXImage(img, null);
		return writableImage;
	}
	
	public static BufferedImage toGrayScale2(BufferedImage img) {
		System.out.println(" Converting to GrayScale2.");
		BufferedImage grayImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		
		int rgb=0, r=0 ,g=0, b=0;
		
		for (int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++) {
				rgb = (int)(img.getRGB(x, y));
				r = ((rgb >> 16) & 0xFF);
				g = ((rgb >> 8) & 0xFF);
				b = (rgb & 0xFF);
				rgb = (int)((r+g+b)/3);
				rgb = (255<<24) | (rgb<<16) | (rgb<<8) | rgb;
				grayImage.setRGB(x, y, rgb);
			}
		}		
		return grayImage;
	}	
	
	public void display(BufferedImage img) {
		WritableImage originalImage = BufferedImage2WritableImage(img);
		originalImageView.setImage(originalImage);
		label_originalImage.setText("Original Image: " + img.getWidth() + "x" + img.getHeight());
	}
	
}
