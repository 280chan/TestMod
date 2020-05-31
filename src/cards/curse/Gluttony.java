
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Sins;
import utils.MiscMethods;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

public class Gluttony extends CustomCard implements MiscMethods {
    public static final String ID = "Gluttony";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = -2;//卡牌费用
    private static final int BASE_MGC = 20;

    public Gluttony() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    	this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    }
    
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		return this.hasPrudence() || p.hasRelic("Blue Candle");
	}
    
    public void onRemoveFromMasterDeck() {
		AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth * this.magicNumber / 100);
    }
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Gluttony();
		return Sins.copyCurse();
    }

    public void upgrade() {
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}