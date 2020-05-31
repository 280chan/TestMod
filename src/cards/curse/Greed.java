
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.GreedPower;
import relics.Sins;
import utils.MiscMethods;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class Greed extends CustomCard implements MiscMethods {
    public static final String ID = "Greed";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = -2;
    private static final int BASE_MGC = 1;

    public Greed() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    	this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
    
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
    	return this.hasPrudence() || p.hasRelic("Blue Candle");
	}

	public void triggerWhenDrawn() {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new GreedPower(p, this.magicNumber), this.magicNumber));
	}
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Greed();
		return Sins.copyCurse();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}