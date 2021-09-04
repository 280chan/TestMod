package powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TaurusBlackCatPower extends AbstractTestPower {
	public static final String POWER_ID = "TaurusBlackCatPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public TaurusBlackCatPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
	private void updateEnemy(AbstractMonster m) {
		if (TaurusBlackCatEnemyPower.hasThis(m))
			this.updateAmount(m);
		else
			this.addEnemyPower(m);
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
        AbstractDungeon.getCurrRoom().monsters.monsters.forEach(this::updateEnemy);
	}
	
	private void updateAmount(AbstractMonster m) {
		AbstractPower p = TaurusBlackCatEnemyPower.getThis(m);
		p.stackPower(this.amount - p.amount);
		p.updateDescription();
	}
	
	private void addEnemyPower(AbstractMonster m) {
		m.powers.add(new TaurusBlackCatEnemyPower(m, this.amount));
	}
	
	private void removeEnemyPower(AbstractMonster m) {
		m.powers.remove(TaurusBlackCatEnemyPower.getThis(m));
	}
	
	public void onInitialApplication() {
		AbstractDungeon.getCurrRoom().monsters.monsters.forEach(this::addEnemyPower);
	}
	
	private boolean needAdd(AbstractMonster m) {
		return !TaurusBlackCatEnemyPower.hasThis(m);
	}
	
	public void atStartOfTurn() {
		AbstractDungeon.getCurrRoom().monsters.monsters.stream().filter(this::needAdd).forEach(this::addEnemyPower);
	}
	
	public void onRemove() {
		AbstractDungeon.getCurrRoom().monsters.monsters.forEach(this::removeEnemyPower);
	}
    
}
