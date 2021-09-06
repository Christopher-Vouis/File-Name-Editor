import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNamer implements ActionListener{

JLabel directoryLabel, targetLabel, replacementLabel;
	
JTextField directoryField, targetField, replacementField;

JFrame frame;

JButton directoryButton, startButton;

JFileChooser fc;

String target, fileExtension, directory;

String[] modes = {"Remove", "Replace", "Append", "Prepend"};

Path oldPath, newPath;

JComboBox modeBox;
boolean isReplace;

FileNamer(){
	frame = new JFrame();
	fc = new JFileChooser();
	directoryLabel = new JLabel("Diectory");
	directoryField = new JTextField();

	targetLabel = new JLabel("Remove:");
	targetField = new JTextField();
	replacementField = new JTextField();
	
	directoryButton = new JButton("Directory");
	startButton = new JButton("Start");
	
	modeBox = new JComboBox(modes);
	modeBox.setBounds(450, 120, 75, 25);
	modeBox.addActionListener(this);
	isReplace = false;

	frame.setSize(800,600);
	frame.setLayout(null);
	frame.setVisible(true);

	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	directoryLabel.setBounds(10, 40, 100, 30);
	targetLabel.setBounds(10, 120, 100, 30);
	replacementLabel = new JLabel("With:");
	replacementLabel.setBounds(10, 160, 100, 30);

	directoryField.setBounds(140, 40, 300, 30);
	directoryField.setEditable(false);
	targetField.setBounds(140, 120, 300, 30);
	replacementField.setBounds(140, 160, 300, 30);

	directoryButton.setBounds(450, 40, 100, 30);
	directoryButton.addActionListener(this);

	startButton.setBounds(250, 160, 100, 30);
	startButton.addActionListener(this);

	frame.add(directoryLabel);
	frame.add(targetLabel);
	frame.add(directoryField);
	frame.add(targetField);
	frame.add(directoryButton);
	frame.add(startButton);
	frame.add(modeBox);
}

public static void main(String[] args)
{
	new FileNamer();
}

public void actionPerformed(ActionEvent e)
{
	if(e.getSource() == directoryButton)
	{
		int directory = fc.showOpenDialog(frame);
		directoryField.setText(fc.getSelectedFile().toPath().toString());
	}
	else if(e.getSource() == startButton)
	{
		System.out.println("Start");
		directory = directoryField.getText();
		RenameFiles();
	}
	else if(e.getSource() == modeBox)
	{
		switch(modeBox.getSelectedItem().toString())
		{
		case "Remove":
			targetLabel.setText("Remove: ");
			HideReplacementField();
			break;
		case "Append":
			targetLabel.setText("Append: ");
			HideReplacementField();
			break;
		case "Prepend":
			targetLabel.setText("Prepend: ");
			HideReplacementField();
			break;
		case "Replace":
			if(!isReplace)
			{
				isReplace = true;
				targetLabel.setText("Replace: ");
				frame.add(replacementField);
				frame.add(replacementLabel);
				startButton.setBounds(250, 200, 100, 30);
				frame.repaint();
			}
		default:
			break;
		}
	}
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
	ArrayList<String> entries = GetFileList(directory);
	target = targetField.getText();
	switch(modeBox.getSelectedItem().toString())
	{
	case "Remove":
		RemoveFromFileNames(target, entries);
		break;
	case "Append":
		AppendToFileNames(target, entries);
		break;
	case "Prepend":
		PrependToFileNames(target, entries);
		break;
	case "Replace":
		ReplaceInFileNames(target, replacementField.getText().toString(), entries);
		break;
	default:
		break;
	}


}

void RemoveFromFileNames(String target, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		System.out.println("Old Name: " + name);
		newName = name.replace(target, "");
		fileExtension = newName.substring(newName.length()-4);
		newName = newName.replace(fileExtension, "");
		newName = newName + fileExtension;
		System.out.println("New Name: " + newName);
		
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
		System.out.println("Old Name: " + name);
		newName = name.replace(toReplace, replacement);
		fileExtension = newName.substring(newName.length()-4);
		newName = newName.replace(fileExtension, "");
		newName = newName + fileExtension;
		System.out.println("New Name: " + newName);
	}
}

void AppendToFileNames(String toAppend, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		System.out.println("Old Name: " + name);
		fileExtension = name.substring(name.length()-4);
		newName = name.replace(fileExtension, toAppend + fileExtension);
		System.out.println("New Name: " + newName);
	}
}

void PrependToFileNames(String toPrepend, ArrayList<String> entries)
{
	String newName;
	
	for(String name : entries)
	{
		System.out.println("Old Name: " + name);
		newName = toPrepend + name;
		System.out.println("New Name: " + newName);
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

}
