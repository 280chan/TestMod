package commands;

public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
