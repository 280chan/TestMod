package testmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TaurusBlackCatPower extends AbstractTestPower {
	private boolean removed = false;
	
	public TaurusBlackCatPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
		this.streamIfElse(AbstractDungeon.getMonsters().monsters.stream(), TaurusBlackCatEnemyPower::hasThis,
				this::updateAmount, this::addEnemyPower);
	}
	
	private void updateAmount(AbstractMonster m) {
		AbstractPower p = TaurusBlackCatEnemyPower.getThis(m);
		if (p.amount == this.amount)
			return;
		p.stackPower(this.amount - p.amount);
		p.updateDescription();
	}
	
	private boolean needUpdate(AbstractMonster m) {
		if (!TaurusBlackCatEnemyPower.hasThis(m))
			return true;
		return TaurusBlackCatEnemyPower.getThis(m).amount != this.amount;
	}
	
	private void addEnemyPower(AbstractMonster m) {
		m.powers.add(new TaurusBlackCatEnemyPower(m, this.amount));
	}
	
	private void removeEnemyPower(AbstractMonster m) {
		m.powers.remove(TaurusBlackCatEnemyPower.getThis(m));
	}
	
	public void onInitialApplication() {
		AbstractDungeon.getMonsters().monsters.forEach(this::addEnemyPower);
	}
	
	public void update(int slot) {
		super.update(slot);
		if (!this.removed && AbstractDungeon.getMonsters().monsters.stream().anyMatch(this::needUpdate))
			this.stackPower(0);
	}
	
	public void onRemove() {
		AbstractDungeon.getMonsters().monsters.forEach(this::removeEnemyPower);
		this.removed = true;
	}
    
}
