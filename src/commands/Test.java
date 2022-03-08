package commands;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

import mymod.TestMod;

public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		//TestMod.info("手牌上限：" + BaseMod.MAX_HAND_SIZE);
		/*Loader.getWorkshopInfos().forEach(i -> {
			i.getID();
		});*/
		this.print(this.isLocalTesting());
		this.print(TestMod.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		this.print(TestMod.hash(TestMod.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
