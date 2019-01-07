package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Server_application extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;

	ServerSocket server;
	ServerThread main_thread;
	ArrayList<WorkThread> totleWorkThread;
	private JTextField textField_totleclient;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server_application frame = new Server_application();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Server_application() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					for (WorkThread workThread : totleWorkThread) {
						OutputStream outToClient = workThread.socket.getOutputStream();
						DataOutputStream send = new DataOutputStream(outToClient);
						send.writeUTF("quit");
						workThread.stop();
					}
					totleWorkThread.clear();

					server.close();
					textField_totleclient.setText(String.valueOf(totleWorkThread.size()));
					if (main_thread.isAlive()) {
						main_thread.stop();
					}
					textArea.append("server has closed...");

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		setTitle("\u6570\u636E\u5E93\u670D\u52A1\u5668");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 446);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// start the server
		JButton button = new JButton("\u5F00\u542F\u670D\u52A1");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int port = Integer.parseInt(textField.getText().trim());
				try {
					server = new ServerSocket(port);
					totleWorkThread = new ArrayList<Server_application.WorkThread>();
					main_thread = new ServerThread();
					main_thread.start();
					textArea.append("server is open...\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});
		button.setBounds(219, 25, 93, 23);
		contentPane.add(button);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 148, 414, 259);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);

		JLabel label = new JLabel("\u7AEF\u53E3\uFF1A");
		label.setBounds(64, 28, 54, 15);
		contentPane.add(label);

		textField = new JTextField();
		textField.setText("6666");
		textField.setBounds(108, 24, 79, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		// shutdown server
		JButton btnNewButton = new JButton("\u5173\u95ED\u670D\u52A1");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					for (WorkThread workThread : totleWorkThread) {
						OutputStream outToClient = workThread.socket.getOutputStream();
						DataOutputStream send = new DataOutputStream(outToClient);
						send.writeUTF("quit");
						workThread.stop();
					}
					totleWorkThread.clear();

					server.close();
					textField_totleclient.setText(String.valueOf(totleWorkThread.size()));
					if (main_thread.isAlive()) {
						main_thread.stop();
					}
					textArea.append("server has closed...");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(323, 24, 101, 23);
		contentPane.add(btnNewButton);

		JLabel label_1 = new JLabel("\u5728\u7EBF\u5BA2\u6237\u7AEF\u6570\uFF1A");
		label_1.setBounds(10, 109, 93, 15);
		contentPane.add(label_1);

		textField_totleclient = new JTextField();
		textField_totleclient.setText("0");
		textField_totleclient.setBounds(108, 105, 79, 21);
		contentPane.add(textField_totleclient);
		textField_totleclient.setColumns(10);

		JButton button_1 = new JButton("\u6E05\u7A7A\u663E\u793A");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textArea.setText(null);
			}
		});
		button_1.setBounds(323, 106, 101, 23);
		contentPane.add(button_1);

		JLabel label_2 = new JLabel("\u6700\u5927\u8FDE\u63A5\u6570:");
		label_2.setBounds(25, 65, 87, 16);
		contentPane.add(label_2);

		textField_1 = new JTextField();
		textField_1.setText("2");
		textField_1.setBounds(108, 60, 79, 26);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
	}

	// main thread
	class ServerThread extends Thread {
		public void run() {
			while (true) {
				try {
					Socket socket = server.accept();
					WorkThread workthread = new WorkThread(socket);
					workthread.start();
					totleWorkThread.add(workthread);
					if (totleWorkThread.size() > Integer.parseInt(textField_1.getText())) {
						textArea.append("The client tried to connect !!!\n");
						textField_totleclient.setText(String.valueOf(totleWorkThread.size()));
					} else {
						textArea.append("client connect success...\n");
						textField_totleclient.setText(String.valueOf(totleWorkThread.size()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	// work thread
	class WorkThread extends Thread {
		private Socket socket;

		public WorkThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			if (totleWorkThread.size() > Integer.parseInt(textField_1.getText())) {
				OutputStream outToClient;
				try {
					outToClient = socket.getOutputStream();
					DataOutputStream send = new DataOutputStream(outToClient);
					send.writeUTF("full");
					totleWorkThread.remove(this);
					this.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			while (true) {
				try {

					OutputStream outToClient = socket.getOutputStream();
					DataOutputStream send = new DataOutputStream(outToClient);
					InputStream inFromClient = socket.getInputStream();
					DataInputStream receive = new DataInputStream(inFromClient);

					String sql = receive.readUTF().trim();
					if (sql.equals("quit")) {
						textArea.append("client close connection...\n");
						totleWorkThread.remove(this);
						textField_totleclient.setText(String.valueOf(totleWorkThread.size()));
						this.stop();
					}

					textArea.append("receive:\n" + sql + "\n");

					long startTime = System.nanoTime();
					/*
					 * dosomething
					 */
					long endTime = System.nanoTime();

					send.writeUTF(" \n(" + ((endTime - startTime) / 1000000.0) + " ms)");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
