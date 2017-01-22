package a7;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PixelInspector extends JPanel implements MouseListener {

	private PictureView picture_view;
	private JLabel redLabel;
	private Picture picture;

	private JPanel mainPanel;
	private JPanel leftPanel;
	private JLabel green;
	private JLabel blue;
	private JLabel red;
	private JLabel xLabel;
	private JLabel yLabel;
	private JLabel intensity;
	private JPanel rightPanel;

	public PixelInspector(Picture picture) {
		setLayout(new BorderLayout());
		this.picture = picture;

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));

		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(6, 1));
		
		rightPanel = new JPanel();
		
		xLabel = new JLabel("");
		leftPanel.add(xLabel);

		yLabel = new JLabel("");
		leftPanel.add(yLabel);

		red = new JLabel("");
		leftPanel.add(red);

		green = new JLabel("");
		leftPanel.add(green);

		blue = new JLabel("");
		leftPanel.add(blue);

		intensity = new JLabel("");
		leftPanel.add(intensity);

		mainPanel.add(leftPanel);
		add(mainPanel, BorderLayout.CENTER);
		picture_view = new PictureView(picture.createObservable());
		picture_view.addMouseListener(this);
		rightPanel.add(picture_view);
		mainPanel.add(rightPanel);

	}

	@Override

	public void mouseClicked(MouseEvent e) {
		double redval;
		double blueval;
		double greenval;
		double intensity;
		double x;
		double y;
		x = e.getX();
		this.xLabel.setText("X:" + x);

		y = e.getY();
		this.yLabel.setText("Y:" + y);

		redval = Math.round(picture.getPixel(e.getX(), e.getY()).getRed() * 100d) / 100d;
		this.red.setText("Red:" + redval);

		blueval = Math.round(picture.getPixel(e.getX(), e.getY()).getBlue() * 100d) / 100d;
		this.blue.setText("Blue:" + blueval);

		greenval = Math.round(picture.getPixel(e.getX(), e.getY()).getGreen() * 100d) / 100d;
		this.green.setText("Green:" + greenval);

		intensity = Math.round(picture.getPixel(e.getX(), e.getY()).getIntensity() * 100d) / 100d;
		this.intensity.setText("Intensity:" + intensity);

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

}
