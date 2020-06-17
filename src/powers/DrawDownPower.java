package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class DrawDownPower extends AbstractTestPower {
	public static final String POWER_ID = "DrawDownPower";
	private static final String REGION = "lessdraw";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public DrawDownPower(AbstractCreature owner, int amount) {
		super(POWER_ID, REGION);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
	public void onInitialApplication() {
		AbstractDungeon.player.gameHandSize -= this.amount;
	}

	public void stackPower(int amt) {
		super.stackPower(amt);
		AbstractDungeon.player.gameHandSize -= amt;
		updateDescription();
	}

	public void atStartOfTurnPostDraw() {
		this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
	}

	public void onRemove() {
		AbstractDungeon.player.gameHandSize += this.amount;
	}

}
