package powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import utils.MiscMethods;

public class DisillusionmentEchoPower extends AbstractTestPower implements MiscMethods {
	public static final String POWER_ID = "DisillusionmentEchoPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public DisillusionmentEchoPower(AbstractCreature owner, int amount) {
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
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.purgeOnUse && AbstractDungeon.player.cardsPlayedThisTurn < 2) {
			flash();
			for (int i = 0; i < this.amount; i++) {
				playAgain(card, action);
			}
		}
	}

	private void playAgain(AbstractCard card, UseCardAction action) {
		this.playAgain(card, action.target == null ? null : (AbstractMonster) action.target);
	}
    
}
