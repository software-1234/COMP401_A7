package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageAdjuster extends JPanel implements ChangeListener, ActionListener {

	private PictureView picture_view;
	private PictureView duplicate_view;
	private JSlider blur;
	private JSlider brightness;
	private JSlider saturation;
	private JLabel blurLabel;
	private JLabel brightnessLabel;
	private JLabel saturationLabel;

	private Picture picture;
	private Picture duplicate;
	List<ChangeListener> change_listeners;

	public ImageAdjuster(Picture picture) {

		setLayout(new BorderLayout());

		this.picture = picture;

		picture_view = new PictureView(picture.createObservable());
		add(picture_view, BorderLayout.CENTER);

		JPanel slider_panel = new JPanel();
		slider_panel.setLayout(new GridLayout(3, 1));

		blurLabel = new JLabel("Blur");
		brightnessLabel = new JLabel("Brightness");
		saturationLabel = new JLabel("Saturation");

		blur = new JSlider(0, 5, 0);
		brightness = new JSlider(-100, 100, 0);
		saturation = new JSlider(-100, 100, 0);

		blur.setMinorTickSpacing(1);
		blur.setPaintTicks(true);

		brightness.setMinorTickSpacing(25);
		brightness.setPaintTicks(true);

		saturation.setMinorTickSpacing(25);
		saturation.setPaintTicks(true);

		blur.setSnapToTicks(true);
		slider_panel.add(blurLabel);
		slider_panel.add(blur);
		slider_panel.add(brightnessLabel);
		slider_panel.add(brightness);
		slider_panel.add(saturationLabel);
		slider_panel.add(saturation);

		add(slider_panel, BorderLayout.SOUTH);

		blur.addChangeListener(this);
		brightness.addChangeListener(this);
		saturation.addChangeListener(this);

		Hashtable label_table_blur = new Hashtable();
		Hashtable label_table_brightness = new Hashtable();
		Hashtable label_table_saturation = new Hashtable();

		for (int i = 0; i < 6; i++) {
			label_table_blur.put(new Integer(i), new JLabel(String.valueOf(i)));
		}

		for (int i = -100; i < 125; i = i + 25) {
			label_table_brightness.put(new Integer(i), new JLabel(String.valueOf(i)));
			label_table_saturation.put(new Integer(i), new JLabel(String.valueOf(i)));
		}

		blur.setLabelTable(label_table_blur);
		brightness.setLabelTable(label_table_brightness);
		saturation.setLabelTable(label_table_saturation);

		blur.setPaintLabels(true);
		brightness.setPaintLabels(true);
		saturation.setPaintLabels(true);

		setFocusable(true);

	}

	public void addChangeListener(ChangeListener l) {
		change_listeners.add(l);
	}

	public void removeChangeListener(ChangeListener l) {
		change_listeners.remove(l);
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		duplicate = new PictureImpl(picture.getWidth(), picture.getHeight());
		for (int i = 0; i < picture.getWidth(); i++) {

			for (int j = 0; j < picture.getHeight(); j++) {
				duplicate.setPixel(i, j, picture.getPixel(i, j));
			}

		}
		double brightnessSlider = brightness.getValue();
		double saturationSlider = saturation.getValue();

		double bright_factor = brightnessSlider / (double) 100;
		double sat_factor = saturationSlider / (double) 100;
		int blur_factor = blur.getValue();

		double brightRed;
		double brightGreen;
		double brightBlue;

		double satRed;
		double satGreen;
		double satBlue;

		double blurRed;
		double blurGreen;
		double blurBlue;

		double red_total = 0;
		double green_total = 0;
		double blue_total = 0;
		double iterations = 0;

		double old_red;
		double old_green;
		double old_blue;

		Pixel sat_pixel;

		if (!((JSlider) e.getSource()).getValueIsAdjusting()) {
			for (int x = 0; x < picture.getWidth(); x++) {
				for (int y = 0; y < picture.getHeight(); y++) {
					iterations = 0;
					red_total = 0;
					blue_total = 0;
					green_total = 0;
					for (int i = -1 * blur_factor; i < blur_factor; i++) {
						for (int j = -1 * blur_factor; j < blur_factor; j++) {
							if (x + i < picture.getWidth() && y + j < picture.getHeight() 
								&& x + i >= 0 && y + j >= 0) {
								iterations++;
								red_total += picture.getPixel(x+i, y+j).getRed();
								blue_total += picture.getPixel(x+i, y+j).getBlue();
								green_total += picture.getPixel(x+i, y+j).getGreen();
							}
						}
						blurRed = (red_total / iterations);
						blurGreen = (green_total / iterations);
						blurBlue = (blue_total / iterations);
						Pixel blurredPixel = new ColorPixel(blurRed, blurGreen, blurBlue);
						duplicate.setPixel(x, y, blurredPixel);
					}
				}
			}
			Picture blur_pic = duplicate;
			
			for (int x = 0; x < blur_pic.getWidth(); x++) {
				for (int y = 0; y < blur_pic.getHeight(); y++) {
					old_red = blur_pic.getPixel(x, y).getRed();
					old_green = blur_pic.getPixel(x, y).getGreen();
					old_blue = blur_pic.getPixel(x, y).getBlue();
					if (bright_factor >= -1 && bright_factor < 0) {
						brightRed = ((1 - Math.abs(bright_factor)) * old_red);
						brightGreen = ((1 - Math.abs(bright_factor)) * old_green);
						brightBlue = ((1 - Math.abs(bright_factor)) * old_blue);
						Pixel darkpixel = new ColorPixel(brightRed, brightGreen, brightBlue);
						blur_pic.setPixel(x, y, darkpixel);
					}
					else if (bright_factor <= 1 && bright_factor > 0) {
						brightRed = ((1 - bright_factor) * old_red) + ((bright_factor) * (1));
						brightGreen = ((1 - bright_factor) * old_green) + ((bright_factor) * (1));
						brightBlue = ((1 - bright_factor) * old_blue) + ((bright_factor) * (1));
						Pixel lightpixel = new ColorPixel(brightRed, brightGreen, brightBlue);
						blur_pic.setPixel(x, y, lightpixel);
					} else {
						blur_pic.setPixel(x, y, blur_pic.getPixel(x, y));
					}
				}
			}
			Picture bright_pic = blur_pic;
			// saturation
			for (int x = 0; x < bright_pic.getWidth(); x++) {
				for (int y = 0; y < bright_pic.getHeight(); y++) {
					old_red = bright_pic.getPixel(x, y).getRed();
					old_green = bright_pic.getPixel(x, y).getGreen();
					old_blue = bright_pic.getPixel(x, y).getBlue();
					try {
						if (sat_factor >= -1 && sat_factor < 0) {
							satRed = (old_red) * (1 + (sat_factor))
									- (bright_pic.getPixel(x, y).getIntensity() * (sat_factor));
							satGreen = (old_green) * (1 + (sat_factor))
									- (bright_pic.getPixel(x, y).getIntensity() * (sat_factor));
							satBlue = (old_blue) * (1 + (sat_factor))
									- (bright_pic.getPixel(x, y).getIntensity() * (sat_factor));
							sat_pixel = new ColorPixel(satRed, satGreen, satBlue);
							bright_pic.setPixel(x, y, sat_pixel);
						}

						else if (sat_factor <= 1 && sat_factor > 0) {
							double max_sat_value = Math.max(old_red, Math.max(old_blue, old_green));
							double sat_multiply_value = (max_sat_value + ((1.0 - max_sat_value) 
									* (sat_factor))) / max_sat_value;
							satRed = old_red * sat_multiply_value;
							satGreen = old_green * sat_multiply_value;
							satBlue = old_blue * sat_multiply_value;
							sat_pixel = new ColorPixel(satRed, satGreen, satBlue);
							bright_pic.setPixel(x, y, sat_pixel);
						}
						else {
							bright_pic.setPixel(x, y, bright_pic.getPixel(x, y));
						}
					} catch (IllegalArgumentException ex) {
						continue;
					}
				}
			}
			ObservablePicture combPic = new ObservablePictureImpl(bright_pic.createObservable());
			picture_view.setPicture(combPic);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
