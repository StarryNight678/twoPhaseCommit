import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Coordinator {

	private static ArrayList<Socket> partList = new ArrayList<Socket>();
	private static int partCount = 3;
	private static ServerSocket server;

	static void Init() throws IOException {
		System.out.println("--Coordinator start--");
		// ����������
		server = new ServerSocket(20000);

		// Socket client;
		String strRec;
		for (int i = 0; i < partCount; i++) {
			partList.add(i, server.accept());
			Tool.sendMessage(partList.get(i), i + "\n");
			System.out.println("������" + i + " ����");
			strRec = Tool.receiveMessage(partList.get(i));
			System.out.println("���ܵ���Ϣ:" + strRec);
		}

		// server.close();

	}

	// ��һ�׶�
	static boolean theFirstStage(String inputStr) throws IOException {
		// Э���߽ڵ������в����߽ڵ�ѯ���Ƿ����ִ���ύ����������ʼ�ȴ��������߽ڵ����Ӧ��
		System.out.println(Signal.QUERY_TO_COMMIT);
		String strRec = null;
		boolean flag_step1 = true;// �Ƿ���ɵ�һ�׶εı�־

		for (int i = 0; i < partCount; i++) {
			try {
				Tool.sendMessage(partList.get(i), Signal.QUERY_TO_COMMIT + inputStr);
				strRec = Tool.receiveMessage(partList.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("!!! P"+i+" error !!!");
				return  false;
			}
			System.out.println("P" + i + " " + strRec);
			if (!strRec.equals(Signal.VOTE_YES)) {
				flag_step1 = false;
			}
		}

		return flag_step1;

	}

	// �ڶ��׶�
	static boolean theSecondStage(boolean flag_step1) throws IOException {
		boolean flag_step2 = true;
		String strRec = null;
		if (flag_step1) {
			// ��ʽ�ύ��Ϣ
			for (int i = 0; i < partCount; i++) {
				try {
					Tool.sendMessage(partList.get(i), Signal.COMMIT);
					strRec = Tool.receiveMessage(partList.get(i));
					System.out.println("P" + i + " " + strRec);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("!!! P"+i+" error !!!");
				}
				if (!strRec.equals(Signal.ACKNOWLEDGMENT)) {
					flag_step2 = false;
				}
			}
		} else {

			// û����ɣ��ع�
			for (int i = 0; i < partCount; i++) {
				try {
					Tool.sendMessage(partList.get(i), Signal.ROLLBACK);
					strRec = Tool.receiveMessage(partList.get(i));
					System.out.println(strRec);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("!!! P"+i+" error !!!");
				}
				if (!strRec.equals(Signal.ACKNOWLEDGMENT)) {
					flag_step2 = false;
				}
			}

		}

		return flag_step2;

	}

	// �ͻ�����Ϣ����
	static void processClientRequests() throws IOException {
		System.out.println("----ProcessClientRequests----");

		Socket client = server.accept();
		System.out.println("��ͻ������ӳɹ���");

		while (true) {
			// �ȴ��ͻ��˵����ӣ����û�л�ȡ����

			String clientMess = Tool.receiveMessage(client);
			System.out.println("�ͻ��˵õ���Ϣ:" + clientMess);

			// ��ʼ����ͻ�����Ϣ

			// ��һ�׶�
			boolean flag1 = theFirstStage(clientMess);		
			// �ڶ��׶�
			boolean flag2=theSecondStage(flag1);

			if( flag1 & flag2)//ֻҪ��һ���������û�����
			{
				// ���ؿͻ���  ��ȷ�����Ϣ
				Tool.sendMessage(client, Signal.OVER);
				
			}else
			{
				Tool.sendMessage(client, "error1");
			}
			
		}

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// ������ʼ��
		Init();
		// Э����.������
		processClientRequests();
	}

}
