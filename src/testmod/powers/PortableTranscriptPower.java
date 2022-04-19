package testmod.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PortableTranscriptPower extends AbstractTestPower {
	
	public PortableTranscriptPower(int amount) {
		this.owner = p();
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	private void act(AbstractCard c) {
		for (int i = 0; i < this.amount; i++)
			c.use(p(), AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true));
		c.isEthereal = true;
	}
	
	public void atStartOfTurn() {
		p().discardPile.group.stream().filter(c -> c.type == CardType.POWER).forEach(this::act);
	}

}
