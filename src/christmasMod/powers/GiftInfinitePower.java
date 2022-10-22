package christmasMod.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import christmasMod.mymod.ChristmasMod;
import testmod.powers.AbstractTestPower;

public class GiftInfinitePower extends AbstractTestPower {
	public static final String POWER_ID = "GiftInfinitePower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private boolean upgraded;
	
	public GiftInfinitePower(AbstractCreature owner, int amount, boolean upgraded) {
		super(POWER_ID);
		this.upgraded = upgraded;
		this.name = NAME;
		if (this.upgraded)
			this.name += "+";
		this.ID += this.upgraded;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		 if (this.upgraded)
			 this.description += DESCRIPTIONS[2];
		 this.description += DESCRIPTIONS[3];
	}
	
	public void atStartOfTurn() {
		for (int i = 0; i < this.amount; i++) {
			this.addToTop(new MakeTempCardInHandAction(ChristmasMod.randomGift(this.upgraded)));
		}
	}

}
