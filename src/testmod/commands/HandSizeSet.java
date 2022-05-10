package testmod.commands;

import java.util.ArrayList;

import basemod.BaseMod;
import testmod.relics.Extravagant;
import testmod.relics.HyperplasticTissue;

public class HandSizeSet extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		try {
			int i = Integer.parseInt(tokens[2]);
			BaseMod.MAX_HAND_SIZE = i;
			HyperplasticTissue.updateCounter();
			Extravagant.updateCounter();
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
