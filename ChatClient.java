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

	private JTextField jtf = new JTextField(); //���뷢�͵���Ϣ

	private JTextArea jta = new JTextArea(); //��ʾ���촰�ڵ������ı�

	private JButton jbtOK = new JButton("����"); //������Ϣ��ť

	File output = new File("ChatHistory.txt"); //��������¼��ChatHistory.txt
	
	//һЩ���ڵ��йر���
	private String ipAdress;
	private JPanel panel = new JPanel();
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel chatWindow = new JPanel();
	private JPanel chatOperation = new JPanel();
	private JPanel chatHistoryPanel = new JPanel();
	private JButton button = new JButton("��������¼");
	private JTextArea chatHistory = new JTextArea();
	private final JPanel nickNamePanel = new JPanel();
	private final JTextField nickName = new JTextField();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JScrollPane scrollPane_2 = new JScrollPane();
	//private final JButton outJbt = new JButton("�˳�");
	//private Thread myThread = new Thread(new ReadMes(Socket );
	private Socket thisSocket;
	/**
	 * ���ܷ�������Ϣ
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
				fromServer = new DataInputStream(socket.getInputStream());//�������Է�������������
				
				DataOutputStream outData = new DataOutputStream(
						new FileOutputStream(output, true));//��׷����ʽ�����浽���ص������
				
				String message = null;
				while (true) {
					message = fromServer.readUTF();//���ܷ������˵���Ϣ
					outData.writeUTF(message + '\n');//�����Ϣ
					outData.flush();//��ջ�������Ϣ
					jta.append(message + '\n');//��ʾ��Ϣ
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(e+"\nReadMes��run()����");
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
	 * ������Ϣ
	 */

	class SendMes extends Thread {
		private Socket socket;

		DataOutputStream out = null;

		public SendMes(Socket socket) {
			this.socket = socket;
		}

		public void send() {
			try {
				//��ʱ���ַ�����
				SimpleDateFormat sdf = new SimpleDateFormat("",
						Locale.SIMPLIFIED_CHINESE);
				sdf.applyPattern("HH:mm:ss");
				String timeStr = sdf.format(new Date());

				toServer = new DataOutputStream(socket.getOutputStream());//����һ�����͸�����˵������
				String name = InetAddress.getLocalHost().getHostAddress();
				//�����û���Ϣ
				if (nickName.getText().trim().equals(""))
					name = InetAddress.getLocalHost().getHostAddress();
				else {
					name = nickName.getText().trim();
				}

				String usersMessage = (jtf.getText().trim());//��ȡ�ı������ı�
				String message = (name + "   [ " + timeStr + " ]"
						+ "  :\n     " + usersMessage);//���Ϸ�����Ϣ
				toServer.writeUTF(message);//������Ϣ
				jtf.setText("");//����ı���

				toServer.flush();


			} catch (IOException e) {
				
				System.err.println(e+"\nsend()��������");
			}
		}

		public void run() {
			//�ֱ�Ϊ�ı���ͷ��Ͱ�ť��Ӽ�����
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
	 * ��������Լ����캯��
	 */


	public ChatClient(String ipAString) {

		this.ipAdress = ipAString;
		chatWindow();
	}

	public void chatWindow() {//���ڲ��ּ���
		this.addWindowListener(new WindowAdapter() {//��Ӵ��ڹر��¼�
			public void windowClosing(WindowEvent we) {
				try {
					DataOutputStream sayBye = new DataOutputStream(thisSocket.getOutputStream());
					sayBye.writeUTF("end#");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		});
		
		nickName.setColumns(10);//�����ǳƵ��ı���
		
		JPanel p = new JPanel();

		p.setLayout(new BorderLayout());
		//���ڵ�һЩ����
		setTitle("Client");
		setSize(435, 468);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		//�����Ĵ���
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

		tabbedPane.addTab("�������", null, chatWindow, null);
		chatWindow.setLayout(new BorderLayout(0, 0));

		chatWindow.add(chatOperation, BorderLayout.SOUTH);
		chatOperation.setLayout(new BorderLayout(0, 0));
		chatOperation.add(jbtOK, BorderLayout.EAST);
		chatOperation.add(jtf, BorderLayout.CENTER);
		jtf.setHorizontalAlignment(JTextField.LEFT);
		JLabel label = new JLabel("���ұ�����������Ϣ");
		chatOperation.add(label, BorderLayout.WEST);
		
		//chatOperation.add(outJbt, BorderLayout.SOUTH);

		chatWindow.add(nickNamePanel, BorderLayout.NORTH);
		nickNamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		nickNamePanel.add(new JLabel("���������ǳƣ�"));

		nickNamePanel.add(nickName);

		chatWindow.add(scrollPane_1, BorderLayout.CENTER);
		scrollPane_1.setViewportView(jta);

		jta.setEditable(false);

		tabbedPane.addTab("�����¼", null, chatHistoryPanel, null);
		chatHistoryPanel.setLayout(new BorderLayout(0, 0));

		chatHistoryPanel.add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {//Ϊ��������¼��ť��ӷ���
			public void actionPerformed(ActionEvent e) {
				try {
					DataOutputStream deleteFile = new DataOutputStream(
							new FileOutputStream(output));

					deleteFile.writeUTF("");
					chatHistory.setText("��ճɹ���\n");
				} catch (IOException e1) {
					//e1.printStackTrace();
					System.err.println(e1+"\n������button.addActionListener()");
				}
			}
		});
		
		chatHistoryPanel.add(scrollPane_2, BorderLayout.CENTER);
		scrollPane_2.setViewportView(chatHistory);
		chatHistory.setEditable(false);

	}

	/**
	 * 
	 * ��ȡ��ʷ��¼
	 */

	public void historyView() {
		chatHistory.setText("");
		try {
			DataInputStream readData = new DataInputStream(new FileInputStream(
					new File("ChatHistory.txt")));//��������Ӳ�̵�������
			String msg = null;
			while ((msg = readData.readUTF()) != null) {//��ȡӲ���ϵ������¼��Ϣ
				chatHistory.append(msg);
			}
			readData.close();
		} catch (IOException e) {
			//e.printStackTrace();
			//System.err.println(e+"\n������HistoryView()");
		}

	}

	public void startWork() {

		// Login client = new Login();
		Socket socket;
		try {
			
			
			socket = new Socket(ipAdress, 2345);//����һ����Ŀ���ַ�Ͷ˿ڵ�Socket����
			
			thisSocket = socket;
			
			new ReadMes(socket).start();//��ʼ�����߳�
		
			new SendMes(socket).start();//��ʼ�����߳�
			
			
		} catch (UnknownHostException e) {
			System.err.println("������startwork()");
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("������startwork()");
		}

		
		//�ɹ����ӵ���ʾ
		jta.append("--------------------------------------------���ӳɹ�--------------------------------------------\n");
		
		
	}

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			//e.printStackTrace();
			System.err.println(e+"\n������UI���淽��");
		}
		// -----------��������ֻ�ܷ����������ĵ�½����-----------------
		final JFrame login = new JFrame();

		JButton jbtOK = new JButton("����");
		JButton jbtCancel = new JButton("�ر�");
		final JTextField jtfAddress = new JTextField(15);
		jtfAddress.setText("localhost");

		JPanel panel = new JPanel();
		panel.add(new JLabel("�����������ַ��"));
		panel.add(jtfAddress);
		panel.add(jbtOK);
		panel.add(jbtCancel);

		login.getContentPane().add(panel);

		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ChatClient chatClient = new ChatClient(jtfAddress.getText()
						.trim());
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
		login.setSize(300, 120);
		login.setLocation(200, 100);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
		// -----------��������ֻ�ܷ����������ĵ�½����-----------------

	}

}
