package commands;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mymod.TestMod;

public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		TestMod.info("准备排布遗物");
		AbstractDungeon.player.reorganizeRelics();
		TestMod.info("排布遗物完成");
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
