package server;

import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.ImageIO;

public class ResourceLoader {
	
	public static BufferedReader getResource(String filename) {
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)));
			return bis;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static BufferedInputStream getResourceAsStream(String filename) {
		try {
			BufferedInputStream bis = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));
			return bis;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getResourceAsString(String filename) {
		BufferedReader bis = getResource(filename);
		try {
			if(bis != null) {
				String out = "", line = "";
				while ((line = bis.readLine()) != null) {
					out = out + line + "\n";
				}
				return out;
			} else return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			if(bis != null) {try {bis.close();} catch (IOException e) {}}
		}
	}
	
	public static byte[] getResourceAsBytes(String filename) throws IOException {
		try(BufferedInputStream bis = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename.substring(1)))) {
			Vector<Byte> bytes = new Vector<Byte>(15000, 15000);
			int c;
			while ((c = bis.read()) != -1) {
				bytes.add(new Byte((byte)c));
			}
			bytes.trimToSize();
			byte[] byteA = new byte[bytes.size()];
			for(int l = 0; l < bytes.size(); l++) {
				byteA[l] = bytes.get(l);
			}
			return byteA;
		} catch (IOException e) {
			//e.printStackTrace();
			return new byte[0];
			//throw e;
		}
	}
}
