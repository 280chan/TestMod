package powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

import christmasMod.actions.PlaguePoisonActAction;

public class PlagueActPower extends AbstractTestPower {
	public static final String POWER_ID = "PlagueActPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public PlagueActPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0];
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (c.type == CardType.POWER)
			for (AbstractCreature m : AbstractDungeon.getCurrRoom().monsters.monsters)
				if (!m.isDeadOrEscaped())
					this.actPower(m);
	}
	
	private void actPower(AbstractCreature m) {
		if (m.hasPower(PoisonPower.POWER_ID))
			this.addToTop(new PlaguePoisonActAction(this.owner, m.getPower(PoisonPower.POWER_ID)));
		if (m.hasPower(ConstrictedPower.POWER_ID))
			m.getPower(ConstrictedPower.POWER_ID).atEndOfTurn(false);
	}

}
