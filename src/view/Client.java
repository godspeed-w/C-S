package view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame {
  private JPanel contentPane;
  
  private JTextArea txtrSelectFrom;
  
  private JTextField textField_address;
  
  private JTextField textField_port;
  
  private JTextArea textArea_message;
  
  private Socket socket;
  
  JButton button_start;
  
  JButton button_close;
  
  JButton button_excute;
  
  public static int readSize = 128;
  
  public static int headLength = 364;
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
          public void run() {
            try {
              Client frame = new Client();
              frame.setVisible(true);
            } catch (Exception e) {
              e.printStackTrace();
            } 
          }
        });
  }
  
  public Client() {
    addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            try {
              if (Client.this.socket != null) {
                OutputStream outToServer = Client.this.socket.getOutputStream();
                DataOutputStream send = new DataOutputStream(outToServer);
                send.writeUTF("quit");
                Client.this.socket.close();
                Client.this.textArea_message.append("close connection...\n");
              } 
            } catch (IOException e1) {
              e1.printStackTrace();
            } 
          }
        });
    setTitle("My console - wj");
    setDefaultCloseOperation(3);
    setBounds(100, 100, 471, 466);
    this.contentPane = new JPanel();
    this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(this.contentPane);
    this.contentPane.setLayout((LayoutManager)null);
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(72, 94, 250, 72);
    this.contentPane.add(scrollPane);
    this.txtrSelectFrom = new JTextArea();
    this.txtrSelectFrom.setText("");
    this.txtrSelectFrom.setColumns(5);
    this.txtrSelectFrom.setLineWrap(true);
    scrollPane.setViewportView(this.txtrSelectFrom);
    JLabel lblNewLabel = new JLabel("输入命令");
    lblNewLabel.setBounds(10, 113, 68, 15);
    this.contentPane.add(lblNewLabel);
    this.button_excute = new JButton("执行");
    if (this.socket == null)
      this.button_excute.setEnabled(false); 
    this.button_excute.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
              OutputStream os = Client.this.socket.getOutputStream();
              BufferedOutputStream bos = new BufferedOutputStream(os);
              int length = (Client.this.txtrSelectFrom.getText().getBytes()).length;
              String message = Client.this.txtrSelectFrom.getText();
              baos.write(Client.this.int2bytes(length));
              baos.write(message.getBytes());
              Client.this.textArea_message.append("[" + new String(baos.toByteArray()) + "\n");
              bos.write(baos.toByteArray());
              bos.flush();
              InputStream is = Client.this.socket.getInputStream();
              int got = 0;
              byte[] buf = new byte[6];
              BufferedInputStream bi = new BufferedInputStream(is);
              baos = new ByteArrayOutputStream();
              int i = bi.read(buf);
              int total = Client.this.bytes2int(buf);
              while (i != -1) {
                if (i < Client.readSize) {
                  byte[] lastByte = new byte[i];
                  System.arraycopy(buf, 0, lastByte, 0, i);
                  baos.write(lastByte);
                } else {
                  baos.write(buf);
                } 
                got += i;
                if (got >= total)
                  break; 
                i = bi.read(buf);
              } 
              byte[] received = baos.toByteArray();
              String backMsg = "";
              backMsg = new String(received);
              Client.this.textArea_message.append("[\t" + backMsg + "\n");
              bi.close();
              bos.close();
              Client.this.buttonOn();
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }
        });
    this.button_excute.setBounds(332, 143, 93, 23);
    this.contentPane.add(this.button_excute);
    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(10, 176, 435, 242);
    this.contentPane.add(scrollPane_1);
    this.textArea_message = new JTextArea();
    this.textArea_message.setFont(new Font("Monospaced", 0, 14));
    scrollPane_1.setViewportView(this.textArea_message);
    JLabel label = new JLabel("服务器地址：");
    label.setBounds(24, 10, 79, 15);
    this.contentPane.add(label);
    JLabel label_1 = new JLabel("端口：");
    label_1.setBounds(62, 49, 54, 15);
    this.contentPane.add(label_1);
    this.textField_address = new JTextField();
    this.textField_address.setText("127.0.0.1");
    this.textField_address.setBounds(113, 7, 136, 21);
    this.contentPane.add(this.textField_address);
    this.textField_address.setColumns(10);
    this.textField_port = new JTextField();
    this.textField_port.setText("666");
    this.textField_port.setBounds(113, 46, 136, 21);
    this.contentPane.add(this.textField_port);
    this.textField_port.setColumns(10);
    this.button_start = new JButton("开启连接");
    this.button_start.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            String address = Client.this.textField_address.getText().trim();
            int port = Integer.parseInt(Client.this.textField_port.getText().trim());
            try {
              Client.this.socket = new Socket(address, port);
              Client.this.buttonOff();
              Client.this.textArea_message.append("connection successful...\n");
            } catch (UnknownHostException e) {
              e.printStackTrace();
            } catch (IOException e) {
              Client.this.textArea_message.append("connection failed...\n");
              e.printStackTrace();
            } 
          }
        });
    this.button_start.setBounds(332, 20, 113, 23);
    this.contentPane.add(this.button_start);
    this.button_close = new JButton("关闭连接");
    if (this.socket == null)
      this.button_close.setEnabled(false); 
    this.button_close.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            try {
              if (Client.this.socket.isConnected() || Client.this.socket != null) {
                OutputStream outToServer = Client.this.socket.getOutputStream();
                DataOutputStream send = new DataOutputStream(outToServer);
                Client.this.socket.close();
                Client.this.socket = null;
                Client.this.buttonOn();
                Client.this.textArea_message.append("close connection...\n");
              } 
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }
        });
    this.button_close.setBounds(332, 62, 113, 23);
    this.contentPane.add(this.button_close);
    JButton button_clear = new JButton("清空显示");
    button_clear.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            Client.this.textArea_message.setText((String)null);
          }
        });
    button_clear.setBounds(332, 95, 113, 23);
    this.contentPane.add(button_clear);
  }
  
  public void buttonOn() {
    this.button_start.setEnabled(true);
    this.button_close.setEnabled(false);
    this.button_excute.setEnabled(false);
  }
  
  public void buttonOff() {
    this.button_start.setEnabled(false);
    this.button_close.setEnabled(true);
    this.button_excute.setEnabled(true);
  }
  
  public byte[] int2bytes(int num) {
    byte[] b = new byte[4];
    for (int i = 0; i < 4; i++)
      b[i] = (byte)(num >>> 24 - i * 8); 
    return b;
  }
  
  public int bytes2int(byte[] b) {
    int len = b.length;
    int mask = 255;
    int temp = 0;
    int res = 0;
    for (int i = 0; i < 4; i++) {
      res <<= 8;
      temp = b[i] & mask;
      res |= temp;
    } 
    return res;
  }
}
