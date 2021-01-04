package server;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;

import client.FileDTO;

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
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			FileDTO fileDTO = (FileDTO) in.readObject();
			String absolutePath = fileDTO.getAbsolutePath();
			String targetPath = getFilePath(target, absolutePath);
			createTargetDir(targetPath);
			
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetPath));
			System.out.println("总文件数： " + fileDTO.getTotal() + ", 剩余文件：" + fileDTO.getCurrent());
			System.out.println();
			while (true) {
				try {
					FileDTO dto = (FileDTO) in.readObject();
					out.write(dto.getBuff(), 0, dto.getC());
					out.flush();
					double overLengthD = (double) dto.getOverLength();
					double fileLengthD = (double) fileDTO.getFileLength();
					long over =  (long) (overLengthD / fileLengthD * 100);
					String progress = fileDTO.getAbsolutePath() + " >>> 已完成 :" + over + "%\r";
					System.out.print(progress);
				} catch (EOFException ex) {
					break;
				}
			}
			out.close();
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
