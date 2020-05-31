package cards.colorless;

import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;

import basemod.abstracts.CustomCard;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.SwordBoomerangAction;

public class Automaton extends CustomCard {
    public static final String ID = "Automaton";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = 2;
    private static final int ATTACK_DMG = 8;
    private static final int BASE_MGC = 1;
    private static final int DELTA_MGC = 1;

    public static void loadMagicNumber() {
    	if (AbstractDungeon.player == null)
    		return;
    	if (AbstractDungeon.player.masterDeck == null)
    		return;
    	for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
    		if (c instanceof Automaton) {
    			if (c.misc > c.baseMagicNumber) {
        			c.baseMagicNumber = c.misc;
        			c.magicNumber = c.misc;
    			} else if (c.misc < c.baseMagicNumber) {
    				c.misc = c.magicNumber;
    			}
    		}
    }
    
    public Automaton() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.misc = this.baseMagicNumber;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	if (this.isInAutoplay) {
			this.addToBot(new AbstractGameAction() {
				@Override
				public void update() {
					this.isDone = true;
					for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
						if (c.uuid.equals(Automaton.this.uuid))
							Automaton.modify(c);
					for (AbstractCard c : GetAllInBattleInstances.get(Automaton.this.uuid))
						Automaton.modify(c);
				}
			});
    	}
    	if (this.magicNumber > 0)
    		this.addToBot(new SwordBoomerangAction(
    		      AbstractDungeon.getMonsters().getRandomMonster(true), new DamageInfo(p, this.baseDamage), this.magicNumber));
    	this.addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
		    	Automaton.this.magicNumber--;
		    	Automaton.this.baseMagicNumber--;
		    	Automaton.this.misc--;
		    	Automaton.this.initializeDescription();
			}
		});
    }
    
	public static void modify(AbstractCard c) {
		c.misc += DELTA_MGC;
		c.baseMagicNumber += DELTA_MGC;
		c.magicNumber += DELTA_MGC;
    }
	
	public void applyPowers() {
		if (this.magicNumber > this.misc) {
			this.misc = this.magicNumber;
		} else if (this.magicNumber < this.misc) {
			this.baseMagicNumber = this.misc;
			this.magicNumber = this.misc;
		}
		super.applyPowers();
		this.initializeDescription();
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard c = super.makeStatEquivalentCopy();
		if (this.magicNumber > this.misc) {
			this.misc = this.magicNumber;
		} else if (this.magicNumber < this.misc) {
			this.baseMagicNumber = this.misc;
			this.magicNumber = this.misc;
		}
		c.magicNumber = this.magicNumber;
		return c;
	}
	
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(4);
        }
    }
}