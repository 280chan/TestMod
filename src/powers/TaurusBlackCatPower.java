package powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import utils.MiscMethods;

public class TaurusBlackCatPower extends AbstractTestPower implements MiscMethods {
	public static final String POWER_ID = "TaurusBlackCatPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private boolean removed = false;
	
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
		if (this.removed)
			return;
		this.stackPower(0);
	}
	
	public void onRemove() {
		AbstractDungeon.getMonsters().monsters.forEach(this::removeEnemyPower);
		this.removed = true;
	}
    
}
