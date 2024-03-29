package testmod.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

import christmasMod.actions.PlaguePoisonActAction;

public class PlagueActPower extends AbstractTestPower {
	
	public PlagueActPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (c.type == CardType.POWER)
			AbstractDungeon.getMonsters().monsters.stream().filter(this::inBattle).forEach(this::actPower);
	}
	
	private boolean inBattle(AbstractCreature m) {
		return !m.isDeadOrEscaped();
	}
	
	private void actPower(AbstractCreature m) {
		for (int i = 0; i < this.amount; i++) {
			if (m.hasPower(PoisonPower.POWER_ID))
				this.addToTop(new PlaguePoisonActAction(this.owner, m.getPower(PoisonPower.POWER_ID)));
			if (m.hasPower(ConstrictedPower.POWER_ID))
				m.getPower(ConstrictedPower.POWER_ID).atEndOfTurn(false);
		}
	}

}
