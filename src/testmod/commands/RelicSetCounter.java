package testmod.commands;

import java.util.ArrayList;

import testmod.screens.RelicCommandSelectScreen;

public class RelicSetCounter extends TestCommand {

	public void execute(String[] tokens, int depth) {
		try {
			int i = Integer.parseInt(tokens[2]);
			new RelicCommandSelectScreen(this, i).open();
		} catch (Exception e) {
			Relic.cmdHelp();
		}
	}

	public ArrayList<String> extraOptions(String[] tokens, int depth) {
		ArrayList<String> result = smallNumbers();
	    if (tokens.length == depth + 1)
	        return result; 
		if (tokens[depth + 1].matches("\\d+"))
			complete = true;
		return result;
	}
}
