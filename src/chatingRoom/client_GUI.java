package chatingRoom;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class client_GUI extends JFrame {
	private int roomNum;
	private String Name;
	Container contentPane;
	JButton ������;
	JTextField textField;
	JTextArea textArea;
	JTextArea textArea_1;
	JTextArea textArea_2;
	JScrollPane chatscroll;
	JScrollPane onlinescroll;
	JButton �ʴ�;
	//////////////////////////////////////////�������� �ʴ�
	inviting inviter;
	JPanel contentPane3;
	JList list;
	JScrollPane scrollPane;
	JButton btnNewButton;
	JButton btnNewButton_1;
	
	
	public client_GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		������ = new JButton("\uB098\uAC00\uAE30");
		������.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.out.println(roomNum + "#" + "!exit" + "#" + Name);
				dispose();
			}
		});
		������.setBounds(328, 214, 90, 27);
		contentPane.add(������);

		textField = new JTextField();
		textField.addActionListener(new ActionListener() {//�Է��� ä���� ������ ����
			public void actionPerformed(ActionEvent e) {
				String sendText = textField.getText();
				if(!(sendText == "")) {
					StringBuilder sb = new StringBuilder();
					sb.append(roomNum);
					sb.append("#");
					sb.append(Name+": "+sendText);
					client.out.println(sb.toString());
					textField.setText("");
				}
			}
		});
		textField.setBounds(14, 215, 300, 24);
		contentPane.add(textField);
		textField.setColumns(10);

		textArea = new JTextArea();//ä�� ���
		textArea.setEditable(false);
		chatscroll = new JScrollPane(textArea, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatscroll.setBounds(14, 12, 300, 201);
		contentPane.add(chatscroll);

		textArea_1 = new JTextArea();//���� �� ���� ���� ���
		textArea_1.setEditable(false);
		onlinescroll = new JScrollPane(textArea_1, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		onlinescroll.setBounds(326, 38, 92, 133);
		contentPane.add(onlinescroll);

		textArea_2 = new JTextArea("Online");//�׳� online���� ���� textArea
		textArea_2.setBounds(326, 12, 92, 27);
		contentPane.add(textArea_2);
		textArea_2.setEditable(false);

		�ʴ� = new JButton("\uCD08\uB300");//�ʴ��ư
		�ʴ�.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(inviter == null) {
					inviter = new inviting();
					inviter.setVisible(true);
				}
				else {
					inviter.dispose();
					inviter = new inviting();
					inviter.setVisible(true);
				}
			}
		});
		�ʴ�.setBounds(328, 183, 90, 27);
		contentPane.add(�ʴ�);

	}
	
	//////////////////////////////////////////////////////
	//�� ������ �����Ǵ� �ΰ��� ����
	public void setRoomNum(int roomn) {
		this.roomNum = roomn;
	}
	
	public void setMyName(String name) {
		this.Name = name;
	}
	////////////////////////////////////////////////////
	
	public int getRoomNum() {
		return this.roomNum;
	}
	
	public void sentMsg(String msg) {//���� �޼����� client���� ó���ǰ� ����� ���޵�
		textArea.append(msg);
		textArea.append("\n");
		chatscroll.getVerticalScrollBar().setValue(chatscroll.getVerticalScrollBar().getMaximum());
	}
	
	public void onlineUser(String msg) {
		String[] Users = msg.split("#");
		textArea_1.setText("");
		for(int i = 2; i < Users.length; i++) {
			textArea_1.append(Users[i]);
			textArea_1.append("\n");
		}
	}

	public class inviting extends JFrame { //����ڸ� �ʴ��� �� ����ϴ� frame
		public inviting() {
			Object[] userList = client.clients.toArray();
			String lis[] = new String[20];
			
			for(int i = 0; i < userList.length; i++) {
				lis[i] = userList[i].toString();
			}
			

			setBounds(100, 100, 263, 380);
			contentPane3 = new JPanel();
			contentPane3.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane3);
			contentPane3.setLayout(null);

			list = new JList(lis);
			scrollPane = new JScrollPane(list, 
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(14, 12, 217, 238);
			contentPane3.add(scrollPane);

			DefaultListModel dl = new DefaultListModel();

			btnNewButton = new JButton("\uCD08\uB300");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dl.addElement(list.getSelectedValue());
					String str = "100#"+getRoomNum()+"#"+dl;
					client.out.println(str);
					dl.clear();
				}
			});
			btnNewButton.setBounds(14, 275, 105, 27);
			contentPane3.add(btnNewButton);

			btnNewButton_1 = new JButton("\uB098\uAC00\uAE30");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnNewButton_1.setBounds(126, 275, 105, 27);
			contentPane3.add(btnNewButton_1);
		}
	}
}
