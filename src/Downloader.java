import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Downloader {
	private String result = null;
	
	public Downloader(String url, Callback callback) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF8"));
			String line;
			StringBuffer output = new StringBuffer();
			
			while((line = input.readLine()) != null) {
				output.append(line);
				output.append("\n");
			}
			
			input.close();
			callback.run(output.toString());
			this.result = callback.getOutput();
		} catch (IOException e) {
			callback.run(null);
			this.result = callback.getOutput();
		}
	}
	
	public String getCallbackResult() {
		return this.result;
	}
}
