import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

public class Extractor {
	private String version = null;
	private String original = null;
	private String functions = null;
	private LinkedHashMap<String, String> names = null;
	private String loader = null;
	
	public Extractor() {
		this.version = this.getVersion();
		this.prepare();
		this.debug("Version: " + this.version);
		this.download();
		this.parse();
		this.extract();
		this.postprare();
	}
	
	private String getVersion() {
		Downloader download = new Downloader("https://preview.knuddels.de/", new Callback() {
			public void run(String output) {
				Matcher matches = Pattern.compile("main.([a-zA-Z0-9]+).min.js").matcher(output);

			    while(matches.find()) {
			    	this.output = matches.group(1);
			    }
			}
		});
		
		return download.getCallbackResult();
	}
	
	private void debug(String message) {
		System.out.println(message);
	}
	
	private void prepare() {
		new File("./temp").mkdirs();
		new File("./temp/" + this.version).mkdirs();
		new File("./temp/" + this.version + "/functions").mkdirs();
	}
	
	private void download() {
		File output = new File("./temp/" + this.version + "/original.js");
		
		if(output.exists()) {
			this.debug("Original already downloaded.");
			this.original = Loader.get("./temp/" + this.version + "/original.js");
			return;
		}
		
		this.debug("Downloading original...");
		
		Downloader download = new Downloader("https://preview.knuddels.de/js/main." + this.version + ".min.js", new Callback() {
			public void run(String output) {
				this.output = output;
			}
		});
		
		this.original = download.getCallbackResult();
		new Saver("./temp/" + this.version + "/original.js", this.original);
	}
	
	private void parse() {
		Matcher matches = Pattern.compile("!function\\([a-z]+\\)\\{(.*)\\}\\(\\[function").matcher(this.original);
		String start = "}([";
		String end = "]);";
		String functions = "";
		
	    while(matches.find()) {
	    	start = matches.group(1) + start;
	    }
	    
	    functions	= this.original.substring(start.length() + 13, this.original.length() - end.length() - 1);
		
		this.debug("Found Loader,...");
		this.debug("\t" + start + end);
		this.loader = "!function(e){" + start + end;
		new Saver("./temp/" + this.version + "/loader.js", this.loader);
		
		this.debug("Extract Functions,...");
		this.functions = functions;
		new Saver("./temp/" + this.version + "/functions.js", functions);
	}
	
	private void extract() {
		this.debug("Extract all Functions,...");
		
		Context cx = Context.enter();
		cx.setLanguageVersion(Context.VERSION_ES6);
		cx.setOptimizationLevel(-1);
		
        try {
        	ScriptableObject scope	= cx.initStandardObjects(null, true);
            cx.evaluateString(scope, this.createInjector(this.functions), "injector", 1, null);
            scope.sealObject();
            
            NativeArray funcs	= (NativeArray) scope.get("output");
            int size			= funcs.size();
            String[] array		= new String[size];
            this.names			= new LinkedHashMap<String, String>();
            
            for(int position = 0; position < size; position++) {
            	System.out.print(".");
                String number		= this.zeroize(position + 1);
                Object entry		= funcs.get(position);
                
                if(entry == null) {
                	this.names.put(this.zeroize(position), "");
                	continue;
                }
                
                array[position]		= ((String) entry).trim();
                String content		= array[position];
                
                String file = "./temp/" + this.version + "/functions/func_" + number + ".js";
                String body = content.substring(0, content.length() - 1);
                
                this.names.put(this.zeroize(position), "func_" + number);
            	
        		new Saver(file, Beautify.run(this.injectName("func_" + number, body)));	
            }
        } finally {
            Context.exit();
        }
	}
	
	private String zeroize(int input) {
		return String.format("%03d", input);
	}
	
	private String injectName(String name, String content) {
		return "function " + name + content.substring("function (".length());
	}
	
	private String createInjector(String array) {
		return Loader.get("./Injector.js").replace("$ARRAY", array);
	}
	
	private void postprare() {
		this.debug("Execute POST-Process...");
		this.debug("Available Functions: " + this.names.size());
		
		String opened			= "([";
		String closed			= "]);";
		String prepared			= this.loader.substring(0, this.loader.length() - (opened + closed).length());
		StringBuffer functions	= new StringBuffer();
		
		for(Map.Entry<String, String> entry : this.names.entrySet()) {
			String key		= entry.getKey();
		    String value	= entry.getValue();

		    functions.append("/* n(" + Integer.parseInt(key) + ") */\t");
		    functions.append(value);
		    functions.append(",\n\t");
		}
		
		new Saver("./temp/" + this.version + "/manipulated.js", Beautify.run(prepared + opened + "\n\t" +  functions.toString() + "\n" + closed));
		this.debug("Process Finished!");
	}
}
