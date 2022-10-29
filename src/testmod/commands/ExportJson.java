package testmod.commands;

import java.util.stream.Stream;
import testmod.mymod.TestMod;
import jc.JsonCreater;

public class ExportJson extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		JsonCreater.createJsonFromObjects("D:/SlayTheSpireMod/TestMod/testmodResources/strings/cardStat.json",
				TestMod.CARDS.stream(), c -> c.cardID + "Stat", c -> Stream.of(c.cost, c.type, c.rarity, c.target,
						c.baseBlock, c.baseDamage, c.baseMagicNumber, c.exhaust, c.isInnate, c.isEthereal));
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
	
}
