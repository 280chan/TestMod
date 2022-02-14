package commands;

import java.util.ArrayList;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AscensionSet extends TestCommand {
	public void execute(String[] tokens, int depth) {
		try {
			int i = Integer.parseInt(tokens[2]);
			AbstractDungeon.ascensionLevel = i;
		} catch (Exception e) {
			Ascension.cmdHelp();
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
