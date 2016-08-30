import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

//工具类
public class Tool {
//接收消息
	public static String receiveMessage(Socket socket) throws IOException {
		BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String str = buf.readLine();
		return str;
	}
//发送消息
	public static void sendMessage(Socket socket, String str) throws IOException {
		PrintStream out = new PrintStream(socket.getOutputStream());
		out.println(str);
	}

}
