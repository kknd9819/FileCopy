package server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.nio.file.Files;

public class FileCopyThread implements Runnable{
	
	private final Socket socket;
	private final String target;
	
	public FileCopyThread(Socket socket, String target) {
		this.socket = socket;
		this.target = target;
	}

	@Override
	public void run() {
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String absolutePath = in.readUTF();
			long total = in.readLong();
			int current = in.readInt();
			long fileLength = in.readLong();
			
			String targetPath = getFilePath(target, absolutePath);
			createTargetDir(targetPath);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetPath));
			
			System.out.println("总文件数： " + total + ", 剩余文件：" + current);
			System.out.println();
			
			int c = 0;
			int buffer = 8192;
			long overLength = 0;
			byte[] buff = new byte[buffer];
			
			while ( (c = in.read(buff)) != -1 && (overLength += c) < fileLength) {
				double overLengthD = (double) overLength;
				double fileLengthD = (double) fileLength;
				long over = (long) (overLengthD / fileLengthD * 100);
				String progress = absolutePath + " >>> 已完成 :" + over + "%\r";
				System.out.print(progress);
				out.write(buff, 0, c);
			}
			
			out.flush();
			out.close();
			in.close();
			System.out.println();
			System.out.println("============================================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createTargetDir(String targetPath) throws Exception{
		int lastIndexOf = targetPath.lastIndexOf(File.separator);
		String parentPath = targetPath.substring(0, lastIndexOf);
		File file = new File(parentPath);
		if (!file.exists()) {
			Files.createDirectories(file.toPath());
		}
	}
	
	private static String getFilePath(String target, String absolutePath) {
		String drive = absolutePath.substring(0, 1);
		int indexOf = absolutePath.indexOf(File.separator);
		absolutePath = absolutePath.substring(indexOf);
		String result = target + File.separator + drive + absolutePath;
		return result;
	}
	
	public static void main(String[] args) throws Exception{
		String target = "D:\\target";
		String absolutePath = "C:\\Desktop\\aaa.txt";
		String targetPath = getFilePath(target, absolutePath);
		System.out.println(targetPath);
		
		createTargetDir(targetPath);
	}
}
