import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Saver {
	public Saver(String path, String content) {
		try {
		    BufferedWriter file = new BufferedWriter(new FileWriter(path));
		    file.write(content);
		    file.close();
		} catch(IOException e) {
			/* Do Nothing */
		}
	}
}
