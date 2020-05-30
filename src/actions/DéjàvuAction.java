package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import relics.Déjàvu;

public class DéjàvuAction extends AbstractGameAction {
	public static final float DURATION = Settings.ACTION_DUR_MED;
	private Déjàvu d;
	private ArrayList<AbstractCard> list;
	
	public DéjàvuAction(Déjàvu d, ArrayList<AbstractCard> list) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.d = d;
		this.list = list;
	}
	
	@Override
	public void update() {
		this.isDone = true;
		boolean isFull = false;
		for (AbstractCard c : this.list) {
			isFull |= this.addCard(c.makeStatEquivalentCopy());
		}
		if (isFull)
			AbstractDungeon.player.createHandIsFullDialog();
		this.d.setState(false);
		this.list.clear();
		this.d.show();
	}
	
	private boolean addCard(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		if (c.costForTurn > 0) {
			c.costForTurn = 0;
			c.isCostModifiedForTurn = true;
		}
		c.exhaustOnUseOnce = true;
		c.unhover();
		c.lighten(true);
		c.setAngle(0.0F);
		c.drawScale = 0.12F;
		c.targetDrawScale = 0.75F;
		c.current_x = Settings.WIDTH / 2.0F;
		c.current_y = Settings.HEIGHT / 2.0F;
		
		if (p.hand.size() != 10) {
			p.hand.addToTop(c);
			p.hand.refreshHandLayout();
			p.hand.applyPowers();
			return false;
		} else {
		    p.discardPile.addToTop(c);
		    return true;
		}
	}
	
}
