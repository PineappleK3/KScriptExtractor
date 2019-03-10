import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Beautify {
	public static boolean disabled = true;
	
	public static String run(String input) {
		return call(input);
	}
	
	private static String call(String code) {
		if(disabled) {
			return code;
		}
		
		try {
			URL url					= new URL("https://www.dsted.org/beautify/");
			URLConnection con		= url.openConnection();
			HttpURLConnection http	= (HttpURLConnection) con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			
			byte[] bytes = code.getBytes();
			int length = bytes.length;
			
			http.setFixedLengthStreamingMode(length);
			http.connect();
			try(OutputStream os = http.getOutputStream()) {
			    os.write(bytes);
				os.close();
			}
			
			BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF8"));
			String line;
			StringBuffer output = new StringBuffer();
			
			while((line = input.readLine()) != null) {
				output.append(line);
				output.append("\n");
			}
			
			input.close();
			return output.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return code;
	}
}
