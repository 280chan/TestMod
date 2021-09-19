package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChangeBloodAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private AbstractCreature p, m;
	
	public ChangeBloodAction(AbstractPlayer p, AbstractMonster m) {
		this.duration = DURATION;
		this.actionType = AbstractGameAction.ActionType.SPECIAL;
		this.source = this.p = p;
		this.m = m;
	}
	
	private static void f(AbstractCreature c, double rate) {
		c.currentHealth = Math.max(1, (int) (rate / 2 * c.maxHealth));
    	c.healthBarUpdatedEvent();
	}
	
	public void update() {
		if (this.duration == DURATION) {
			this.isDone = true;
			double rate = p.currentHealth * 1.0 / p.maxHealth + m.currentHealth * 1.0 / m.maxHealth;
	    	f(p, rate);
	    	f(m, rate);
		}
	}
}
