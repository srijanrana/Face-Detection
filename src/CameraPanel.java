import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

public class CameraPanel extends JPanel implements Runnable, ActionListener {
BufferedImage image;
VideoCapture capture;
JButton screenshot;
CascadeClassifier faceDetector;
MatOfRect faceDetections;
CameraPanel()
{
faceDetector=new CascadeClassifier(CameraPanel.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
faceDetections=new MatOfRect();
screenshot=new JButton("Capture");
screenshot.addActionListener(this);
add(screenshot);
}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//capturing and storing image
		File output=new File("Image1.png");
		int i=0;
		while(output.exists()){
			i++;
			output=new File("Image"+i+".png");
			
		}
			
	try {
		ImageIO.write(image, "png", output);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
		
	}

	@Override
	public void run() {
		System.loadLibrary("opencv_java2410");
		capture= new VideoCapture(0);
		Mat webcam_image=new Mat();
		if (capture.isOpened()){
			while(true)
			{
				capture.read(webcam_image);
				if(!webcam_image.empty())
				{
					JFrame topFrame=(JFrame) SwingUtilities.getWindowAncestor(this);
					topFrame.setSize(webcam_image.width()+40, webcam_image.height()+110);
					matToBufferedImage(webcam_image);
					faceDetector.detectMultiScale(webcam_image, faceDetections);
					repaint();
				}
			}
		}
		
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(image==null) return;
		g.drawImage(image, 10, 40, image.getWidth(), image.getHeight(), null);
		g.setColor(Color.GREEN);
		for(Rect rect: faceDetections.toArray()){
		g.drawRect(rect.x+10,rect.y+40,rect.width,rect.height);	
		}
	}
	
	public void matToBufferedImage(Mat matBGR)
	{
		int width=matBGR.width(); 
		int height=matBGR.height();
		int channels=matBGR.channels();
		
		byte[] source=new byte[width*height*channels];
		matBGR.get(0, 0, source);
		image=new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
		final byte[] target=((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(source, 0, target, 0, source.length);
		
	}
	
	public void switchCamera(int x)
	{
		capture=new VideoCapture(x);
	}
	
}
