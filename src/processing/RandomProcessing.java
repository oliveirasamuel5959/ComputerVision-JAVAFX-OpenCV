package processing;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class RandomProcessing {
	
	List<Integer> numList = new ArrayList<Integer>();
	
	public Mat imageProcessing(Mat image) {
		
		Mat resizeimage = new Mat();
		Mat grayimage = new Mat();
		
		Imgproc.resize(image, resizeimage, new Size(80, 80), 0.1, 0.1, Imgproc.INTER_AREA);
	    Imgproc.cvtColor(resizeimage, grayimage, Imgproc.COLOR_RGB2GRAY);
	    
		for (int i = 0; i < grayimage.rows(); i++) {
    	  for (int j = 0; j < grayimage.cols(); j++) {
    		  
		  	  double[] data = grayimage.get(i, j);
		  	  
			  for (int k = 0; k < 1; k++) //Runs for the available number of channels
		        {
		            data[k] = data[k] * 2; //Pixel modification done here
		        }
			  grayimage.put(i, j, data); //Puts element back into matrix
    	  }
	    }
		
		//System.out.println(dataMatrix);
		//System.out.println(grayimage.type());

		return grayimage;
	}
	
	public List<Integer> generateNumber(int min, int max) {
		List<Integer> numList = new ArrayList<Integer>();
		for (int i = min; i < max + 1; i++) {
			numList.add(i);
		}
		return numList;
	}
}
