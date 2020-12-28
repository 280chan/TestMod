package christmasMod.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import christmasMod.mymod.ChristmasMod;
import powers.AbstractTestPower;

public class GiftDamagedPower extends AbstractTestPower {
	public static final String POWER_ID = "GiftDamagedPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private static boolean check = true;
	
	public static boolean hasThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof GiftDamagedPower)
				return true;
		return false;
	}
	
	public static AbstractPower getThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof GiftDamagedPower)
				return p;
		return null;
	}
	
	public GiftDamagedPower(AbstractCreature owner, int amount) {
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
    
	private static boolean isGift(AbstractCard c) {
		for (AbstractCard gift : ChristmasMod.GIFTS)
			if (c.cardID.equals(gift.cardID))
				return true;
		return false;
	}
	
	private static void setCost(CardGroup g) {
		for (AbstractCard c : g.group) {
			if (isGift(c) && (c.costForTurn != 0)) {
				c.modifyCostForCombat(-9999);
			}
		}
	}
	
	public void onInitialApplication() {
		setCost(AbstractDungeon.player.hand);
		setCost(AbstractDungeon.player.drawPile);
		setCost(AbstractDungeon.player.discardPile);
		setCost(AbstractDungeon.player.exhaustPile);
	}
	
	public void onDrawOrDiscard() {
		if (check) {
			setCost(AbstractDungeon.player.hand);
		}
	}
    
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (isGift(c)) {
			flash();
			action.exhaustCard = true;
		}
	}
    
}
