package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.actions.unique.SwordBoomerangAction;

public class Automaton extends AbstractTestCard {
    private static final int ATTACK_DMG = 8;
    private static final int BASE_MGC = 1;
    private static final int DELTA_MGC = 1;

    private static void load(AbstractCard c) {
		if (c.misc > c.baseMagicNumber) {
			c.magicNumber = c.baseMagicNumber = c.misc;
		} else if (c.misc < c.baseMagicNumber) {
			c.misc = c.magicNumber;
		}
    }
    
    public static void loadMagicNumber() {
    	if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null)
    		return;
    	AbstractDungeon.player.masterDeck.group.stream().filter(c -> c instanceof Automaton).forEach(Automaton::load);
    }
    
    public Automaton() {
        super(Automaton.class, 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.misc = this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (this.isInAutoplay) {
			this.addTmpActionToBot(() -> {
				AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.uuid.equals(this.uuid))
						.forEach(this::modify);
				GetAllInBattleInstances.get(this.uuid).forEach(this::modify);
			});
    	}
		if (this.magicNumber > 0)
			this.addToBot(new SwordBoomerangAction(AbstractDungeon.getMonsters().getRandomMonster(true),
					new DamageInfo(p, this.baseDamage), this.magicNumber));
		this.addTmpActionToBot(() -> {
	    	this.magicNumber--;
	    	this.baseMagicNumber--;
	    	this.misc--;
	    	this.initializeDescription();
    	});
    }
    
	public void modify(AbstractCard c) {
		c.misc += DELTA_MGC;
		c.baseMagicNumber += DELTA_MGC;
		c.magicNumber += DELTA_MGC;
    }
	
	private void updateValue() {
		if (this.magicNumber > this.misc) {
			this.misc = this.magicNumber;
		} else if (this.magicNumber < this.misc) {
			this.magicNumber = this.baseMagicNumber = this.misc;
		}
	}
	
	public void applyPowers() {
		this.updateValue();
		super.applyPowers();
		this.initializeDescription();
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard c = super.makeStatEquivalentCopy();
		this.updateValue();
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