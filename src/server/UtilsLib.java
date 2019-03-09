package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UtilsLib {

	public static final String crlf = "\r\n";
	private static HashMap<String, String> mimeMap = new HashMap<String, String>();
	static {
		String[] mimes = ResourceLoader.getResourceAsString("server/mimeTypes.txt").split("\n");
		for(int i = 0; i < mimes.length; i++) {
			mimeMap.put(mimes[i].substring(0, mimes[i].indexOf(" ")),
					mimes[i].substring(mimes[i].indexOf(" ") + 1,
					mimes[i].length()));
		}
	}
	
	public static void safeSleep(long mil) {
		try {Thread.sleep(mil);} catch(Exception e) {}
	}

	public static String toMIME(String ext) {
		return mimeMap.get(ext);
	}
	
	public static byte[] concatArrays(byte[] bs, byte[]... b2) {
	  int totalLength = bs.length;
	  for (byte[] array : b2) {
	    totalLength += array.length;
	  }
	  byte[] result = Arrays.copyOf(bs, totalLength);
	  int offset = bs.length;
	  for (byte[] array : b2) {
	    System.arraycopy(array, 0, result, offset, array.length);
	    offset += array.length;
	  }
	  return result;
	}
}
