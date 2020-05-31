
package cards.colorless;

import cards.AbstractUpdatableCard;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import actions.BloodthirstyAction;

public class Bloodthirsty extends AbstractUpdatableCard {
    public static final String ID = "Bloodthirsty";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = 1;
    private static final int BASE_MGC = 5;
    private static final int D_MGC = -2;
    private static final int UPGRADED_D_MGC = -1;
    private boolean used = false;

    public Bloodthirsty() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.preApplyPowers(p, m);
    	super.applyPowers();
    	AbstractDungeon.actionManager.addToBottom(new BloodthirstyAction(m, p, this.magicNumber));
    	this.used = true;
    }

    @Override
	public void preApplyPowers(AbstractPlayer p, AbstractMonster m) {
		if (m == null) {
			this.misc = 0;
			this.onMonster = false;
			return;
		}
		this.onMonster = true;
		
		Long max = (long) m.maxHealth;
		Long rate = (long) this.magicNumber;
		Long amount = max * rate / 100L;
		if (amount > 2147483647L)
			amount = 100000000L;
    	this.misc = amount.intValue();
    	if (this.misc > m.currentHealth)
    		this.misc = m.currentHealth;
	}
	
	public void applyPowers() {
		super.applyPowers();
    	this.changeDescription(this.getDesc(), false);
    }
	
	private String getDesc() {
		String tmp = EXTENDED_DESCRIPTION[0];
    	if (this.misc > 0 && this.onMonster && this.isHovered()) {
    		tmp += "(" + this.misc + ")";
    	}
		tmp += EXTENDED_DESCRIPTION[1];
    	if (!this.upgraded) {
    		tmp += EXTENDED_DESCRIPTION[2];
    	} else {
        	tmp += EXTENDED_DESCRIPTION[3];
    	}
    	return tmp + EXTENDED_DESCRIPTION[4];
	}
	
	public void doublesMagicNumber() {
		this.upgradeMagicNumber(this.magicNumber);
	}
	
    public void triggerOnCardPlayed(AbstractCard c) {
    	if (!c.cardID.equals(ID) && this.used) {
    		if (!this.upgraded && this.magicNumber + D_MGC > 0) {
        		this.upgradeMagicNumber(D_MGC);
    		} else if (this.magicNumber + UPGRADED_D_MGC > 0) {
        		this.upgradeMagicNumber(UPGRADED_D_MGC);
    		}
    	}
    }
    
    public AbstractCard makeCopy() {
    	Bloodthirsty tmp = new Bloodthirsty();
    	TO_UPDATE.add(tmp);
        return tmp;
    }

    public AbstractCard makeStatEquivalentCopy() {
    	Bloodthirsty tmp = (Bloodthirsty)super.makeStatEquivalentCopy();
    	tmp.upgradeMagicNumber(this.magicNumber - tmp.magicNumber);
		return tmp;
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(2);//升级增加的特殊常量MagicNumber
            this.changeDescription(UPGRADED_DESCRIPTION, true);
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变

}