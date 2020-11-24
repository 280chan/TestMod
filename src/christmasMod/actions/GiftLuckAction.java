package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import christmasMod.mymod.ChristmasMod;

public class GiftLuckAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	
	private int delta;
	private boolean upgraded;
	
	public GiftLuckAction(AbstractCreature source, int amount, int delta, boolean upgraded) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.source = source;
		this.amount = amount;
		this.delta = delta;
		this.upgraded = upgraded;
	}

	@Override
	public void update() {
		this.isDone = true;
		int num = AbstractDungeon.miscRng.random(amount, amount + delta);
		AbstractDungeon.actionManager.addToTop(new MakeTempCardInDiscardAction(new Dazed(), num));
		for (int i = 0; i < num; i++)
			AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(ChristmasMod.randomGift(this.upgraded)));
	}

}
