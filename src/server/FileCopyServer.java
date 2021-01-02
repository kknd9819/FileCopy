package server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileCopyServer {
	public static void main(String[] args) throws Exception{
		System.out.println("请输入文件存放路径: ");
		Scanner s = new Scanner(System.in);
		String target = s.nextLine();
		s.close();
		File targetFile = new File(target);
		if (!targetFile.exists()) {
			Files.createDirectory(targetFile.toPath());
		}
		ServerSocket serverSocket = new ServerSocket(9000);
		System.out.println("服务已启动, 监听9000端口, 等待客户端连接....");
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(processors);
		while (true) {
			Socket socket = serverSocket.accept();
			service.submit(new FileCopyThread(socket, target));
		}
	}
}
