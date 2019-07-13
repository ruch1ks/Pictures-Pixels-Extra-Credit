package a8;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FramePuzzle extends JPanel implements MouseListener, KeyListener {

	private PictureView[][] pictureGrid;
	private PictureView blankTile;

	//used to divide picture into 25 pieces
	private int newWidth;
	private int newHeight;

	//main method
	public static void main(String[] args) throws IOException {
		Picture p = A8Helper.readFromURL("https://upload.wikimedia.org/wikipedia/en/2/2f/Landscape_of_Guriceel.jpeg");

		FramePuzzle puzzle = new FramePuzzle(p);

		JFrame picFrame = new JFrame();
		picFrame.setTitle("FramePuzzle");
		picFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		picFrame.addKeyListener(puzzle);

		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(puzzle, BorderLayout.CENTER);
		picFrame.setContentPane(top_panel);

		picFrame.pack();
		picFrame.setVisible(true);
	}

	public FramePuzzle(Picture picture) {

		/*
		 * add subpictures to array
		 * except for last element
		 * uses default initialization of picture obj
		 */
		pictureGrid = new PictureView[5][5];
		int newXOff = 0;
		int newYOff = 0;
		newWidth = picture.getWidth() / 5;
		newHeight = picture.getHeight() / 5;

		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {

				if(i == 4 && j == 4) {
					blankTile = new PictureView(new PictureImpl(newWidth, newHeight).createObservable());
					pictureGrid[i][j] = blankTile;
					blankTile.addMouseListener(this);
					blankTile.addKeyListener(this);
				}

				else {
					pictureGrid[i][j] = new PictureView(new SubPictureImpl(picture, newXOff, newYOff, newWidth, newHeight).createObservable());

					pictureGrid[i][j].addMouseListener(this);
					pictureGrid[i][j].addKeyListener(this);

				} 

				newXOff += newWidth; 
			}

			newYOff += newHeight;
			newXOff = 0;

		}

		setLayout(new GridLayout(5,5));
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				add(pictureGrid[i][j]);
			}
		}

	}

	public void keyPressed(KeyEvent e) {
		return;
	}

	public void keyTyped(KeyEvent e) {
		return;
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 5; j++) {

					/*
					 * pick arbitrary pixels
					 * determine if both are white
					 * use this to assume if that is white tile
					 */
					if(pictureGrid[i][j].getPicture().getPixel(0, 0).getIntensity() == 1.0
							&& pictureGrid[i][j].getPicture().getPixel(13, 16).getIntensity() == 1.0) {
						//account for out of bounds
						if(j - 1 < 0) {
							return;
						}

						//exchange white tile with the one to the left						
						pictureGrid[i][j].setPicture(pictureGrid[i][j - 1].getPicture());
						pictureGrid[i][j - 1].setPicture(new PictureImpl(newWidth, newHeight).createObservable());

						//only do this ONCE
						return;
					}
				}
			}
		}

		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 5; j++) {

					/*
					 * pick arbitrary pixels
					 * determine if both are white
					 * use this to assume if that is white tile
					 */
					if(pictureGrid[i][j].getPicture().getPixel(0, 0).getIntensity() == 1.0
							&& pictureGrid[i][j].getPicture().getPixel(13, 16).getIntensity() == 1.0) {
						//account for out of bounds
						if(j + 1 >= 5) {
							return;
						}

						//exchange white tile with the one to the right						
						pictureGrid[i][j].setPicture(pictureGrid[i][j + 1].getPicture());
						pictureGrid[i][j + 1].setPicture(new PictureImpl(newWidth, newHeight).createObservable());

						//only do this ONCE
						return;
					}
				}
			}
		}

		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 5; j++) {

					/*
					 * pick arbitrary pixels
					 * determine if both are white
					 * use this to assume if that is white tile
					 */
					if(pictureGrid[i][j].getPicture().getPixel(0, 0).getIntensity() == 1.0
							&& pictureGrid[i][j].getPicture().getPixel(13, 16).getIntensity() == 1.0) {
						//account for out of bounds
						if(i - 1 < 0) {
							return;
						}

						//exchange white tile with the one to the top						
						pictureGrid[i][j].setPicture(pictureGrid[i - 1][j].getPicture());
						pictureGrid[i - 1][j].setPicture(new PictureImpl(newWidth, newHeight).createObservable());

						//only do this ONCE
						return;
					}
				}
			}
		}

		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 5; j++) {

					/*
					 * pick arbitrary pixels
					 * determine if both are white
					 * use this to assume if that is white tile
					 */
					if(pictureGrid[i][j].getPicture().getPixel(0, 0).getIntensity() == 1.0
							&& pictureGrid[i][j].getPicture().getPixel(13, 16).getIntensity() == 1.0) {
						//account for out of bounds
						if(i + 1 >= 5) {
							return;
						}

						//exchange white tile with the one to the bottom						
						pictureGrid[i][j].setPicture(pictureGrid[i + 1][j].getPicture());
						pictureGrid[i + 1][j].setPicture(new PictureImpl(newWidth, newHeight).createObservable());

						//only do this ONCE
						return;
					}
				}
			}
		}	
	}

	public void mouseReleased(MouseEvent e) {
		PictureView pv_clicked = (PictureView) e.getSource();

		int clickedRow = -1;
		int clickedColumn = -1;

		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				if(pictureGrid[i][j].equals(pv_clicked)) {
					clickedRow = i;
					clickedColumn = j;
				}
			}
		}

		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				/*
				 * pick arbitrary pixels in each picture
				 * determine if both are white
				 * use this to assume if that is white tile
				 */
				if(pictureGrid[i][j].getPicture().getPixel(0, 0).getIntensity() == 1.0
						&& pictureGrid[i][j].getPicture().getPixel(13, 16).getIntensity() == 1.0) {

					//white is to LEFT of click, shift everything else left 
					if(i == clickedRow && j < clickedColumn) {
						for(int k = j + 1; k <= clickedColumn; k++) {
							pictureGrid[clickedRow][k - 1].setPicture(pictureGrid[clickedRow][k].getPicture());
						}
						pictureGrid[clickedRow][clickedColumn].setPicture(new PictureImpl(newWidth, newHeight).createObservable());
					}

					//white is to RIGHT of click, shift everything right
					else if(i == clickedRow && j > clickedColumn) {
						for(int k = j - 1; k >= clickedColumn; k--) {
							pictureGrid[clickedRow][k + 1].setPicture(pictureGrid[clickedRow][k].getPicture());
						}
						pictureGrid[clickedRow][clickedColumn].setPicture(new PictureImpl(newWidth, newHeight).createObservable());

					}

					//white is ABOVE click, shift everything up
					else if(j == clickedColumn && i < clickedRow) {
						for(int k = i + 1; k <= clickedRow; k++) {
							pictureGrid[k - 1][clickedColumn].setPicture(pictureGrid[k][clickedColumn].getPicture());
						}
						pictureGrid[clickedRow][clickedColumn].setPicture(new PictureImpl(newWidth, newHeight).createObservable());

					}

					//white is BELOW click, shift everything down
					else if(j == clickedColumn && i > clickedRow) {
						for(int k = i - 1; k >= clickedRow; k--) {
							pictureGrid[k + 1][clickedColumn].setPicture(pictureGrid[k][clickedColumn].getPicture());
						}

						pictureGrid[clickedRow][clickedColumn].setPicture(new PictureImpl(newWidth, newHeight).createObservable());
					}			                                                                                                                                    
				}
			}
		}		
	}

	//rest of these are unneeded
	public void mouseClicked(MouseEvent e) {
		return;
	}

	public void mouseEntered(MouseEvent e) {
		return;
	}

	public void mousePressed(MouseEvent e) {
		return;
	}

	public void mouseExited(MouseEvent e) {
		return;
	}

}

