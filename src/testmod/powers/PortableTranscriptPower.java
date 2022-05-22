package testmod.powers;

import java.util.HashMap;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class PortableTranscriptPower extends AbstractTestPower {
	
	private static final HashMap<AbstractCard, Boolean> PRE = new HashMap<AbstractCard, Boolean>();
	
	public PortableTranscriptPower(int amount) {
		this.owner = p();
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	private int fakeEnergy() {
		int base = p().energy.energy;
		if (p().hasRelic("Ice Cream") || p().hasPower("Conserve"))
			base += EnergyPanel.totalCount;
		return base;
	}
	
	private void act(AbstractCard c) {
		if (c.cost == -1) {
			c.energyOnUse = fakeEnergy();
			PRE.compute(c, (a, b) -> c.freeToPlayOnce);
			c.freeToPlayOnce = true;
		}
		for (int i = 0; i < this.amount; i++)
			c.use(p(), AbstractDungeon.getRandomMonster());
		c.isEthereal = true;
		if (c.cost == -1) {
			this.addTmpActionToBot(() -> {
				c.freeToPlayOnce = PRE.getOrDefault(c, false);
				PRE.remove(c);
			});
		}
	}
	
	public void atStartOfTurn() {
		p().discardPile.group.stream().filter(c -> c.type == CardType.POWER).forEach(this::act);
	}
	
	public void onVictory() {
		PRE.clear();
	}
	
	public void onRemove() {
		PRE.clear();
	}

}
