package myChat;

import javax.swing.*;
import java.awt.event.*;

public class ChatLogin extends JFrame {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			//e.printStackTrace();
			System.err.println(e+"\n这里是UI界面方法");
		}
		// -----------出于无奈只能放在主函数的登陆界面-----------------
		final JFrame login = new JFrame();

		JButton jbtOK = new JButton("连接");
		JButton jbtCancel = new JButton("关闭");
		final JTextField jtfAddress1 = new JTextField(15);
		final JTextField jtfAddress2 = new JTextField(15);
		final JTextField jtfAddress3 = new JTextField(15);
		jtfAddress3.setText("localhost");

		JPanel panel = new JPanel();
		panel.add(new JLabel("账号："));
		panel.add(jtfAddress1);
		panel.add(new JLabel("密码："));
		panel.add(jtfAddress2);
		panel.add(new JLabel("输入服务器地址："));
		panel.add(jtfAddress3);
		panel.add(jbtOK);
		panel.add(jbtCancel);

		login.getContentPane().add(panel);

		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ChatClient chatClient = new ChatClient(jtfAddress3.getText().trim());
				chatClient.startWork(); //开启聊天室界面
				
				login.setVisible(false);
			}
		});
		jbtCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		login.setTitle("请登录");
		login.setSize(480, 100);
		login.setLocation(200, 100);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
		// -----------出于无奈只能放在主函数的登陆界面-----------------
	}
}
