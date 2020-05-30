package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChangeBloodAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private AbstractPlayer p;
	private AbstractMonster m;
	
	public ChangeBloodAction(AbstractPlayer p, AbstractMonster m) {
		this.duration = DURATION;
		this.actionType = AbstractGameAction.ActionType.SPECIAL;
		this.source = this.p = p;
		this.m = m;
	}
	
	public void update() {
		if (this.duration == DURATION) {
			this.isDone = true;
			double rate = p.currentHealth * 1.0 / p.maxHealth + m.currentHealth * 1.0 / m.maxHealth;
	    	p.currentHealth = (int) (rate / 2 * p.maxHealth);
	    	if (p.currentHealth == 0)
	    		p.currentHealth = 1;
	    	p.healthBarUpdatedEvent();
	    	m.currentHealth = (int) (rate / 2 * m.maxHealth);
	    	if (m.currentHealth == 0)
	    		m.currentHealth = 1;
	    	m.healthBarUpdatedEvent();
		}
	}
}
