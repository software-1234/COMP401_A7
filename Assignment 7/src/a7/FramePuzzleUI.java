package a7;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FramePuzzleUI {
	public static void main(String[] args) throws IOException {
		Picture p = A7Helper.readFromURL("http://www.cs.unc.edu/~kmp/kmp.jpg");
		FramePuzzle FramePuzzle_widget = new FramePuzzle(p);

		JFrame main_frame = new JFrame();
		main_frame.setTitle("Assignment 7 Frame Puzzle");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.addKeyListener(FramePuzzle_widget);

		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(FramePuzzle_widget, BorderLayout.CENTER);
		main_frame.setContentPane(top_panel);

		main_frame.validate();
		main_frame.pack();
		main_frame.setVisible(true);

	}
}
