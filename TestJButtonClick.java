package myChat;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class TestJButtonClick implements ActionListener{
	public TestJButtonClick(){
		JFrame f = new JFrame("������ť�¼�");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = f.getContentPane();
		JButton b = new JButton("Press Me!");
		b.addActionListener(this);
		c.add(b,"Center");
		f.setSize(200,100);
		f.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e){
		JOptionPane.showMessageDialog(null, "�㵥���˰�ť\"" + e.getActionCommand() + "\"","��ʾ", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void main(String args[]){
		new TestJButtonClick();
	}
}
