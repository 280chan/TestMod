package testmod.commands;

import java.util.ArrayList;
import testmod.utils.MiscMethods;

public class HandSizeAdd extends TestCommand implements MiscMethods {
	
	public void execute(String[] tokens, int depth) {
		try {
			this.updateHandSize(Integer.parseInt(tokens[2]));
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
