package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import relics.MyRelic;

public class AssaultLearningAction extends AbstractGameAction {

	private float startingDuration;

	private MyRelic r;
	
	public AssaultLearningAction(MyRelic r) {
		this.amount = 1;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.r = r;
	}

	@Override
	public void update() {
		AbstractPlayer p = AbstractDungeon.player;
		this.isDone = true;
		if (p.drawPile.isEmpty())
			return;
		for (int i = p.drawPile.size() - 1; i >= 0; i--) {
			AbstractCard c = p.drawPile.group.get(i);
			if (c.canUpgrade()) {
				c.upgrade();
				r.show();
				return;
			}
		}
		
	}

}
