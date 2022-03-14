package testmod.commands;

import testmod.screens.RelicCommandSelectScreen;

public class RelicAdd extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		new RelicCommandSelectScreen(this).open();
	}
	
}
