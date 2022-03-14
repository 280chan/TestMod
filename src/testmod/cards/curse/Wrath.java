package testmod.cards.curse;

import testmod.cards.AbstractTestCurseCard;
import testmod.relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;

public class Wrath extends AbstractTestCurseCard {
	private static final UIStrings UI = INSTANCE.uiString();
    public static final String ID = "Wrath";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int BASE_MGC = 1;

    public Wrath() {
    	super(ID, NAME, DESCRIPTION);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    	this.isEthereal = true;
    }

	public boolean canPlay(AbstractCard card) {
		if (this.hasPrudence() || card.type == CardType.ATTACK)
			return true;
		card.cantUseMessage = UI.TEXT[0];
		return false;
	}
	
	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Wrath();
		return Sins.copyCurse();
	}

	public void triggerWhenDrawn() {
		AbstractDungeon.getMonsters().monsters.stream()
				.filter(m -> !(m.isDead || m.isDying || m.isEscaping || m.escaped))
				.forEach(m -> this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player,
						new AngryPower(m, this.magicNumber), this.magicNumber)));
	}
}