package cn.bsdn.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rd-yue.yanjie
 *
 */
public class FileUtils {

	public static void main(String[] args) throws IOException {
//		String folder="E:/projects/shangqi/CTP2_UI/CTP2/web/WEB-INF/config";
//		String destFolder="E:/projects/shangqi/CTP2_UI/CTP2/web/WEB-INF/2";
//		Map<String,String> map=new HashMap<String,String>();
//		map.put("@Resource", "");
//		Integer a=Integer.valueOf("81100090450");
//		//replaceFileContent(folder,destFolder,".java",map);
//		//replaceFileContent(folder,destFolder,".java","commonReportDao","commonDao");
//		String file="D:/devtools/apache-tomcat-7.0.62/logs/catalina.2015-07-07.log";
//		//readFileRotate(file);
		String folder="E:/projects/shangqi/CTP2_UI/CTP2/src/generated/com/sfit/model/trade";
		List<File> list=getFiles(folder, ".java",null);
		List<String> filePathList=new ArrayList<String>();
		Map<String,String> javaClassMap=getJavas(list,filePathList);
		//List<String> jcList=new ArrayList<String>();
		//jcList.add("com.sfit.model.epayment.Accountregister");
		String folder2="E:/projects/shangqi/CTP2_UI/CTP2/src";
		for(String javaClass:javaClassMap.keySet()){
			String patttern1=javaClass.substring(0,javaClass.lastIndexOf(".")+1);
			String patttern2=javaClass.substring(javaClass.lastIndexOf(".")+1);
			String patterns[]={patttern1,patttern2};
			//getFilesContains(folder2, ".java","and" patttern1,patttern2);
			List<String>javaRefList=getFilesContains(folder2, ".java", "and",filePathList,patterns);
			if(javaRefList.size()<1){
				List<String>xmlRefList=getFilesContains(folder2, ".xml", "and",null,patterns);
				if(xmlRefList.size()<1){
					System.out.println("无用Java类："+javaClass);
					File f=new File(javaClassMap.get(javaClass));
					if(f.exists()){
						f.delete();
						System.out.println(" 删除类："+javaClassMap.get(javaClass));
					}
				}
			}
		}
	}
	public void convertFileFromGBKToUTF8(){
		
	}
	public static Map<String,String> getJavas(List<File> list,List<String>filePathList){
		Map<String,String> map=new HashMap<String,String>();
		for(File f:list){
			String path=f.getAbsolutePath();
			String packageName=path.substring(path.indexOf("com\\sfit"),path.lastIndexOf(".java"));
			map.put(packageName.replace("\\", "."),f.getAbsolutePath());
			filePathList.add(path);
		}
		return map;
	}
	public static void replaceFileContent(String folder,String destFolder,String endStr,Map<String,String>fromToPairs) throws IOException{
		int count=0,igoreCount=0;
		List<File> files=getFiles(folder, endStr,null);
		for(File f:files){
			String content=FileUtils.getFileContent(f.getAbsolutePath());
			String tempContent=null;
			for(String from:fromToPairs.keySet()){
				if(tempContent==null&&content.contains(from)){
					tempContent=content;
				}
				if(tempContent!=null&&tempContent.contains(from)){
					String to =fromToPairs.get(from);
					tempContent=tempContent.replace(from, to);
				}
			}
			if(tempContent!=null){
				String out=destFolder+f.getAbsolutePath().substring(folder.length());
				File outFile=new File(out);
				if(!outFile.exists()){
					if(!outFile.getParentFile().exists()){
						outFile.getParentFile().mkdirs();
					}
					outFile.createNewFile();
				}
				writeContentToFile(tempContent, out,null);
				count++;
				System.out.println(count+"."+out);
			}else{
				igoreCount++;
				System.out.println("  ignore,"+igoreCount+"."+f.getAbsolutePath());
			}
		}
	}
	public static List<String> getFilesContains(String folder,String endStr,String... patterns){
		return getFilesContains(folder, endStr, "or", null,patterns);
	}
	public static List<String> getFilesContains(String folder,String endStr,String andOr,List<String>excludeFiles,String... patterns){
		List<String> list=new ArrayList<String>();
		List<File> fileList=getFiles(folder,endStr,null);
		for(File file:fileList){
			if(excludeFiles!=null&&excludeFiles.contains(file.getAbsolutePath())){
				continue;
			}
			String content=getFileContent(file.getAbsolutePath());
			if("and".equals(andOr)){
				boolean hasFlag=true;
				for(String p:patterns){
					if(!content.contains(p)){
						hasFlag=false;
						break;
					}
				}
				if(hasFlag){
					list.add(file.getAbsolutePath());
				}
			}else if("or".equals(andOr)){
				for(String p:patterns){
					if(content.contains(p)){
						list.add(file.getAbsolutePath());
						break;
					}
				}
			}
		}
		return list;
	}
	public static List<File> getFiles(String folder, final String endStr1,final String endStr2) {
		List<File> list = new ArrayList<File>();
		File f = new File(folder);
		File[] fList = f.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && (file.getName().endsWith(endStr1)||(endStr2!=null?file.getName().endsWith(endStr2):false));
			}
		});
		File[] dirList = f.listFiles(new FileFilter() {
			public boolean accept(File dir) {
				return dir.isDirectory();
			}
		});
		if(fList!=null){
			Collections.addAll(list, fList);
		}
		if(dirList!=null){
			for (File dir : dirList) {
				List<File> cFileList = getFiles(dir.getAbsolutePath(), endStr1,endStr2);
				list.addAll(cFileList);
			}
		}
		return list;
	}
	@SuppressWarnings("resource")
	public static String getFileContent(String fileName) {
		File f = new File(fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			return new String(bytes, "utf-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	public static void readFileRotate(String file){
		try {
			@SuppressWarnings("resource")
			RandomAccessFile raf=new RandomAccessFile(file, "r");
			long length=raf.length();
			System.out.println("length:"+length);
			System.out.println("point:"+raf.getFilePointer());
			raf.seek(10);
			//int next=length
			int code=raf.read();
			while(code!=-1){
				System.out.println("point:"+raf.getFilePointer()+":"+code);
				code=raf.read();
				//System.out.println("point:"+raf.getFilePointer());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
