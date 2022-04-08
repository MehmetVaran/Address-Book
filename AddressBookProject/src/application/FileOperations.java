package application;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FileOperations {
	
	public static String readFixedLengthString(int size, DataInput in) throws IOException {
		char[] chars = new char[size];
		
		 for (int i = 0; i < size; i++) {
			 chars[i] = in.readChar();		 
		 }
		 
		 return new String(chars);
	}
	
	public static void writeFixedLengthString(String s, int size, DataInput out) throws IOException {
		char[] chars = new char[size];
		
		s.getChars(0, s.length(), chars, 0);
		
		for (int i = Math.min(s.length(), size); i < chars.length; i++) {
			chars[i] = ' ';
		}
		((DataOutput) out).writeChars(new String(chars));
	
	}

}
