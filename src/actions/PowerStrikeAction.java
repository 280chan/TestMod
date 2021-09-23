package actions;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;

public class PowerStrikeAction extends AbstractGameAction {

	public PowerStrikeAction(AbstractCreature source, int magicNumber) {
		this.source = source;
		this.amount = magicNumber;
		if (this.amount < 5)
			this.amount = 5;
		this.actionType = ActionType.DAMAGE;
	}

	private void modify(AbstractMonster m) {
		int tmp = (int) (Math.log(m.currentHealth) / Math.log(1 + this.amount / 10.0));
		if (tmp < m.currentHealth && tmp >= 0)
			m.currentHealth = tmp;
	}
	
	@Override
	public void update() {
		this.isDone = true;
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().monsters == null)
			return;
		if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead())
			return;
		ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
		ArrayList<AbstractMonster> kill = new ArrayList<AbstractMonster>();
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (m.isDead || m.isDying || m.isEscaping || m.escaped)
				continue;
			this.modify(m);
			if (m.currentHealth > 0) {
				list.add(m);
				m.healthBarUpdatedEvent();
			} else {
				kill.add(m);
			}
		}
		if (list.size() == 0) {
			this.addToTop(new AbstractGameAction(){
				@Override
				public void update() {
					this.isDone = true;
					AbstractDungeon.actionManager.clearPostCombatActions();
				}});
		} else {
			for (AbstractMonster m : list) {
				AbstractPower p = new IntangiblePower(m, list.size());
				p.atEndOfTurn(false);
				this.addToTop(new ApplyPowerAction(m, this.source, p, list.size()));
			}
		}
		for (AbstractMonster m : kill) {
			this.addToTop(new InstantKillAction(m));
		}
	}

}
