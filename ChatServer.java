package myChat;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ChatServer extends JFrame {
	private JTextArea jta = new JTextArea();// ����һ���ı���ʾ���
	Socket socket = null;
	private boolean[] isConnect = new boolean[100];
	private int count = 0;

	public void startWork() throws IOException {

		getContentPane().setLayout(new BorderLayout());// ����һ��Border����
		getContentPane().add(new JScrollPane(jta), BorderLayout.CENTER);// ���ı���ʾ��ӵ���������
		// �������Ե�һЩ����
		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			ServerSocket serverSocket = new ServerSocket(2345);// ��2345�˿ڽ���һ��������Socket

			jta.append("Server started at " + new Date() + '\n');// �����������ɹ���ı������

			List<Socket> socketList = new ArrayList<Socket>();

			while (true) {
				socket = serverSocket.accept();// �ȴ��ͻ��˵Ľ���
				count++;// �����ͻ�����Ŀ����
				isConnect[count] = true;
				// ��ʾ�ͻ��˵�һЩ��Ϣ
				jta.append("\n��ʼ���ܿͻ��� <" + count + ">�������� , ʱ��Ϊ " + new Date()
						+ '\n');
				InetAddress inetAddress = socket.getInetAddress();
				jta.append("�ͻ���< " + count + ">������������ "
						+ inetAddress.getHostName() + "\n");
				jta.append("�ͻ��� <" + count + ">��IP�� "
						+ inetAddress.getHostAddress() + "\n");

				// ��ÿһ�����ӵ��÷������Ŀͻ��ˣ��ӵ�List��

				socketList.add(socket);

				// ÿһ�����ӵ��������Ŀͻ��ˣ�����������һ���µ��߳�������
				new Chat(socket, socketList).start();
			}
		} catch (IOException ex) {
			System.err.println(ex + "\n" + "����ChatServer��startwork()");
		}
	}

	class Chat extends Thread {
		private Socket socket;
		private List<Socket> socketList;// ����socket��
		String message = null;
		int id;

		public Chat(Socket socket, List<Socket> socketList) {// ���캯��
			this.socket = socket;
			this.socketList = socketList;
		}

		public void run() {// �������˵Ķ��̵߳�run()����
			DataOutputStream toClient = null;
			DataInputStream fromClient = null;
			id = count;
			try {
				fromClient = new DataInputStream(socket.getInputStream());// �������ܿͻ��˵�������

				InetAddress inetAddress = socket.getInetAddress();// ��ȡ�ͻ��˵�ַ

				for (int i = 0; i < socketList.size(); i++) {// ���͸������û������û��ĵ���
					if (!socketList.get(i).isClosed()) {
						toClient = new DataOutputStream(socketList.get(i)
								.getOutputStream());
						if (isConnect[i] == true) {
							toClient.writeUTF("��ӭ���û�"
									+ inetAddress.getHostAddress() + "\n");
						}
						toClient.flush();
					}

				}

				while (true) {

					if (isConnect[id] == true) {
						message = fromClient.readUTF();// ���ܿͻ��˵���Ϣ

						if (message.equals("end#")) {
							for (int i = 0; i < socketList.size(); i++) {//���û��˳�ʱ֪ͨ�����û����û��˳�

								toClient = new DataOutputStream(socketList.get(
										i).getOutputStream());
								if (isConnect[i + 1] == true) {
									toClient.writeUTF(inetAddress + "�Ѿ��˳�");
								}
								toClient.flush();

							}
							isConnect[id] = false;
						}

						else {
							// �����еĿͻ��˷��ͽ��յ���Ϣ��ʵ��Ⱥ��
							for (int i = 0; i < socketList.size(); i++) {

								toClient = new DataOutputStream(socketList.get(
										i).getOutputStream());
								if (isConnect[i + 1] == true) {
									toClient.writeUTF(message);
								}
								toClient.flush();

							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(e + "\n������run()");

			}
		}

	}

	public static void main(String[] args) throws IOException {
		ChatServer chatServer = new ChatServer();// ����һ��chatServer����
		chatServer.startWork();// �߳̿�ʼ
	}

}