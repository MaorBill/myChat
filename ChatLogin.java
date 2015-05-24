package myChat;

import javax.swing.*;
import java.awt.event.*;

public class ChatLogin extends JFrame {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			//e.printStackTrace();
			System.err.println(e+"\n������UI���淽��");
		}
		// -----------��������ֻ�ܷ����������ĵ�½����-----------------
		final JFrame login = new JFrame();

		JButton jbtOK = new JButton("����");
		JButton jbtCancel = new JButton("�ر�");
		final JTextField jtfAddress1 = new JTextField(15);
		final JTextField jtfAddress2 = new JTextField(15);
		final JTextField jtfAddress3 = new JTextField(15);
		jtfAddress3.setText("localhost");

		JPanel panel = new JPanel();
		panel.add(new JLabel("�˺ţ�"));
		panel.add(jtfAddress1);
		panel.add(new JLabel("���룺"));
		panel.add(jtfAddress2);
		panel.add(new JLabel("�����������ַ��"));
		panel.add(jtfAddress3);
		panel.add(jbtOK);
		panel.add(jbtCancel);

		login.getContentPane().add(panel);

		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ChatClient chatClient = new ChatClient(jtfAddress3.getText().trim());
				chatClient.startWork(); //���������ҽ���
				
				login.setVisible(false);
			}
		});
		jbtCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		login.setTitle("���¼");
		login.setSize(480, 100);
		login.setLocation(200, 100);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
		// -----------��������ֻ�ܷ����������ĵ�½����-----------------
	}
}
