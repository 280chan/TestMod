package testmod.cards.colorless;

import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.StrengthPower;

import testmod.cards.AbstractTestCard;

public class LimitFlipper extends AbstractTestCard {
	private boolean active = false;

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.updateActive(secondLastIfLastIsThis());
		atb(active ? new LimitBreakAction() : apply(p, new StrengthPower(p, this.magicNumber)));
	}

	public void triggerOnGlowCheck() {
		this.glowColor = (active ? AbstractCard.GOLD_BORDER_GLOW_COLOR : AbstractCard.BLUE_BORDER_GLOW_COLOR).cpy();
	}
	
	public void triggerOnCardPlayed(AbstractCard c) {
		if (!this.equals(c)) {
			this.updateActive(c);
		}
	}
	
	private AbstractCard secondLastIfLastIsThis() {
		return lastCard() != this ? lastCard()
				: reverse(AbstractDungeon.actionManager.cardsPlayedThisCombat).stream().skip(1).findFirst()
						.orElse(null);
	}
	
	private AbstractCard lastCard() {
		return AbstractDungeon.actionManager == null || AbstractDungeon.actionManager.cardsPlayedThisCombat == null
				? null : AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().reduce(this::last).orElse(null);
	}
	
	private void updateActive(AbstractCard c) {
		if (c == null)
			return;
		this.active = this.upgraded ? c.color == CardColor.COLORLESS : c instanceof LimitFlipper;
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
			this.upDesc();
		}
	}
}