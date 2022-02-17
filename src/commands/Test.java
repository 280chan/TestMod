package commands;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

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
		AbstractDungeon.getMonsters().monsters.forEach(m -> m.powers.stream().filter(p -> p.type == PowerType.DEBUFF)
				.forEach(p -> this.atb(new RemoveSpecificPowerAction(m, m, p))));
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
