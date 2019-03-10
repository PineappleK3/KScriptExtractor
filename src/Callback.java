public class Callback {
	protected String output = null;
	
	public void run(String content) {
		/* Override Me */
	}
	
	public String getOutput() {
		return this.output;
	}
}
