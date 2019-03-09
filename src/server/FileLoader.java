package server;

import java.io.IOException;

import page_gen.PageGenerator;

public class FileLoader {
	
	public static byte[] load(String path, boolean log) throws IOException {
		String ext = path.lastIndexOf(".") != -1 ? path.substring(path.lastIndexOf(".") + 1, path.length()) : "html";
		if(!ext.equals("html")) {
			byte[] stuff; stuff = ResourceLoader.getResourceAsBytes(path);
			if(stuff.length == 0) return null;
			else return stuff;
		} else {
			//byte[] stuff; stuff = ResourceLoader.getResourceAsBytes(path);
			//if(stuff.length == 0) return null;
			//return (LAYOUT_TOP + new String(stuff) + LAYOUT_BOTTOM).getBytes();
			return PageGenerator.generate(path).toBytes();
		}
	}
}
