package halloweenMod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import halloweenMod.mymod.HalloweenMod;
import halloweenMod.utils.HalloweenMiscMethods;

public class GhostCostumePower extends AbstractPower implements HalloweenMiscMethods {
	public static final String POWER_ID = HalloweenMod.MOD_PREFIX + "GhostCostumePower";
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	private static final String NAME = powerStrings.NAME;
	private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final String IMG = "halloweenResources/images/image.png";
	
	public GhostCostumePower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		if (this.amount > 1 && DESCRIPTIONS.length > 2) {
			this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
		} else {
			this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		}
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
	}
	
	public void atStartOfTurnPostDraw() {
		for (int i = 0; i < this.amount; i++)
			this.addRandomPower(this.owner);
	}

}
