package commands;

import screens.RelicCommandSelectScreen;

public class RelicAdd extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		new RelicCommandSelectScreen(this).open();
	}
	
}
