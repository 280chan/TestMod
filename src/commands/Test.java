package commands;

import basemod.BaseMod;
import mymod.TestMod;

public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		TestMod.info("手牌上限：" + BaseMod.MAX_HAND_SIZE);
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
