package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class DreamHousePurgeCardAction extends AbstractGameAction {
	private AbstractCard c;
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	
	public DreamHousePurgeCardAction(AbstractCard c) {
		this.duration = DURATION;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.source = AbstractDungeon.player;
		this.c = c;
	}
	
	public void update() {
		TestMod.info("梦幻馆: 尝试运行");
		if (this.duration == DURATION) {
			this.isDone = true;
			AbstractDungeon.player.masterDeck.removeCard(c);
			TestMod.info("梦幻馆: 删除" + c.name + "成功");
		}
	}
}
