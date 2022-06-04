package testmod.commands;

import java.util.ArrayList;
import basemod.BaseMod;
import testmod.utils.HandSizeCounterUpdater;

public class HandSizeAdd extends TestCommand implements HandSizeCounterUpdater {
	
	public void execute(String[] tokens, int depth) {
		try {
			int i = Integer.parseInt(tokens[2]);
			BaseMod.MAX_HAND_SIZE += i;
			this.updateHandSize();
		} catch (Exception e) {
			HandSize.cmdHelp();
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
