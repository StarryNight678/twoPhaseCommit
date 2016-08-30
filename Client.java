import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Socket client = new Socket("127.0.0.1", 20000);
		client.setSoTimeout(10000);
		// 获取键盘输入
		System.out.println("----usage:input string to write.----");

		while(true)
		{
			System.out.println("--please input string--");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			String inputStr = input.readLine();
			Tool.sendMessage(client, inputStr);
			String s1=Tool.receiveMessage(client);
			System.out.println("get message:"+s1);
			
			if(s1.equals(Signal.OVER))
			{
				System.out.println("信息正确写入");
			}else
			{
				System.out.println("信息写入错误！！！");
			}
			
		}
	}

}
