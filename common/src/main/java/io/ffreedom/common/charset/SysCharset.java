package io.ffreedom.common.charset;

public interface SysCharset {

	String LINE_SEPARATOR = System.getProperty("line.separator");
	
	String FILE_SEPARATOR = System.getProperty("file.separator");
	
	String PATH_SEPARATOR = System.getProperty("path.separator");

}
