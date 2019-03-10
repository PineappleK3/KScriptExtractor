import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Resolver {
	@SuppressWarnings("serial")
	private static HashMap<String, ArrayList<String>> definitions = new HashMap<String, ArrayList<String>>() {{
		try {
			Properties properties = new Properties();
			properties.load(new BufferedInputStream(new FileInputStream("./name.map")));
			Set<Entry<Object, Object>> entries = properties.entrySet();
			
			for(Entry<Object, Object> entry : entries) {
			    put((String) entry.getKey(), new ArrayList<String>() {{
			    	String[] values = ((String) entry.getValue()).toString().split(",");
		        	for(String value : values) {
		        		add(value);
		        	}
		        }});
			}
			
			System.out.println("[Resilver] Added " + entries.size() + " definitions from name.map.");
		} catch(Exception e) {
			System.err.println("[Resolver] No name.map found, Continue.");
		}
    }};
	
	public static String test(String name, String content) {
		String returns = null;
		
		for(Entry<String, ArrayList<String>> entry : definitions.entrySet()) {
			if(returns != null) {
				break;
			}
			
			String class_name				= entry.getKey();
			ArrayList<String> definitions	= entry.getValue();
			
			for(String value : definitions) {
				if(content.contains(value)) {
					if(returns != null) {
						continue;
					}
					
					returns = class_name;
				} else {
					returns = null;
					break;
				}
			}
		}
		
		if(returns != null) {
			System.err.println("");
			System.err.println("[Resolver] " + name + " to " + returns);
			return returns;
		}
		
		return name;
	}
}
