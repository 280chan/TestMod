package christmasMod.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import christmasMod.mymod.ChristmasMod;
import testmod.powers.AbstractTestPower;

public class GiftDamagedPower extends AbstractTestPower {
	public static final String POWER_ID = "GiftDamagedPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public static boolean hasThis() {
		return MISC.p().powers.stream().anyMatch(p -> p instanceof GiftDamagedPower);
	}
	
	public static AbstractPower getThis() {
		return MISC.p().powers.stream().filter(p -> p instanceof GiftDamagedPower).findFirst().orElse(null);
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
    
	private boolean isGift(AbstractCard c) {
		return ChristmasMod.GIFTS.stream().map(a -> a.cardID).anyMatch(c.cardID::equals);
	}
	
	private boolean check(AbstractCard c) {
		return isGift(c) && !c.freeToPlayOnce && c.cost > -1;
	}
	
	private void modify(AbstractCard c) {
		c.freeToPlayOnce = true;
	}
	
	public void onInitialApplication() {
		combatCards(false, true).filter(this::check).forEach(this::modify);
	}
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.STATUS) {
			p().hand.moveToExhaustPile(c);
			for (int i = 0; i < this.amount; i++)
				this.addToBot(new MakeTempCardInHandAction(ChristmasMod.randomGift(false)));
		} else if (check(c))
			modify(c);
	}
    
	public void onUseCard(AbstractCard c, UseCardAction a) {
		if (isGift(c)) {
			flash();
			a.exhaustCard = true;
		}
	}
	
	public void onAfterUseCard(AbstractCard c, UseCardAction a) {
		if (isGift(c) && !a.exhaustCard) {
			this.addTmpActionToTop(() -> modify(c));
		}
	}
	
	public void onRemove() {
		combatCards(false, true).filter(this::isGift).forEach(c -> c.freeToPlayOnce = false);
	}
    
}
