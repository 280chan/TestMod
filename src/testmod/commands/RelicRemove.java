package testmod.commands;

import testmod.screens.RelicCommandSelectScreen;

public class RelicRemove extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		new RelicCommandSelectScreen(this).open();
	}
	
}
