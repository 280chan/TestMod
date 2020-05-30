package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.QueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BloodSacrificeSpiritualizationPlayAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardGroup g;
	private AbstractPlayer p;
	private AbstractCard c;

	public BloodSacrificeSpiritualizationPlayAction(AbstractPlayer p, CardGroup sourceGroup, AbstractCard c) {
		this.actionType = ActionType.USE;
		this.duration = DURATION;
		this.p = p;
		this.c = c;
		this.g = sourceGroup;
	}

	private void init() {
		ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!checkMonster(m))
				list.add(m);
		if (!list.isEmpty())
			this.target = list.get(AbstractDungeon.cardRandomRng.random(0, list.size() - 1));
	}
	
	private boolean checkMonster(AbstractMonster m) {
		return m.isDead || m.halfDead || m.escaped || m.isEscaping || m.isDying;
	}
	
	@Override
	public void update() {
		if (this.duration == DURATION) {
			this.init();
			this.playCard();
		}
		tickDuration();
	}

	private void playCard() {
        this.g.group.remove(this.c);
        AbstractDungeon.getCurrRoom().souls.remove(this.c);
        this.c.freeToPlayOnce = true;
        this.p.limbo.group.add(this.c);
        this.c.current_y = (-200.0F * Settings.scale);
        this.c.target_x = (Settings.WIDTH / 2.0F + 200.0F * Settings.scale);
        this.c.target_y = (Settings.HEIGHT / 2.0F);
        this.c.targetAngle = 0.0F;
        this.c.lighten(false);
        this.c.drawScale = 0.12F;
        this.c.targetDrawScale = 0.75F;
		if (!this.c.canUse(this.p, (AbstractMonster) this.target)) {
			AbstractDungeon.actionManager.addToTop(new UnlimboAction(this.c));
			AbstractDungeon.actionManager.addToTop(new DiscardSpecificCardAction(this.c, AbstractDungeon.player.limbo));
			AbstractDungeon.actionManager.addToTop(new WaitAction(0.4F));
		} else {
			this.c.applyPowers();
			AbstractDungeon.actionManager.addToTop(new QueueCardAction(this.c, this.target));
			AbstractDungeon.actionManager.addToTop(new UnlimboAction(this.c));
			AbstractDungeon.actionManager.addToTop(new WaitAction(Settings.ACTION_DUR_FASTER));
		}
		this.isDone = true;
	}
	
}
