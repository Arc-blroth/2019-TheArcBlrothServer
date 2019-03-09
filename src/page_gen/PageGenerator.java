package page_gen;

import java.util.concurrent.ConcurrentHashMap;

public class PageGenerator {
	
	private static ConcurrentHashMap<String, Page> pages = new ConcurrentHashMap<String, Page>();
	private static ConcurrentHashMap<String, PageLayout> layouts = new ConcurrentHashMap<String, PageLayout>();
	
	public static Page generate(String path) {
		if(!path.startsWith("/")) path = "/" + path;
		Page p = pages.get(path);
		if(p != null) return p;
		else {
			System.err.println("PageGenerator[<static>] Page " + path + " is not cached, rebuilding...");
			p = new Page(path);
			pages.put(path, p);
			return p;
		}
	}
	
	public static PageLayout getLayout(String path) {
		if(!path.startsWith("/")) path = "/" + path;
		PageLayout p = layouts.get(path);
		if(p != null) return p;
		else {
			p = new PageLayout(path);
			layouts.put(path, p);
			return p;
		}
	}

}
