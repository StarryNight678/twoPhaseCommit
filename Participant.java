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
//参与者
public class Participant {
	static private int num;
	static private Socket client;
	static private String fimeName;

	void SetNum(int num1) {
		this.num = num1;
	}

	// 初始化过程
	static void init() throws UnknownHostException, IOException {
		// new object
		// Participant Par = new Participant();
		client = new Socket("127.0.0.1", 20000);
		// 设置等待时间
		//client.setSoTimeout(10000);

		String s1 = Tool.receiveMessage(client);
		// 设置参与者编号
		num = Integer.parseInt(s1);
		System.out.println("参与者num:" + num);
		// 处理的文件名
		fimeName = ".\\" + "testfile" + num + ".txt";
		
		String shealth="Participant "+num+" is ok.";
		Tool.sendMessage(client, shealth);
	}

	static boolean WriteFile(String fimeName, String str) throws IOException {

		/* 写入Txt文件 */

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

	// 参与者处理
	static boolean participantProcess() throws IOException {
		String recStr;
		String stringToWrite=null;
		while (true) {

			// 接收第一次消息
			recStr = Tool.receiveMessage(client);
			System.out.println("P"+num+"get first message:" + recStr);

			
			if (recStr.startsWith(Signal.QUERY_TO_COMMIT)) {
				// 应答正确
				stringToWrite=recStr.substring(15);
				Tool.sendMessage(client, Signal.VOTE_YES);
			} else {
				// 应答失败失败
				Tool.sendMessage(client, Signal.VOTE_NO);
			}

			// 接收第二次消息
			recStr = Tool.receiveMessage(client);
			System.out.println("P"+num+" get second message:" + recStr);

			// 协调者协调成功，正式写入文件
			if (recStr.equals(Signal.COMMIT)) {

				WriteFile(fimeName, stringToWrite);
				// 处理完毕，应答消息
				Tool.sendMessage(client, Signal.ACKNOWLEDGMENT);
			}

			// 得到回滚信号，不写文件。返回初始状态
			if (recStr.equals(Signal.ROLLBACK)) {

				// 处理完毕，应答消息
				Tool.sendMessage(client, Signal.ACKNOWLEDGMENT);
				continue;
			}

		}

	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub

		// 初始化过程
		init();
		// 参与者处理过程
		participantProcess();

	}

}
