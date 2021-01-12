package client;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileCopyClient {
	public static void main(String[] args) throws Exception{
		System.out.println("请输入你要拷贝的文件夹路径, 例如 C:/example");
		Scanner s = new Scanner(System.in);
		String source = s.nextLine();
		System.out.println("请输入服务器域名或IP地址: ");
		String ipAddress = s.nextLine();
		s.close();
		File sourceFile = new File(source);
		List<File> fileList = new ArrayList<>();
		getAllFiles(sourceFile, fileList);
		transfer(fileList, ipAddress);
	}
	
	public static void getAllFiles(File sourceFile, List<File> fileList) {
		File[] listFiles = sourceFile.listFiles();
		if (listFiles != null && listFiles.length > 0) {
			for (File file : listFiles) {
				if (file.isDirectory()) {
					getAllFiles(file, fileList);
				} else {
					fileList.add(file);
				}
			}
		}
	}
	
	private static void transfer(List<File> fileList, String ipAddress) throws Exception {
		int current = fileList.size();
		for (File file : fileList) {
			Socket socket = new Socket(ipAddress, 9000);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			
			System.out.println("总文件数： " + fileList.size() + ", 剩余文件：" + --current);
			System.out.println();
			
			int c = 0;
			int buffer = 8192;
			long overLength = 0;
			long fileLength = file.length();
			String absolutePath = file.getAbsolutePath();
			byte[] buff = new byte[buffer];
			
			// 路径
			out.writeUTF(absolutePath);
			out.flush();
			// 总文件数量
			out.writeLong(fileList.size());
			out.flush();
			// 当前文件数量
			out.writeInt(current);
			out.flush();
			// 当前文件长度
			out.writeLong(fileLength);
			out.flush();
			
			while ( ( c = bis.read(buff)) != -1) {
				overLength += c;
				double overLengthD = (double) overLength;
				double fileLengthD = (double) fileLength;
				long over = (long) (overLengthD / fileLengthD * 100);
				String progress = absolutePath + " >>> 已完成 :" + over + "%\r";
				System.out.print(progress);
				out.write(buff, 0, c);
			}
			
			out.flush();
			bis.close();
			out.close();
			socket.close();
			System.out.println();
			System.out.println("============================================");
		}
	}
}
