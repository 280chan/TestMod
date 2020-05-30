package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DrawDownPower extends AbstractPower {
	public static final String POWER_ID = "DrawDownPower";
	public static final String NAME = "抽牌变少";
	public static final String[] DESCRIPTIONS = {"下一回合少抽 #b", " 张牌。"};
	
	public DrawDownPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		loadRegion("lessdraw");
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
		AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
	}

	public void onRemove() {
		AbstractDungeon.player.gameHandSize += this.amount;
	}

}
