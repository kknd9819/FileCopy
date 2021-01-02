package client;

import java.io.Serializable;

public class FileDTO implements Serializable{

	private static final long serialVersionUID = 3710268206842189076L;
	
	private String absolutePath;
	private int c;
	private long fileLength;
	private long overLength;
	private byte[] buff;
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	
	public long getOverLength() {
		return overLength;
	}
	public void setOverLength(long overLength) {
		this.overLength = overLength;
	}
	public byte[] getBuff() {
		return buff;
	}
	public void setBuff(byte[] buff) {
		this.buff = buff;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
}
