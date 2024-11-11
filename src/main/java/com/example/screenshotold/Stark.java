package com.example.screenshotold;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class Stark extends JFrame implements ActionListener, NativeMouseListener, NativeKeyListener
{
	
	private JButton startButton;
	private JButton pauseButton;
	private JButton saveButton;
	private boolean isListening=false;
	private JButton tempButton;
	private JButton newSessionButton;
	private static String path1="";
	private static String path2="";
	private static List<String> screenshotComments = new ArrayList<>();
	
	public Stark()
	{
		setTitle("Screenshot Capturer");
		setSize(300,150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		startButton = new JButton("Start");
		pauseButton = new JButton("Pause");
		saveButton = new JButton("Save");
		tempButton = new JButton("Temp location");
		newSessionButton = new JButton("Start new Session");
		
		setLayout(new FlowLayout());
		
		startButton.addActionListener(this);
		pauseButton.addActionListener(this);
		saveButton.addActionListener(this);
		tempButton.addActionListener(this);
		newSessionButton.addActionListener(this);

		add(startButton);
		add(pauseButton);
		add(saveButton);
		add(tempButton);
		add(newSessionButton);
		startButton.setEnabled(true);
		pauseButton.setEnabled(false);
		saveButton.setEnabled(false);
		newSessionButton.setEnabled(false);
		
		try
		{
			GlobalScreen.registerNativeHook();
		}
		catch(NativeHookException ex)
		{
			System.err.println("Error :"+ex);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			Stark app = new Stark();
			app.setVisible(true);
		});
	}
	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		if(isListening && nativeEvent.getKeyCode()==NativeKeyEvent.VC_SPACE && nativeEvent.getModifiers()==NativeKeyEvent.CTRL_L_MASK)
		{
			takeSreenshot();
		}
		
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
		if(isListening)
		{
			System.out.println("Clicked at ("+nativeEvent.getY()+")");
		}
		
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==startButton)
		{
			if(tempButton.isEnabled())
			{
				JOptionPane.showMessageDialog(null, "Please select a directory for temp files");
			}
			else
			{
				GlobalScreen.addNativeKeyListener(this);
				GlobalScreen.addNativeMouseListener(this);
				isListening=true;
				startButton.setEnabled(false);
				pauseButton.setEnabled(true);
				saveButton.setEnabled(false);
				newSessionButton.setEnabled(false);
			}
			
		}
		else if(e.getSource()==pauseButton)
		{
			GlobalScreen.addNativeKeyListener(this);
			GlobalScreen.addNativeMouseListener(this);
			isListening=false;
			startButton.setText("Continue");
			startButton.setEnabled(true);
			pauseButton.setEnabled(false);
			saveButton.setEnabled(true);
			newSessionButton.setEnabled(true);
		}
		else if(e.getSource()==saveButton)
		{
			JFileChooser chooser = new JFileChooser();
			
			int returnValue = chooser.showSaveDialog(null);
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = chooser.getSelectedFile();
				path2=selectedFile.getAbsolutePath();
				if(path2.toLowerCase().endsWith(".docx"))
				{
					path2+=".docx";
				}
				try
				{
					word();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, "Saved data to:"+path2);
				
				//pauseButton.setEnabled(true);
				saveButton.setEnabled(false);
				newSessionButton.setEnabled(true);
				startButton.setEnabled(false);
				startButton.setText("Start");
			}
			
		}
		else if(e.getSource()==tempButton)
		{
			JFileChooser chooser= new JFileChooser();
			chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);
			int returnValue=chooser.showOpenDialog(null);
			
			if(returnValue==JFileChooser.APPROVE_OPTION)
			{
				File selectedFolder=chooser.getSelectedFile();
				path1=selectedFolder.getAbsolutePath()+"\\";
				System.out.println("Path1:"+path1);
				
				try
				{
					word();
				}
				catch(Exception e1)
				{
				 e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, "Selected path: "+path1);
				
				tempButton.setEnabled(false);
			}
		}
		else if(e.getSource()==newSessionButton)
		{
			deleteTempFiles();
			
			newSessionButton.setEnabled(false);
			startButton.setEnabled(true);
			startButton.setText("Start");
			saveButton.setEnabled(false);
		}
		
	}
	public static void word() throws Exception
	{
		XWPFDocument docu = new XWPFDocument();
		FileOutputStream file = new FileOutputStream(path2);

		File folder = new File(path1);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				File fileImage = listOfFiles[i];
				if (fileImage.isFile() && i < screenshotComments.size()) {
					// Add the comment for this screenshot
					XWPFParagraph commentParagraph = docu.createParagraph();
					XWPFRun commentRun = commentParagraph.createRun();
					commentRun.setText("Comment: " + screenshotComments.get(i));
					commentRun.setItalic(true);
					commentRun.addBreak();

					// Add the screenshot image
					FileInputStream fileInput = new FileInputStream(fileImage);
					XWPFRun imageRun = docu.createParagraph().createRun();
					int imageFormat = XWPFDocument.PICTURE_TYPE_PNG;
					int width = 500;
					int height = 300;
					imageRun.addPicture(fileInput, imageFormat, fileImage.getName(), Units.toEMU(width), Units.toEMU(height));
					fileInput.close();
				}
			}
		}

		docu.write(file);
		file.close();
		docu.close();
		deleteTempFiles();
		System.out.println("Document saved with comments and screenshots.");
	}
	
	private static void deleteTempFiles()
	{
		List<String> imageExtensions = Arrays.asList(".jpg",".jpeg",".png",".gif",".bmp");
		File folder = new File(path1);
		
		if(folder.exists() && folder.isDirectory())
		{
			File[] files=folder.listFiles();
			if(files!=null)
			{
				for(File file: files)
				{
					if(isImageFile(file.getName(),imageExtensions))
					{
						if(file.delete())
						{
							System.out.println("Deleted: "+file.getName());
							System.out.println("All files are deleted");
						}
						else
						{
							System.out.println("Failed to delete: "+file.getName());
						}
					}
				}
			}
			
		}
	}
	
	private static boolean isImageFile(String fileName,List<String> imageExensions)
	{
		for(String extension : imageExensions)
		{
			if(fileName.toLowerCase().endsWith(extension))
			{
				return true;
			}
				
		}
		
		return false;
	}
	
	private void takeSreenshot() {
		try {
			Robot robot = new Robot();
			Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenshot = robot.createScreenCapture(rect);
			String imageName = "image" + System.currentTimeMillis() + ".jpg";
			File imageFile = new File(path1 + imageName);
			ImageIO.write(screenshot, "jpg", imageFile);

			// Prompt the user for a comment for this screenshot
			String comment = JOptionPane.showInputDialog("Enter a comment for this screenshot:");
			screenshotComments.add(comment != null ? comment : ""); // Add empty string if comment is null

			System.out.println("Screenshot taken and comment added.");
		} catch (Exception e) {
			System.out.println("Error taking screenshot: " + e.getMessage());
		}
	}
}
