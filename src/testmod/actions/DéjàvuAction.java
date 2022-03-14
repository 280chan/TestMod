package testmod.actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import testmod.relics.Déjàvu;

public class DéjàvuAction extends AbstractGameAction {
	public static final float DURATION = Settings.ACTION_DUR_MED;
	private Déjàvu d;
	private ArrayList<AbstractCard> list;
	private static boolean flag = false;
	
	public DéjàvuAction(Déjàvu d, ArrayList<AbstractCard> list) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.d = d;
		this.list = list;
	}
	
	@Override
	public void update() {
		this.isDone = true;
		this.list.stream().sequential().map(AbstractCard::makeStatEquivalentCopy).forEach(this::addCard);
		this.d.setState(false);
		this.list.clear();
		this.d.show();
		flag = false;
	}
	
	private void addCard(AbstractCard c) {
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
		
		if (p.hand.size() < BaseMod.MAX_HAND_SIZE) {
			p.hand.addToTop(c);
			p.hand.refreshHandLayout();
			p.hand.applyPowers();
		} else {
		    p.discardPile.addToTop(c);
		    if (!flag)
				AbstractDungeon.player.createHandIsFullDialog();
		    flag = true;
		}
	}
	
}
