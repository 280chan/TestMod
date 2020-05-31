
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Sins;
import utils.MiscMethods;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;

public class Wrath extends CustomCard implements MiscMethods {
    public static final String ID = "Wrath";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = -2;
    private static final int BASE_MGC = 1;

    public Wrath() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    	this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
	
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
    	return this.hasPrudence() || p.hasRelic("Blue Candle");
	}

	public boolean canPlay(AbstractCard card) {
		if (this.hasPrudence() || card.type == CardType.ATTACK)
			return true;
		card.cantUseMessage = "暴怒:我无法打出 #r非攻击牌 ";
		return false;
	}
	
	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Wrath();
		return Sins.copyCurse();
	}

	public void triggerWhenDrawn() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.isEscaping && !m.escaped) {
				this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new AngryPower(m, this.magicNumber), this.magicNumber));
			}
		}
	}

    public void upgrade() {
    }
}