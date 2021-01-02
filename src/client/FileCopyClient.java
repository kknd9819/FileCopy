package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
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
		for (File file : fileList) {
			Socket socket = new Socket(ipAddress, 9000);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			FileDTO fileDTO = new FileDTO();
			fileDTO.setAbsolutePath(file.getAbsolutePath());
			fileDTO.setFileLength(file.length());
			out.writeObject(fileDTO);
			out.flush();
			
			System.out.println("开始传输：" + file.getAbsolutePath());
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			int c;
			long overLength = 0;
			byte[] buff = new byte[8192];
			while (( c = bis.read(buff)) != -1) {
				overLength += c;
				FileDTO dto = new FileDTO();
				dto.setOverLength(overLength);
				dto.setBuff(buff);
				dto.setC(c);
				out.writeObject(dto);
				out.flush();
				double overLengthD = (double) overLength;
				double fileLengthD = (double) file.length();
				long over = (long) (overLengthD / fileLengthD * 100);
				String progress = file.getAbsolutePath() + " >>> 已完成 :" + over + "%\r";
				System.out.print(progress);
			}
			bis.close();
			socket.close();
			System.out.println();
			System.out.println(file.getAbsolutePath() + " 传输完毕..");
			System.out.println();
			System.out.println("============================================");
		}
	}
}
