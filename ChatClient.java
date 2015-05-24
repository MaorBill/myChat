package myChat;

import java.awt.BorderLayout;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;

public class ChatClient extends JFrame {

	private JTextField jtf = new JTextField(); //输入发送的信息

	private JTextArea jta = new JTextArea(); //显示聊天窗口的聊天文本

	private JButton jbtOK = new JButton("发送"); //发送信息按钮

	File output = new File("ChatHistory.txt"); //输出聊天记录到ChatHistory.txt
	
	//一些窗口的有关变量
	private String ipAdress;
	private JPanel panel = new JPanel();
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel chatWindow = new JPanel();
	private JPanel chatOperation = new JPanel();
	private JPanel chatHistoryPanel = new JPanel();
	private JButton button = new JButton("清空聊天记录");
	private JTextArea chatHistory = new JTextArea();
	private final JPanel nickNamePanel = new JPanel();
	private final JTextField nickName = new JTextField();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JScrollPane scrollPane_2 = new JScrollPane();
	//private final JButton outJbt = new JButton("退出");
	//private Thread myThread = new Thread(new ReadMes(Socket );
	private Socket thisSocket;
	/**
	 * 接受服务器信息
	 */
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;


	class ReadMes extends Thread {
		private Socket socket;

		public ReadMes(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			try {
				fromServer = new DataInputStream(socket.getInputStream());//创建来自服务器的数据流
				
				DataOutputStream outData = new DataOutputStream(
						new FileOutputStream(output, true));//以追加形式创建存到本地的输出流
				
				String message = null;
				while (true) {
					message = fromServer.readUTF();//接受服务器端的信息
					outData.writeUTF(message + '\n');//输出信息
					outData.flush();//清空缓冲区信息
					jta.append(message + '\n');//显示信息
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(e+"\nReadMes的run()错误");
			} finally {
				try {
					if (fromServer != null) {
						fromServer.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 * 发送信息
	 */

	class SendMes extends Thread {
		private Socket socket;

		DataOutputStream out = null;

		public SendMes(Socket socket) {
			this.socket = socket;
		}

		public void send() {
			try {
				//将时间字符串化
				SimpleDateFormat sdf = new SimpleDateFormat("",
						Locale.SIMPLIFIED_CHINESE);
				sdf.applyPattern("HH:mm:ss");
				String timeStr = sdf.format(new Date());

				toServer = new DataOutputStream(socket.getOutputStream());//创建一个发送给服务端的输出流
				String name = InetAddress.getLocalHost().getHostAddress();
				//整合用户信息
				if (nickName.getText().trim().equals(""))
					name = InetAddress.getLocalHost().getHostAddress();
				else {
					name = nickName.getText().trim();
				}

				String usersMessage = (jtf.getText().trim());//读取文本框内文本
				String message = (name + "   [ " + timeStr + " ]"
						+ "  :\n     " + usersMessage);//整合发送信息
				toServer.writeUTF(message);//发送信息
				jtf.setText("");//清空文本框

				toServer.flush();


			} catch (IOException e) {
				
				System.err.println(e+"\nsend()函数错误");
			}
		}

		public void run() {
			//分别为文本框和发送按钮添加监听器
			jbtOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					send();
				}
			});

			jtf.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					send();
				}
			});
		}
	}

	/**
	 * 
	 * 聊天界面以及构造函数
	 */


	public ChatClient(String ipAString) {

		this.ipAdress = ipAString;
		chatWindow();
	}

	public void chatWindow() {//窗口布局集中
		this.addWindowListener(new WindowAdapter() {//添加窗口关闭事件
			public void windowClosing(WindowEvent we) {
				try {
					DataOutputStream sayBye = new DataOutputStream(thisSocket.getOutputStream());
					sayBye.writeUTF("end#");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		});
		
		nickName.setColumns(10);//输入昵称的文本框
		
		JPanel p = new JPanel();

		p.setLayout(new BorderLayout());
		//窗口的一些属性
		setTitle("Client");
		setSize(435, 468);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		//基本的窗口
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p, BorderLayout.SOUTH);
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.NORTH);

		scrollPane.setRowHeaderView(panel);
		panel.setLayout(new BorderLayout(0, 0));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				historyView();
			}

		});

		tabbedPane.addTab("聊天界面", null, chatWindow, null);
		chatWindow.setLayout(new BorderLayout(0, 0));

		chatWindow.add(chatOperation, BorderLayout.SOUTH);
		chatOperation.setLayout(new BorderLayout(0, 0));
		chatOperation.add(jbtOK, BorderLayout.EAST);
		chatOperation.add(jtf, BorderLayout.CENTER);
		jtf.setHorizontalAlignment(JTextField.LEFT);
		JLabel label = new JLabel("在右边输入您的信息");
		chatOperation.add(label, BorderLayout.WEST);
		
		//chatOperation.add(outJbt, BorderLayout.SOUTH);

		chatWindow.add(nickNamePanel, BorderLayout.NORTH);
		nickNamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		nickNamePanel.add(new JLabel("输入您的昵称："));

		nickNamePanel.add(nickName);

		chatWindow.add(scrollPane_1, BorderLayout.CENTER);
		scrollPane_1.setViewportView(jta);

		jta.setEditable(false);

		tabbedPane.addTab("聊天记录", null, chatHistoryPanel, null);
		chatHistoryPanel.setLayout(new BorderLayout(0, 0));

		chatHistoryPanel.add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {//为清空聊天记录按钮添加方法
			public void actionPerformed(ActionEvent e) {
				try {
					DataOutputStream deleteFile = new DataOutputStream(
							new FileOutputStream(output));

					deleteFile.writeUTF("");
					chatHistory.setText("清空成功！\n");
				} catch (IOException e1) {
					//e1.printStackTrace();
					System.err.println(e1+"\n这里是button.addActionListener()");
				}
			}
		});
		
		chatHistoryPanel.add(scrollPane_2, BorderLayout.CENTER);
		scrollPane_2.setViewportView(chatHistory);
		chatHistory.setEditable(false);

	}

	/**
	 * 
	 * 读取历史记录
	 */

	public void historyView() {
		chatHistory.setText("");
		try {
			DataInputStream readData = new DataInputStream(new FileInputStream(
					new File("ChatHistory.txt")));//创建来自硬盘的输入流
			String msg = null;
			while ((msg = readData.readUTF()) != null) {//读取硬盘上的聊天记录信息
				chatHistory.append(msg);
			}
			readData.close();
		} catch (IOException e) {
			//e.printStackTrace();
			//System.err.println(e+"\n这里是HistoryView()");
		}

	}

	public void startWork() {

		// Login client = new Login();
		Socket socket;
		try {
			
			
			socket = new Socket(ipAdress, 2345);//建立一个到目标地址和端口的Socket对象
			
			thisSocket = socket;
			
			new ReadMes(socket).start();//开始接受线程
		
			new SendMes(socket).start();//开始发送线程
			
			
		} catch (UnknownHostException e) {
			System.err.println("这里是startwork()");
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("这里是startwork()");
		}

		
		//成功连接的提示
		jta.append("--------------------------------------------连接成功--------------------------------------------\n");
		
		
	}

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			//e.printStackTrace();
			System.err.println(e+"\n这里是UI界面方法");
		}
		// -----------出于无奈只能放在主函数的登陆界面-----------------
		final JFrame login = new JFrame();

		JButton jbtOK = new JButton("连接");
		JButton jbtCancel = new JButton("关闭");
		final JTextField jtfAddress = new JTextField(15);
		jtfAddress.setText("localhost");

		JPanel panel = new JPanel();
		panel.add(new JLabel("输入服务器地址："));
		panel.add(jtfAddress);
		panel.add(jbtOK);
		panel.add(jbtCancel);

		login.getContentPane().add(panel);

		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ChatClient chatClient = new ChatClient(jtfAddress.getText()
						.trim());
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
		login.setSize(300, 120);
		login.setLocation(200, 100);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
		// -----------出于无奈只能放在主函数的登陆界面-----------------

	}

}
