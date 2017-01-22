package a7;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FramePuzzle extends JPanel implements KeyListener, ActionListener, MouseListener {

	private Picture picture;
	private JPanel mainPanel;
	private int width;
	private PictureView[] array_pic_chunks;
	private int pic_num;
	private Picture splitPic;
	private Picture duplicate;

	private int pic_height;
	private int pic_width;

	private JPanel[][] panels_to_add;
	private ObservablePicture small_obs;
	
	private int iterations;

	private int blank_x = 4;
	private int blank_y = 4;

	PictureView[][] movable = new PictureView[5][5];

	public FramePuzzle(Picture picture) {
		array_pic_chunks = new PictureView[25];

		this.picture = picture;
		pic_width = picture.getWidth() / 5;
		pic_height = picture.getHeight() / 5;

		setLayout(new BorderLayout());
		splitPic = new PictureImpl(pic_width, pic_height);

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 5));
		panels_to_add = new JPanel[5][5];

		duplicate = new PictureImpl(picture.getWidth(), picture.getHeight());
		for (int i = 0; i < picture.getWidth(); i++) {
			for (int j = 0; j < picture.getHeight(); j++) {
				duplicate.setPixel(i, j, picture.getPixel(i, j));
			}
		}

		int a = 0;
		int b = 0;
		iterations = 0;
		while (a < 5) {
			while (b < 5) {
				for (int j = (a) * pic_width; j < (a + 1) * pic_width; j++) {
					for (int k = (b) * pic_height; k < (b + 1) * pic_height; k++) {
						splitPic.setPixel(j - ((a) * pic_width), k - ((b) * pic_height), picture.getPixel(j, k));
					}
				}
				small_obs = splitPic.createObservable();
				JPanel split = new JPanel();
				PictureView pic_listener = new PictureView(small_obs);
				if (iterations < 24) {
					pic_listener.setPicture(small_obs);
					split.add(pic_listener);

					panels_to_add[a][b] = split;
					panels_to_add[a][b].revalidate();
				
					movable[a][b] = pic_listener;
					movable[a][b].addMouseListener(this);
					movable[a][b].setFocusable(false);
					movable[a][b].requestFocus();
					movable[a][b].revalidate();
				
				} else {
					Pixel color = new ColorPixel(.3, .6, .89);
					Picture solid = new PictureImpl(pic_width, pic_height);
					for (int i = 0; i < solid.getWidth(); i++) {
						for (int j = 0; j < solid.getHeight(); j++) {
							solid.setPixel(i, j, color);
						}
					}
					PictureView solid_listener = new PictureView(solid.createObservable());
					solid_listener.addMouseListener(this);
					split.add(solid_listener);

					panels_to_add[a][b] = split;
					panels_to_add[a][b].revalidate();
				
					movable[a][b] = solid_listener;
					movable[a][b].addMouseListener(this);
					movable[a][b].setFocusable(false);

				}
				iterations++;
				b++;
			}
			b = 0;
			a++;
		}
		for (int c = 0; c < 5; c++) {
			for (int d = 0; d < 5; d++) {
				mainPanel.add(panels_to_add[d][c]);
			}
		}
		mainPanel.revalidate();
		add(mainPanel);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		PictureView clickedPV;
		PictureView solid;
		PictureView old;
		int XLocation = 0;
		int YLocation = 0;

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (e.getSource() == movable[i][j]) {
					XLocation = i;
					YLocation = j;
				}
			}
		}
		solid = movable[blank_x][blank_y];
		clickedPV = movable[XLocation][YLocation];
		if ((XLocation < blank_x && YLocation == blank_y) || (YLocation < blank_y && XLocation == blank_x)) {
			if (XLocation == blank_x) {
				int locY = YLocation;
				for (int i = locY; i < blank_y + 1; i++) {
					panels_to_add[blank_x][i].remove(movable[blank_x][i]);
					if (i == locY) {
						panels_to_add[XLocation][YLocation].add(solid);
					} else {
						panels_to_add[XLocation][i].add(movable[XLocation][i - 1]);
					}
				}
				for (int i = blank_y; i > locY; i--) {
					old = movable[XLocation][i];
					movable[XLocation][i] = movable[XLocation][i - 1];
					movable[XLocation][i - 1] = old;
				}
				blank_y = YLocation;
			} else if (YLocation == blank_y) {
				int locX = XLocation;
				for (int i = locX; i < blank_x + 1; i++) {
					panels_to_add[i][blank_y].remove(movable[i][blank_y]);
					if (i == locX) {
						panels_to_add[XLocation][YLocation].add(solid);
					} else {
						panels_to_add[i][YLocation].add(movable[i - 1][YLocation]);
					}
				}
				for (int i = blank_x; i > locX; i--) {
					old = movable[i][YLocation];
					movable[i][YLocation] = movable[i - 1][YLocation];
					movable[i - 1][YLocation] = old;
				}
				blank_x = XLocation;
			}
		}

		else if ((XLocation > blank_x && YLocation == blank_y) || (YLocation > blank_y && XLocation == blank_x)) {
			if (XLocation == blank_x) {
				int locY = YLocation;
				for (int i = blank_y; i < locY + 1; i++) {
					panels_to_add[blank_x][i].remove(movable[blank_x][i]);
					if (i == locY) {
						panels_to_add[XLocation][YLocation].add(solid);
					} else {
						panels_to_add[blank_x][i].add(movable[blank_x][i + 1]);
					}
				}
				for (int i = blank_y; i < locY; i++) {
					old = movable[XLocation][i];
					movable[XLocation][i] = movable[XLocation][i + 1];
					movable[XLocation][i + 1] = old;
				}
				blank_y = YLocation;

			} else if (YLocation == blank_y) {
				int locX = XLocation;
				for (int i = blank_x; i < locX + 1; i++) {
					panels_to_add[i][blank_y].remove(movable[i][blank_y]);
					if (i == locX) {
						panels_to_add[XLocation][YLocation].add(solid);
					} else {
						panels_to_add[i][YLocation].add(movable[i + 1][YLocation]);
					}
				}

				for (int i = blank_x; i < locX; i++) {
					old = movable[i][YLocation];
					movable[i][YLocation] = movable[i + 1][YLocation];
					movable[i + 1][YLocation] = old;
				}
				blank_x = XLocation;
			}
		} else {

		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		PictureView monotone;
		PictureView old;
		int keyCode = e.getKeyCode();
		int x_old = 0;
		int y_old = 0;
		int x_new;
		int y_new;
		int x_source = x_old + blank_x;
		int y_source = y_old + blank_y;
		monotone = movable[x_source][y_source];
		switch (keyCode) {
		case KeyEvent.VK_UP:
			if (y_source > 0) {
				y_old++;
			}
			break;
		case KeyEvent.VK_DOWN:
			// handle down
			if (y_source < 4) {
				y_old--;
			}
			break;
		case KeyEvent.VK_LEFT:
			// handle left
			if (x_source > 0) {
				x_old--;
			}
			break;
		case KeyEvent.VK_RIGHT:
			// handle right
			if (x_source < 4) {
				x_old++;
			}
			break;
		}
		x_new = blank_x + x_old;
		y_new = blank_y - y_old;

		panels_to_add[x_source][y_source].remove(monotone);
		panels_to_add[x_new][y_new].remove(movable[x_new][y_new]);

		panels_to_add[x_new][y_new].add(monotone);
		panels_to_add[x_source][y_source].add(movable[x_new][y_new]);

		old = movable[x_source][y_source];
		movable[x_source][y_source] = movable[x_new][y_new];
		movable[x_new][y_new] = old;

		blank_x = x_new;
		blank_y = y_new;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
