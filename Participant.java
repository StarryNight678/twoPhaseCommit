import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.io.FileWriter;

public class Participant {
	static private int num;
	static private Socket client;
	static private String fimeName;

	void SetNum(int num1) {
		this.num = num1;
	}

	// ��ʼ������
	static void init() throws UnknownHostException, IOException {
		// new object
		// Participant Par = new Participant();
		client = new Socket("127.0.0.1", 20000);
		// ���õȴ�ʱ��
		//client.setSoTimeout(10000);

		String s1 = Tool.receiveMessage(client);
		// ���ò����߱��
		num = Integer.parseInt(s1);
		System.out.println("������num:" + num);
		// ������ļ���
		fimeName = ".\\" + "testfile" + num + ".txt";
		
		String shealth="Participant "+num+" is ok.";
		Tool.sendMessage(client, shealth);
	}

	static boolean WriteFile(String fimeName, String str) throws IOException {

		/* д��Txt�ļ� */

		FileWriter writer = new FileWriter(fimeName, true);
		SimpleDateFormat format = new SimpleDateFormat();
		writer.write(str);
		writer.write("\n");
		writer.close();

		return true;
	}

	static boolean ReadFile(String fimeName) throws IOException {
		String line = "";
		BufferedReader in = new BufferedReader(new FileReader(fimeName));
		line = in.readLine();
		System.out.println("----this is file read----");
		while (line != null) {
			System.out.println(line);
			line = in.readLine();
		}
		in.close();

		return true;
	}

	// �����ߴ���
	static boolean participantProcess() throws IOException {
		String recStr;
		String stringToWrite=null;
		while (true) {

			// ���յ�һ����Ϣ
			recStr = Tool.receiveMessage(client);
			System.out.println("P"+num+"get first message:" + recStr);

			
			if (recStr.startsWith(Signal.QUERY_TO_COMMIT)) {
				// Ӧ����ȷ
				stringToWrite=recStr.substring(15);
				Tool.sendMessage(client, Signal.VOTE_YES);
			} else {
				// Ӧ��ʧ��ʧ��
				Tool.sendMessage(client, Signal.VOTE_NO);
			}

			// ���յڶ�����Ϣ
			recStr = Tool.receiveMessage(client);
			System.out.println("P"+num+" get second message:" + recStr);

			// Э����Э���ɹ�����ʽд���ļ�
			if (recStr.equals(Signal.COMMIT)) {

				WriteFile(fimeName, stringToWrite);
				// ������ϣ�Ӧ����Ϣ
				Tool.sendMessage(client, Signal.ACKNOWLEDGMENT);
			}

			// �õ��ع��źţ���д�ļ������س�ʼ״̬
			if (recStr.equals(Signal.ROLLBACK)) {

				// ������ϣ�Ӧ����Ϣ
				Tool.sendMessage(client, Signal.ACKNOWLEDGMENT);
				continue;
			}

		}

	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub

		// ��ʼ������
		init();
		// �����ߴ������
		participantProcess();

	}

}
