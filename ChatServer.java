package myChat;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ChatServer extends JFrame {
	private JTextArea jta = new JTextArea();// 创建一个文本显示面板
	Socket socket = null;
	private boolean[] isConnect = new boolean[100];
	private int count = 0;

	public void startWork() throws IOException {

		getContentPane().setLayout(new BorderLayout());// 建立一个Border布局
		getContentPane().add(new JScrollPane(jta), BorderLayout.CENTER);// 将文本显示框加到这个面板上
		// 窗口属性的一些设置
		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			ServerSocket serverSocket = new ServerSocket(2345);// 在2345端口建立一个服务器Socket

			jta.append("Server started at " + new Date() + '\n');// 建立服务器成功后的报告语句

			List<Socket> socketList = new ArrayList<Socket>();

			while (true) {
				socket = serverSocket.accept();// 等待客户端的接入
				count++;// 接入后客户端数目增加
				isConnect[count] = true;
				// 显示客户端的一些信息
				jta.append("\n开始接受客户端 <" + count + ">传输数据 , 时间为 " + new Date()
						+ '\n');
				InetAddress inetAddress = socket.getInetAddress();
				jta.append("客户端< " + count + ">的主机名字是 "
						+ inetAddress.getHostName() + "\n");
				jta.append("客户端 <" + count + ">的IP是 "
						+ inetAddress.getHostAddress() + "\n");

				// 将每一个连接到该服务器的客户端，加到List中

				socketList.add(socket);

				// 每一个连接到服务器的客户端，服务器开启一个新的线程来处理
				new Chat(socket, socketList).start();
			}
		} catch (IOException ex) {
			System.err.println(ex + "\n" + "这是ChatServer的startwork()");
		}
	}

	class Chat extends Thread {
		private Socket socket;
		private List<Socket> socketList;// 建立socket组
		String message = null;
		int id;

		public Chat(Socket socket, List<Socket> socketList) {// 构造函数
			this.socket = socket;
			this.socketList = socketList;
		}

		public void run() {// 服务器端的多线程的run()方法
			DataOutputStream toClient = null;
			DataInputStream fromClient = null;
			id = count;
			try {
				fromClient = new DataInputStream(socket.getInputStream());// 开启接受客户端的数据流

				InetAddress inetAddress = socket.getInetAddress();// 获取客户端地址

				for (int i = 0; i < socketList.size(); i++) {// 发送给所有用户：新用户的到来
					if (!socketList.get(i).isClosed()) {
						toClient = new DataOutputStream(socketList.get(i)
								.getOutputStream());
						if (isConnect[i] == true) {
							toClient.writeUTF("欢迎新用户"
									+ inetAddress.getHostAddress() + "\n");
						}
						toClient.flush();
					}

				}

				while (true) {

					if (isConnect[id] == true) {
						message = fromClient.readUTF();// 接受客户端的信息

						if (message.equals("end#")) {
							for (int i = 0; i < socketList.size(); i++) {//当用户退出时通知其他用户该用户退出

								toClient = new DataOutputStream(socketList.get(
										i).getOutputStream());
								if (isConnect[i + 1] == true) {
									toClient.writeUTF(inetAddress + "已经退出");
								}
								toClient.flush();

							}
							isConnect[id] = false;
						}

						else {
							// 向所有的客户端发送接收到信息，实现群聊
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
				System.err.println(e + "\n这里是run()");

			}
		}

	}

	public static void main(String[] args) throws IOException {
		ChatServer chatServer = new ChatServer();// 创建一个chatServer对象
		chatServer.startWork();// 线程开始
	}

}