import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
//协调者程序
public class Coordinator {

	private static ArrayList<Socket> partList = new ArrayList<Socket>();
	private static int partCount = 3;
	private static ServerSocket server;

	static void Init() throws IOException {
		System.out.println("--Coordinator start--");
		// 参与者连接
		server = new ServerSocket(20000);

		// Socket client;
		String strRec;
		for (int i = 0; i < partCount; i++) {
			partList.add(i, server.accept());
			Tool.sendMessage(partList.get(i), i + "\n");
			System.out.println("参与者" + i + " 启动");
			strRec = Tool.receiveMessage(partList.get(i));
			System.out.println("接受到消息:" + strRec);
		}

		// server.close();

	}

	// 第一阶段
	static boolean theFirstStage(String inputStr) throws IOException {
		// 协调者节点向所有参与者节点询问是否可以执行提交操作，并开始等待各参与者节点的响应。
		System.out.println(Signal.QUERY_TO_COMMIT);
		String strRec = null;
		boolean flag_step1 = true;// 是否完成第一阶段的标志

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

	// 第二阶段
	static boolean theSecondStage(boolean flag_step1) throws IOException {
		boolean flag_step2 = true;
		String strRec = null;
		if (flag_step1) {
			// 正式提交信息
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

			// 没有完成，回滚
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

	// 客户端消息处理
	static void processClientRequests() throws IOException {
		System.out.println("----ProcessClientRequests----");

		Socket client = server.accept();
		System.out.println("与客户端连接成功！");

		while (true) {
			// 等待客户端的连接，如果没有获取连接

			String clientMess = Tool.receiveMessage(client);
			System.out.println("客户端得到信息:" + clientMess);

			// 开始处理客户端消息

			// 第一阶段
			boolean flag1 = theFirstStage(clientMess);		
			// 第二阶段
			boolean flag2=theSecondStage(flag1);

			if( flag1 & flag2)//只要有一个出错就是没有完成
			{
				// 返回客户端  正确完成信息
				Tool.sendMessage(client, Signal.OVER);
				
			}else
			{
				Tool.sendMessage(client, "error1");
			}
			
		}

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// 参数初始化
		Init();
		// 协调者.处理函数
		processClientRequests();
	}

}
