package a8;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageAdjuster extends JPanel implements ChangeListener, Runnable {

	//main method
	public static void main(String[] args) throws IOException {
		Picture p = A8Helper.readFromURL("https://upload.wikimedia.org/wikipedia/en/2/2f/Landscape_of_Guriceel.jpeg");

		ImageAdjuster adjuster = new ImageAdjuster(p);

		JFrame picFrame = new JFrame();
		picFrame.setTitle("Image Adjuster");
		picFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(adjuster, BorderLayout.CENTER);
		picFrame.setContentPane(top_panel);

		picFrame.pack();
		picFrame.setVisible(true);
	}

	//abstraction
	private Picture picture;
	private Picture untouched;
	private PictureView pictureView;
	private JPanel sliderPanel;
	private JSlider blur;
	private JSlider saturation;
	private JSlider brightness;


	public ImageAdjuster(Picture picture) {
		this.picture = picture;

		//copies original picture
		untouched = new PictureImpl(picture.getWidth(), picture.getHeight());
		for(int i = 0; i < picture.getWidth(); i++) {
			for(int j = 0; j < picture.getHeight(); j++) {
				untouched.setPixel(i, j, picture.getPixel(i, j));
			}
		}	

		pictureView = new PictureView(picture.createObservable());
		setLayout(new BorderLayout());
		add(pictureView, BorderLayout.CENTER);

		/*
		 * create JSliders with appropriate spacing
		 * name each one to differentiate b/w them
		 */
		blur = new JSlider(0, 5, 0);
		blur.setPaintTicks(true);
		blur.setPaintLabels(true);
		blur.setMajorTickSpacing(1);
		blur.setName("blur");
		saturation = new JSlider(-100, 100, 0);
		saturation.setPaintTicks(true);
		saturation.setMajorTickSpacing(25);
		saturation.setPaintLabels(true);
		saturation.setName("saturation");
		brightness = new JSlider(-100, 100, 0);
		brightness.setPaintTicks(true);
		brightness.setPaintLabels(true);
		brightness.setMajorTickSpacing(25);
		brightness.setName("brightness");

		/*
		 * this class is changeListener for all three sliders
		 */
		blur.addChangeListener(this);
		saturation.addChangeListener(this);
		brightness.addChangeListener(this);

		/*
		 * create new JPanel to contain sliders
		 * each slider goes in one element of grid
		 */
		sliderPanel = new JPanel();
		add(sliderPanel, BorderLayout.SOUTH);
		sliderPanel.setLayout(new GridLayout(7,1));

		sliderPanel.add(new JLabel("Blur: "));
		sliderPanel.add(blur);
		sliderPanel.add(new JLabel("Saturation: "));
		sliderPanel.add(saturation);
		sliderPanel.add(new JLabel("Brightness: "));
		sliderPanel.add(brightness);
		sliderPanel.add(new JLabel(" "));
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (((JSlider) (e.getSource())).getValueIsAdjusting()) {
			return;
		}


		(new Thread(this)).start();
	}

	//used with brightness slider
	private static Pixel calculateNewBrightness(Pixel current, Pixel toBlend, int percentage) {
		double dblPercentage = percentage / 100.0;

		return new ColorPixel(toBlend.getRed()*dblPercentage + current.getRed()*(1.0-dblPercentage),
				toBlend.getGreen()*dblPercentage + current.getGreen()*(1.0-dblPercentage),
				toBlend.getBlue()*dblPercentage + current.getBlue()*(1.0-dblPercentage));
	}

	//used with saturation slider
	private static Pixel saturate(int satFactor, double intensity, Pixel old) {
		double newRed = 0;
		double newGreen = 0;
		double newBlue = 0;
		
		/*
		 * account for black pixels
		 * just stay black no matter what
		 */ 
		if((old.getIntensity() - 0.0) < 0.0001) {
			return old;
		}

		//removing color 
		if(satFactor < 0) {
			newRed = old.getRed() * (1.0 + (satFactor / 100.0) ) - (intensity * satFactor / 100.0);
			newBlue = old.getBlue() * (1.0 + (satFactor / 100.0) ) - (intensity * satFactor / 100.0);
			newGreen = old.getGreen() * (1.0 + (satFactor / 100.0) ) - (intensity * satFactor / 100.0);
			return new ColorPixel(newRed, newGreen, newBlue);
		}

		//saturating color
		else if(satFactor > 0) {
			//find which color has maximum 
			double maxComponent = old.getRed() > old.getBlue() ? old.getRed() : old.getBlue();
			maxComponent = maxComponent > old.getGreen() ? maxComponent : old.getGreen();

			newRed = old.getRed() * ((maxComponent + ((1.0 - maxComponent) * (satFactor/ 100.0))) / maxComponent);
			newBlue = old.getBlue() * ((maxComponent + ((1.0 - maxComponent) * (satFactor/ 100.0))) / maxComponent);
			newGreen = old.getGreen() * ((maxComponent + ((1.0 - maxComponent) * (satFactor/ 100.0))) / maxComponent);

			return new ColorPixel(newRed, newGreen, newBlue);
		}

		//no saturation/discoloration 
		else {
			return old;
		}
	}

	//do the work of blurring/saturating/brightness
	//on separate thread 
	public void run() {

		Picture blurred = new PictureImpl(picture.getWidth(), picture.getHeight());
		Picture blurredAndSaturated = new PictureImpl(picture.getWidth(), picture.getHeight());
		Picture allChanges = new PictureImpl(picture.getWidth(), picture.getHeight());

		//BLUR
		//get value from slider 
		int dx = blur.getValue();
		int dy = blur.getValue();

		//iterate through the entire picture
		for(int i = 0; i < picture.getWidth(); i++) {
			for(int j = 0; j < picture.getHeight(); j++) {

				//accumulate color components here
				//keep track of number of pixels for avg
				double totalR = 0;
				double totalB = 0;
				double totalG = 0;
				double numOfPixs = 0;

				//iterating through pixels surrounding pixel to blur
				for(int k = i - dx; k <= i + dx; k++) {
					for(int l = j - dy; l <= j + dy; l++) {

						//check for out of bounds, do nothing
						if (k < 0 || k >= untouched.getWidth()
								|| l < 0 || l >= untouched.getHeight()) {

						}

						//if not out of bounds, accumulate
						else {
							totalR += untouched.getPixel(k, l).getRed();
							totalG += untouched.getPixel(k, l).getGreen();
							totalB += untouched.getPixel(k, l).getBlue();
							numOfPixs++;
						}
					}

					blurred.setPixel(i, j, new ColorPixel(totalR / numOfPixs, totalG / numOfPixs, totalB / numOfPixs));
				}
			}
		}

		//SATURATION
		//get value from slider
		int satFactor = saturation.getValue();

		for(int i = 0; i < picture.getWidth(); i++) {
			for(int j = 0; j < picture.getHeight(); j++) {
				Pixel saturated = saturate(satFactor, blurred.getPixel(i, j).getIntensity(), blurred.getPixel(i, j));
				blurredAndSaturated.setPixel(i, j, saturated); 
			}
		}

		//BRIGHTNESS
		//get value from slider
		int percent = brightness.getValue();

		for(int i = 0; i < picture.getWidth(); i++) {
			for(int j = 0; j < picture.getHeight(); j++) {
				if(percent < 0) {
					Pixel darker = calculateNewBrightness(blurredAndSaturated.getPixel(i, j), new GrayPixel(0.0), Math.abs(percent));
					allChanges.setPixel(i, j, darker);
				}
				else {
					Pixel lighter = calculateNewBrightness(blurredAndSaturated.getPixel(i, j), new GrayPixel(1.0), Math.abs(percent));
					allChanges.setPixel(i, j, lighter);
				}
			}
		} 
		//Modify entire picture based on all changes
		pictureView.setPicture(allChanges.createObservable());

	}
}

