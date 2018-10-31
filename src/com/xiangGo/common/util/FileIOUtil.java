package com.xiangGo.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class FileIOUtil {

	public static String readFileAsString(String fileName){
		return readFileAsString(new File(fileName));
	}
	
	public static String readFileAsString(File file){
        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		}  
		return readerToString(reader);
	}
	
	
	public static String toString(InputStream in){
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        return readerToString(br);
	}
	private static String readerToString(BufferedReader reader){
		
		StringBuilder sb = new StringBuilder();
		if(null == reader){
			return sb.toString();
		}

        try {  
            String tempString = null; 
            int i=0;
            while ((tempString = reader.readLine()) != null) {
            	if(i!=0){
            		sb.append("\n");
            	}
            	sb.append(tempString);
            	i++;
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        
        return sb.toString();
	}
	
	public static byte[] getBytes(String fileName) {
		return getBytes(new File(fileName));
	}

	public static byte[] getBytes(File file) {
		
		if ((file == null) || !file.exists()) {
			return null;
		}

		byte[] bytes = null;
		
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
			bytes = new byte[(int)randomAccessFile.length()];
			randomAccessFile.readFully(bytes);
			randomAccessFile.close();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}

		return bytes;
	}


	public static String read(String fileName) {
		return read(new File(fileName));
	}
	
	public static String read(File file) {
		byte[] bytes = getBytes(file);

		if (bytes == null) {
			return null;
		}

		String s = null;
		try {
			s = new String(bytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}

		return s;
	}

	public static  byte[] toByteArray(InputStream in) {
		byte[] data = null;
		
        ByteArrayOutputStream out = null;
        try{
        	out =  new ByteArrayOutputStream();
            byte[] buffer=new byte[1024*4];
            int n=0;
            while ( (n=in.read(buffer)) !=-1) {
                out.write(buffer,0,n);
            }
            data = out.toByteArray();
        }catch (Exception e) {
		}finally{
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
        
        return data;
    }
}
