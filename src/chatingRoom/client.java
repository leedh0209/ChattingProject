package chatingRoom;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class client {
	static Map<Integer, client_GUI> rooms = new HashMap<>();//자신이 접속중인 채팅방 목록
	static Set<String> clients = new HashSet<>();//전체 유저 목록(초대기능에서 사용)

	String serverAddress;
	static String name;
	Scanner in;
	public static PrintWriter out;

	initialWindow frame = new initialWindow();

	client(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	private String getName() {
		return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);

	}

	private int getInvited(int roomNum) {
		StringBuilder sb = new StringBuilder();
		sb.append(roomNum);
		sb.append("번방 에서의 초대");
		return JOptionPane.showConfirmDialog(frame, "채팅방에 초대받았습니다.", sb.toString(), JOptionPane.YES_NO_OPTION);
	}

	private void run() throws IOException {
		try {
			Socket socket = null;
			socket = new Socket(serverAddress, 59001);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);

			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.startsWith("SUBMITNAME")) {//로그인
					name = getName();
					out.println(name);
				} else if (line.startsWith("NAME")) {//전체 이름목록
					String[] names = line.split("#");
					clients.clear();
					for (int i = 1; i < names.length; i++) {
						clients.add(names[i]);
					}
				} else if (line.startsWith("roomnum")) {// 방을 만들시에 할당돼는 방 번호
					client_GUI cg = new client_GUI();
					cg.setVisible(true);
					String[] division = line.split("#");
					int divisionNum = Integer.parseInt(division[1]);
					cg.setRoomNum(divisionNum);
					cg.setMyName(name);
					rooms.put(divisionNum, cg);
				} else if (line.startsWith("!invite")) {// 초대를 받았을 때
					String[] division = line.split("#");
					int divisionNum = Integer.parseInt(division[1]);
					int yn = getInvited(divisionNum);
					out.println("101#" + divisionNum + "#" + yn + "#" + name);
					if (yn == JOptionPane.YES_OPTION) {
						client_GUI cg = new client_GUI();
						cg.setVisible(true);
						cg.setRoomNum(divisionNum);
						cg.setMyName(name);
						rooms.put(divisionNum, cg);
					} else {
					} // 초대를 거부한다면 아무것도 하지 않음
				} else if (line.startsWith("!online")) {// 특정방의 인원 목록
					String[] division = line.split("#");
					int divisionNum = Integer.parseInt(division[1]);
					client_GUI cg = rooms.get(divisionNum);

					cg.onlineUser(line); // 특정 방의 인원 목록을 해당 채팅방으로 전송
				} else { // 일반 메세지가 도착했을 때
					String[] division = line.split("#");
					int divisionNum = Integer.parseInt(division[0]);
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < division.length; i++) {
						sb.append(division[i]);
						if (!(i == division.length-1)) {
							sb.append("#"); // 채팅 메세지로 #을 사용한 경우
						}
					}
					client_GUI cg = rooms.get(divisionNum);
					cg.sentMsg(sb.toString());
				}
			}
		} finally {
			frame.setVisible(false);
			frame.dispose();
		}
	}

	public static void main(String[] args) throws Exception {
		client client = null;
		client = new client("127.0.0.1");
		client.frame.setVisible(true);
		client.run();
	}

	class initialWindow extends JFrame {// 그냥 형식상 띄워놓은 화면 닉네임 입력후 버튼 누르면 채팅창 생성
		initialWindow() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 450, 300);
			JPanel contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JButton btnNewButton = new JButton("New button");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					out.println("99#" + name);
				}
			});
			btnNewButton.setBounds(14, 60, 135, 88);
			contentPane.add(btnNewButton);
		}
	}
}