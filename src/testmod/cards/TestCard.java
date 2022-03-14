
package testmod.cards;

import basemod.abstracts.*;
import testmod.mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.cards.green.Acrobatics;
import com.megacrit.cardcrawl.cards.green.Backflip;
import com.megacrit.cardcrawl.cards.green.Defend_Green;
import com.megacrit.cardcrawl.cards.green.GrandFinale;
import com.megacrit.cardcrawl.cards.green.Neutralize;
import com.megacrit.cardcrawl.cards.green.Nightmare;
import com.megacrit.cardcrawl.cards.green.Strike_Green;
import com.megacrit.cardcrawl.cards.green.Survivor;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class TestCard extends CustomCard {
    public static final String ID = "SpecialIDThatDontRepeatWithOthers";
    public static final String NAME = "功能";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "测试，如果你看见这张卡，请反馈bug";
    private static final int COST = 0;
    private static final int BASE_BLK = 0;
    private static final int BASE_DMG = 0;
    private static final int BASE_MGC = 0;

    public TestCard() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.baseBlock = BASE_BLK;
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.purgeOnUse = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	m.currentHealth = 57;
    	p.currentHealth = 4;
    	p.drawPile.clear();
    	for (AbstractCard c : new AbstractCard[] {new GrandFinale(), new Strike_Green(), new Strike_Green(), new Strike_Green(), new Acrobatics(), new Burn(), new Burn()}) {
    		if (c instanceof Burn)
    			c.upgrade();
    		p.drawPile.addToBottom(c);
    	}
    	p.drawPile.group.get(0).upgrade();
    	p.hand.clear();
    	for (AbstractCard c : new AbstractCard[] {new Regret(), new Decay(), new Backflip(), new Nightmare(), new Defend_Green()})
    		p.hand.addToTop(c);
    	p.discardPile.clear();
    	for (AbstractCard c : new AbstractCard[] {new Neutralize(), new Survivor(), new Burn(), new Defend_Green(), new Burn(), new Defend_Green(), new Burn(), new Defend_Green(), new Defend_Green(), new Burn()}) {
    		if (c instanceof Burn)
    			c.upgrade();
    		p.discardPile.addToTop(c);
    	}
    }
    
    public boolean canUpgrade() {
    	return false;
    }
    
    public void upgrade() {
    }
}