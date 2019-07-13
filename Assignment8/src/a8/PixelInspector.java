package a8;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelInspector extends JPanel implements MouseListener {
	//main method
	public static void main(String[] args) throws IOException {
		Picture p = A8Helper.readFromURL("https://upload.wikimedia.org/wikipedia/en/2/2f/Landscape_of_Guriceel.jpeg");
		
		PixelInspector inspector = new PixelInspector(p);
		
		JFrame picFrame = new JFrame();
		picFrame.setTitle("Pixel Inspector");
		picFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(inspector, BorderLayout.CENTER);
		picFrame.setContentPane(top_panel);

		picFrame.pack();
		picFrame.setVisible(true);
	}
	
	//abstraction
	private PictureView pictureView;
	private JPanel pixelInfoPanel;
	
	/*
	 * stores picture passed in
	 * to access getPixel() methods
	 */
	private Picture picture;
	
	/*
	 * these labels need to be updated with every click
	 */
	private JLabel xLocation;
	private JLabel yLocation;
	private JLabel red;
	private JLabel green;
	private JLabel blue;
	private JLabel brightness;
		
	public PixelInspector(Picture picture) {
		//to access pixel methods
		this.picture = picture;
		
		/*
		 * put Picture in east
		 * it handles its own MouseEvents
		 */
		setLayout(new BorderLayout());
		
		pictureView = new PictureView(picture.createObservable());
		pictureView.addMouseListener(this);
		add(pictureView, BorderLayout.CENTER);
		
		/*
		 * initially, all JLabels should be populated
		 * make their font larger
		 */
		xLocation = new JLabel("X: ");
		yLocation = new JLabel("Y: ");
		red = new JLabel("Red: ");
		green = new JLabel("Green: ");
		blue = new JLabel("Blue: ");
		brightness = new JLabel("Brightness: ");
		
		xLocation.setFont(new Font("SansSerif", Font.PLAIN, 20));
		yLocation.setFont(new Font("SansSerif", Font.PLAIN, 20));
		red.setFont(new Font("SansSerif", Font.PLAIN, 20));
		green.setFont(new Font("SansSerif", Font.PLAIN, 20));
		blue.setFont(new Font("SansSerif", Font.PLAIN, 20));
		brightness.setFont(new Font("SansSerif", Font.PLAIN, 20));

		
		/*
		 * creating new JPanel
		 * holds a grid of 6 rows
		 * one row for each component of assignment
		 * set new bounds to make sure 
		 * nothing gets covered by picture
		 */
		pixelInfoPanel = new JPanel();
   		add(pixelInfoPanel, BorderLayout.WEST);
		pixelInfoPanel.setLayout(new GridLayout(6,1));
		
		//add labels to each unit of grid
		pixelInfoPanel.add(xLocation);
		pixelInfoPanel.add(yLocation);
		pixelInfoPanel.add(red);
		pixelInfoPanel.add(green);
		pixelInfoPanel.add(blue);
		pixelInfoPanel.add(brightness);
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//to round doubles to two places
		NumberFormat rounded = new DecimalFormat("#0.00");
		
		//update labels according to click
		int xCoord = e.getX();
		int yCoord = e.getY();
		xLocation.setText("X: " + xCoord);
		yLocation.setText("Y: " + yCoord);
		red.setText("Red: " + rounded.format(picture.getPixel(xCoord, yCoord).getRed()));
		green.setText("Green " + rounded.format(picture.getPixel(xCoord, yCoord).getGreen()));
		blue.setText("Blue " + rounded.format(picture.getPixel(xCoord, yCoord).getBlue()));
		brightness.setText("Brightness: " + rounded.format(picture.getPixel(xCoord, yCoord).getIntensity()));
	}

	/*
	 * not used
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}