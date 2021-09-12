import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNamer implements ActionListener{

JLabel directoryLabel, targetLabel, replacementLabel, beforeLabel, afterLabel;
	
JTextField directoryField, targetField, replacementField;

JTextArea beforePreview, afterPreview;

JFrame frame;

JButton directoryButton, startButton;

JFileChooser fc;

String target, fileExtension, directory;

String[] modes = {"Remove", "Replace", "Append", "Prepend"};

Path directoryPath, oldPath, newPath;

JComboBox<EditMode> modeBox;

ArrayList<String> files;
boolean isReplace;

final int FRAME_WIDTH = 745, FRAME_HEIGHT = 500;

@FunctionalInterface
public interface TargetListener extends DocumentListener {
    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    default void removeUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    default void changedUpdate(DocumentEvent e) {
        update(e);
    }
}

enum EditMode{
	REMOVE,
	REPLACE,
	APPEND,
	PREPEND
}

FileNamer(){
	files = new ArrayList<String>();
	directory = "";
	
	frame = new JFrame("File Renamer");
	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	fc = new JFileChooser();
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	
	directoryLabel = new JLabel("Directory:");
	targetLabel = new JLabel("Remove:");
	replacementLabel = new JLabel("With:");
	beforeLabel = new JLabel("Before:");
	afterLabel = new JLabel("After:");
	
	directoryField = new JTextField();
	targetField = new JTextField();
	replacementField = new JTextField();
	beforePreview = new JTextArea();
	afterPreview = new JTextArea();
	
	directoryButton = new JButton("Directory");
	startButton = new JButton("Start");
	

	frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	frame.setLocationRelativeTo(null);
	frame.setLayout(null);
	frame.setVisible(true);
	frame.setResizable(false);

	directoryField.setBounds((FRAME_WIDTH / 2) - 175, 50, 300, 30);
	directoryField.setEditable(false);
	directoryField.setBackground(Color.white);
	targetField.setBounds((FRAME_WIDTH / 2) - 175, 120, 300, 30);
	replacementField.setBounds((FRAME_WIDTH / 2) - 175, 160, 300, 30);
	
	directoryLabel.setBounds(directoryField.getX() - 75, directoryField.getY(), 75, 30);
	targetLabel.setBounds(targetField.getX() - 75, targetField.getY(), 75, 30);
	replacementLabel.setBounds(replacementField.getX() - 75, replacementField.getY(), 75, 30);
	beforeLabel.setBounds(50, 230, 100, 30);
	afterLabel.setBounds(430, 230, 100, 30);

	directoryButton.setBounds(directoryField.getX() + directoryField.getWidth() + 10, directoryField.getY(), 100, 30);
	directoryButton.addActionListener(this);

	startButton.setBounds((FRAME_WIDTH / 2) - 75, replacementField.getY() + replacementField.getHeight(), 100, 30);
	startButton.addActionListener(this);
	
	modeBox = new JComboBox<>(EditMode.values());
	modeBox.setBounds(targetField.getX() + targetField.getWidth() + 10, targetField.getY(), 110, 25);
	modeBox.addActionListener(this);
	isReplace = false;

	
	beforePreview.setBounds(50, 260, 250, 160);
	afterPreview.setBounds(430, 260, 250, 160);
	beforePreview.setEditable(false);
	afterPreview.setEditable(false);
	beforePreview.setBackground(Color.white);
	afterPreview.setBackground(Color.white);

	frame.add(directoryLabel);
	frame.add(targetLabel);
	frame.add(beforeLabel);
	frame.add(afterLabel);
	frame.add(directoryField);
	frame.add(targetField);
	frame.add(directoryButton);
	frame.add(startButton);
	frame.add(modeBox);
	frame.add(beforePreview);
	frame.add(afterPreview);
	
	targetField.getDocument().addDocumentListener((TargetListener) e -> {
		UpdateAfterPreview();
		});	
	replacementField.getDocument().addDocumentListener((TargetListener) e -> {
		UpdateAfterPreview();
		});
}

public static void main(String[] args)
{
	new FileNamer();
}

public void actionPerformed(ActionEvent e)
{
	if(e.getSource() == directoryButton)
	{
		fc.showOpenDialog(frame);
		File selectedDir = fc.getSelectedFile();
		if(selectedDir != null)
		{
			directoryField.setText(selectedDir.toPath().toString());
			directory = directoryField.getText();
			files = GetFileList(directory);
			UpdateBeforePreview();
			UpdateAfterPreview();
		}
	}
	else if(e.getSource() == startButton)
	{
		if(directory.isEmpty())
		{
			ShowErrorMessage("No directory selected");
			return;
		}
		
		directoryPath = Paths.get(directory);
		
		if(Files.exists(directoryPath))
		{
			if(IsValidInput(targetField.getText().toString()))
			{
				if(!isReplace)
				{
					if(ConfirmChoice())
					{
						RenameFiles();
					}
				}
				else
				{
					if(IsValidInput(replacementField.getText().toString()))
					{
						if(ConfirmChoice())
						{
							RenameFiles();
						}
					}
				}
			}
			else
			{
				ShowErrorMessage("Invalid characters entered");
			}
		}
		else
		{
			ShowErrorMessage("Invalid directory");
		}
	}
	else if(e.getSource() == modeBox)
	{
		switch((EditMode)modeBox.getSelectedItem())
		{
		case REMOVE:
			targetLabel.setText("Remove: ");
			HideReplacementField();
			break;
		case APPEND:
			targetLabel.setText("Append: ");
			HideReplacementField();
			break;
		case PREPEND:
			targetLabel.setText("Prepend: ");
			HideReplacementField();
			break;
		case REPLACE:
			if(!isReplace)
			{
				isReplace = true;
				ShowReplacementField();
			}
			break;
		default:
			break;
		}
		
		UpdateAfterPreview();
	}
}

boolean ConfirmChoice()
{
	int choice = JOptionPane.showConfirmDialog(frame, "This may edit the names of some or all files in the chosen directory.\n\nAre you sure this is okay?", "WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	if(choice == JOptionPane.YES_OPTION)
	{
		return true;
	}
	else
	{
		return false;
	}
}

boolean IsValidInput(String input)
{
	try 
	{
		Paths.get(input + ".txt");
		return true;
	}
	catch(InvalidPathException ex)
	{ 
		return false;
	}
}

void ShowReplacementField()
{
	targetLabel.setText("Replace: ");
	frame.add(replacementField);
	frame.add(replacementLabel);
	startButton.setBounds(250, 200, 100, 30);
	frame.repaint();
}

void HideReplacementField()
{
	isReplace = false;
	startButton.setBounds(250, 160, 100, 30);
	frame.remove(replacementField);
	frame.remove(replacementLabel);
	frame.repaint();
}

void RenameFiles()
{
	target = targetField.getText();
	switch((EditMode)modeBox.getSelectedItem())
	{
	case REMOVE:
		RemoveFromFileNames(target, files);
		break;
	case APPEND:
		AppendToFileNames(target, files);
		break;
	case PREPEND:
		PrependToFileNames(target, files);
		break;
	case REPLACE:
		ReplaceInFileNames(target, replacementField.getText(), files);
		break;
	default:
		break;
	}
	files = GetFileList(directory);
	UpdateBeforePreview();
	UpdateAfterPreview();
	JOptionPane.showMessageDialog(null, "Files renamed", "Success!", JOptionPane.INFORMATION_MESSAGE);
}

void RemoveFromFileNames(String target, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		newName = GetNewName(name, EditMode.REMOVE);
		oldPath = Paths.get(String.format("%s\\%s", directory, name));
		newPath = Paths.get(String.format("%s\\%s", directory, newName));

		try
		{
			Files.move(oldPath, newPath);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

void ReplaceInFileNames(String toReplace, String replacement, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		newName = GetNewName(name, EditMode.REPLACE);
		oldPath = Paths.get(String.format("%s\\%s", directory, name));
		newPath = Paths.get(String.format("%s\\%s", directory, newName));

		try
		{
			Files.move(oldPath, newPath);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

void AppendToFileNames(String toAppend, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		newName = GetNewName(name, EditMode.APPEND);
		oldPath = Paths.get(String.format("%s\\%s", directory, name));
		newPath = Paths.get(String.format("%s\\%s", directory, newName));

		try
		{
			Files.move(oldPath, newPath);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

void PrependToFileNames(String toPrepend, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		newName = GetNewName(name, EditMode.PREPEND);
		oldPath = Paths.get(String.format("%s\\%s", directory, name));
		newPath = Paths.get(String.format("%s\\%s", directory, newName));

		try
		{
			Files.move(oldPath, newPath);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

ArrayList<String> GetFileList(String dir)
{
	File[] entries = new File(dir).listFiles();
	ArrayList<String> result = new ArrayList<String>();
	for(File file : entries)
	{
		if(file.isFile())
		{
			result.add(file.getName());
		}
	}
	return result;
}

void ShowErrorMessage(String message)
{
	JOptionPane p = new JOptionPane();
	p.setMessage(message);
	JDialog alert = p.createDialog("Error");
	alert.setVisible(true);	
}

void UpdateBeforePreview()
{	
	beforePreview.setText("");
	for(int i = 0; i < 10; ++i)
	{
		if(i >= files.size())
		{
			return;
		}
		beforePreview.setText(beforePreview.getText() + files.get(i) + "\n");
	}
}

void UpdateAfterPreview()
{
	afterPreview.setText("");
	String newName;
	for(int i = 0; i < 10; ++i)
	{
		if(i >= files.size())
		{
			return;
		}
		newName = GetNewName(files.get(i), (EditMode)modeBox.getSelectedItem());
		afterPreview.setText(afterPreview.getText() + newName + "\n");
	}
}

String GetNewName(String old, EditMode mode)
{
	String result = old;
	fileExtension = old.substring(old.length()-4);
	result = result.replace(fileExtension, "");
	
	switch(mode)
	{
	case REMOVE:
		result = result.replace(targetField.getText(), "");
		break;
	case REPLACE:
		result = result.replace(targetField.getText(), replacementField.getText());
		break;
	case APPEND:
		result += targetField.getText();
		break;
	case PREPEND:
		result = targetField.getText() + result;
		break;
	default:
		break;
	}
	
	result += fileExtension;
	return result;
}

}
