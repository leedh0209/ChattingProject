package chatingRoom;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class server {

	static Map<Integer, String[]> roomlist = new HashMap<>();
	static Map<String, PrintWriter> clients = new HashMap<>();

	public static Integer SERVER_NUMBER = 1;

	public static void main(String[] args) throws IOException {
		System.out.println("The chat server is running...");
		ExecutorService pool = Executors.newFixedThreadPool(500);
		try (ServerSocket listener = new ServerSocket(59001)) {
			while (true) {
				pool.execute(new Handler(listener.accept()));
			}
		}
	}

	private static class Handler implements Runnable {
		private String name;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);

				out.println("SUBMITNAME");
				name = in.nextLine();
				while (true) {
					if (name == null) {
						return;
					}
					synchronized (clients) {
						if (name.length() > 0 && !clients.containsKey(name)) {
							clients.put(name, out);
							break;
						}
					}
				}

				StringBuilder sb = new StringBuilder();
				for (String cl : clients.keySet()) {
					sb.append(cl);
					sb.append("#");
				} // ����ڰ� ���� ������ ����� ����� ������Ʈ ��
				for (String cl : clients.keySet()) {
					PrintWriter pw = clients.get(cl);
					pw.println("NAME#" + sb);
				} // ������Ʈ �� ����� ����� ���� ����

				while (true) {
					String input = in.nextLine();
					String[] division = input.split("#");
					int divisionNum = Integer.parseInt(division[0]);

					if (divisionNum == 99) { // �� �����
						String[] roomUser = { division[1] };
						roomlist.put(SERVER_NUMBER, roomUser);
						PrintWriter roomOwner = clients.get(division[1]);
						roomOwner.println("roomnum#" + SERVER_NUMBER);
						System.out.println("The room " + SERVER_NUMBER + " has been made");
						sendClientsList(SERVER_NUMBER);
						SERVER_NUMBER++;
					} else if (divisionNum == 100) { // �濡 �ʴ��ϱ�
						int invitingRoom = Integer.parseInt(division[1]);

						String invitedName = division[2].replace("[", "");
						invitedName = invitedName.replace("]", "");

						PrintWriter invitedUser = clients.get(invitedName);

						String[] roomUsers;
						roomUsers = roomlist.get(invitingRoom);

						if (Arrays.asList(roomUsers).contains(invitedName)) {
							continue;
						}

						invitedUser.println("!invite#" + invitingRoom);

					} else if (divisionNum == 101) {// ����ڰ� �ʴ븦 �����ߴ��� ���ߴ��� Ȯ��
						int inviteRespond = Integer.parseInt(division[2]);
						int invitingRoom = Integer.parseInt(division[1]);

						if (inviteRespond == JOptionPane.YES_OPTION) {// �ʴ���� �ο��� �ʴ븦 ������ ��
							String[] roomUsers;
							roomUsers = roomlist.get(invitingRoom);

							StringBuilder sbr = new StringBuilder();
							for (int i = 0; i < roomUsers.length; i++) {
								if (!roomUsers[i].equals("")) {
									sbr.append(roomUsers[i]);
									sbr.append("#");
								}
							}
							sbr.append(division[3]);
							sbr.append("#");

							String[] updatedRoomUser = sbr.toString().split("#");
							roomlist.put(invitingRoom, updatedRoomUser);
							
							broadcast(invitingRoom+"#User '"+division[3]+"' has entered",invitingRoom);

							sendClientsList(invitingRoom);
						} // �ʴ� ���� �� �ʴ���� ����
					} else {
						if (division[1].equals("!exit")) { // ä�ù� ����
							synchronized (roomlist) {
								String[] updatedRoomUser = roomlist.get(divisionNum);
								StringBuilder sbr = new StringBuilder();

								for (int i = 0; i < updatedRoomUser.length; i++) {
									if (!updatedRoomUser[i].equals(this.name)) {
										sbr.append(updatedRoomUser[i]);
										sbr.append("#");
									}
								} // end for

								if (!(sbr.toString().split("#") == null)) {
									roomlist.put(divisionNum, sbr.toString().split("#"));
									sendClientsList(divisionNum);
									broadcast(divisionNum+"#User '"+division[2]+"' has exited",divisionNum);
								}
								else {
									roomlist.remove(divisionNum);//���� �濡 1�� ���ٸ� �� ����
								}
							}
						} // end if(ä�ù� �����۾�)
						else { // �Ϲ� �޼��� ����
							broadcast(input, divisionNum);
						}
					}
				}

			} catch (Exception e) {
				System.out.println(e);
			} finally {
				if (name != null) {
					System.out.println(name + " is leaving");
					clients.remove(name);
					StringBuilder sb = new StringBuilder();
					for (String cl : clients.keySet()) {
						sb.append(cl);
						sb.append("#");
					} // ����ڰ� ������ ����� ����� ������Ʈ ��
					for (String cl : clients.keySet()) {
						PrintWriter pw = clients.get(cl);
						pw.println("NAME#" + sb.toString());
					} // ������Ʈ �� ����� ����� ���� ����

				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		public String getClientName() {
			return this.name;
		}

		private void broadcast(String msg, int roomNum) {
			synchronized (roomlist) {
				String[] Users = roomlist.get(roomNum);
				synchronized (clients) {
					for (int i = 0; i < Users.length; i++) {
						if (!Users[i].equals("")) {
							PrintWriter User = clients.get(Users[i]);
							User.println(msg);
						}
					}
				}
			}
		} // end broadcast

		private void send(String msg) {
			out.println(msg);
		}

		private void sendClientsList(int roomNum) {
			StringBuilder sb = new StringBuilder();

			synchronized (roomlist) {
				String[] Users = roomlist.get(roomNum);

				for (int i = 0; i < Users.length; i++) {
					if (!(Users[i] == null)) {
						sb.append(Users[i]);
						sb.append("#");
					}
				}
				broadcast("!online#" + roomNum + "#" + sb.toString(), roomNum);
			}
		} // end sendClientsList
	}
}