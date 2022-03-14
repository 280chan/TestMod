package testmod.commands;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import basemod.ReflectionHacks;

public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		//this.print("手牌上限：" + BaseMod.MAX_HAND_SIZE);
		/*Loader.getWorkshopInfos().forEach(i -> {
			i.getID();
		});*/
		this.print("img是否为null" + (ReflectionHacks.getPrivate(AbstractDungeon.getCurrRoom().event, AbstractEvent.class, "img") == null));
		this.print("type: " + AbstractEvent.type);
		// TODO
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
