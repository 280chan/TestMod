package commands;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mymod.TestMod;

public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		TestMod.info("准备测试药水bug");
		AbstractDungeon.returnRandomPotion();
		TestMod.info("测试药水bug完成");
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
