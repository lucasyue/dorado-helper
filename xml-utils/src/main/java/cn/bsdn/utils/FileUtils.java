package cn.bsdn.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author rd-yue.yanjie
 *
 */
public class FileUtils {

	public static void main(String[] args) throws IOException {
		String folder="E:/projects/shangqi/xml-utils/src/main/java";
		String destFolder="E:/projects/shangqi/xml-utils/src/main/java2";
		replaceFileContent(folder,destFolder,".xml","<DataType name=","$dddddd$");
	}
	public void convertFileFromGBKToUTF8(){
		
	}
	public static void replaceFileContent(String folder,String destFolder,String endStr,String from,String to) throws IOException{
		int count=0,igoreCount=0;
		List<File> files=getFiles(folder, endStr);
		for(File f:files){
			String content=FileUtils.getFileContent(f.getAbsolutePath());
			if(content.contains(from)){
				String content2=content.replace(from, to);
				String out=destFolder+f.getAbsolutePath().substring(folder.length());
				File outFile=new File(out);
				if(!outFile.exists()){
					if(!outFile.getParentFile().exists()){
						outFile.getParentFile().mkdirs();
					}
					outFile.createNewFile();
				}
				writeContentToFile(content2, out,null);
				count++;
				System.out.println(count+"."+out);
			}else{
				igoreCount++;
				System.out.println("ignore,"+igoreCount+"."+f.getAbsolutePath());
			}
		}
	}
	public static List<File> getFiles(String folder, final String endStr) {
		List<File> list = new ArrayList<File>();
		File f = new File(folder);
		File[] fList = f.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(endStr);
			}
		});
		File[] dirList = f.listFiles(new FileFilter() {
			public boolean accept(File dir) {
				return dir.isDirectory();
			}
		});
		Collections.addAll(list, fList);
		for (File dir : dirList) {
			List<File> cFileList = getFiles(dir.getAbsolutePath(), endStr);
			list.addAll(cFileList);
		}
		return list;
	}
	public static String getFileContent(String fileName) throws IOException {
		File f = new File(fileName);
		@SuppressWarnings("resource")
		FileInputStream fis = new FileInputStream(f);
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		return new String(bytes, "utf-8");
	}
	public static void writeContentToFile(String content, String file) throws IOException{
		writeContentToFile(content,file,null);
	}
	public static void writeContentToFile(String content, String file,String encode)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes(encode==null?"utf-8":encode));
		fos.close();
	}
}
