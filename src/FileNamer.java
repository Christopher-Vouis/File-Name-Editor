import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;

public class FileNamer implements ActionListener{

JLabel directoryLabel,
	   targetLabel;
	
JTextField directoryField, targetField;

JFrame frame;

JButton directoryButton, startButton;

JFileChooser fc;

String target, fileExtension;

String[] modes = {"Remove", "Replace", "Append", "Prepend"};

JComboBox modeBox;

FileNamer(){
	frame = new JFrame();
	fc = new JFileChooser();
	directoryLabel = new JLabel("Diectory");
	directoryField = new JTextField();

	targetLabel = new JLabel("Target");
	targetField = new JTextField();
	
	directoryButton = new JButton("Directory");
	startButton = new JButton("Start");
	
	modeBox = new JComboBox(modes);
	modeBox.setBounds(450, 120, 75, 25);


	frame.setSize(600,400);
	frame.setLayout(null);
	frame.setVisible(true);

	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	directoryLabel.setBounds(10, 40, 100, 30);
	targetLabel.setBounds(10, 120, 100, 30);

	directoryField.setBounds(140, 40, 300, 30);
	directoryField.setEditable(false);
	targetField.setBounds(140, 120, 300, 30);

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
		RenameFiles(directoryField.getText());
	}
}

void RenameFiles(String directory)
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
