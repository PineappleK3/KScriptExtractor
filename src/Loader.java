import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Loader {
	public static String get(String file) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line;
			StringBuffer content = new StringBuffer();
			
			while((line = input.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			
			input.close();
			return content.toString();
		} catch (IOException e) {
			/* Do Nothing */
		}
		
		return null;
	}
}
