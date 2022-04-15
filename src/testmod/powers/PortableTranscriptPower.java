package testmod.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class PortableTranscriptPower extends AbstractTestPower {
	public static final String POWER_ID = "PortableTranscriptPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public PortableTranscriptPower(int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = p();
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
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
