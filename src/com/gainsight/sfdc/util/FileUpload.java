package com.gainsight.sfdc.util;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Robot;

public class FileUpload {

	public void uploadFile(String filename) {

		new FileChooserThread(filename).start();

	}

	public class FileChooserThread extends Thread {
		public FileChooserThread(String file) {
			super(new FileRunner(file));
		}
	}

	public class FileRunner implements Runnable {
		private String fullName;

		public FileRunner(String fileName) {
			this.fullName = fileName;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);

				Robot robot = new Robot(); // input simulation class

				for (char c : fullName.toCharArray()) {
					if (c == ':') {
						robot.keyPress(KeyEvent.VK_SHIFT);
						robot.keyPress(KeyEvent.VK_SEMICOLON);
						robot.keyRelease(KeyEvent.VK_SHIFT);
					} else if (c == '/') {
						robot.keyPress(KeyEvent.VK_BACK_SLASH);
					} else {
						robot.keyPress(KeyStroke.getKeyStroke(
								Character.toUpperCase(c), 0).getKeyCode());
					}
				}

				robot.keyPress(KeyEvent.VK_ENTER);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}