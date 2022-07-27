package testmod.cards.colorless;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import basemod.abstracts.CustomSavable;
import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.actions.unique.SwordBoomerangAction;

public class Automaton extends AbstractTestCard implements CustomSavable<Integer> {
    private static final int ATTACK_DMG = 8;
    private static final int BASE_MGC = 1;
    private static final int DELTA_MGC = 1;

	@Override
	public void onLoad(Integer savedMagic) {
		if (savedMagic != null)
			this.magicNumber = this.baseMagicNumber = savedMagic;
	}

	@Override
	public Integer onSave() {
		return this.magicNumber;
	}
    
    public Automaton() {
        super(1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (this.isInAutoplay) {
			this.addTmpActionToBot(() -> {
				p().masterDeck.group.stream().filter(c -> c.uuid.equals(this.uuid)).forEach(this::modify);
				GetAllInBattleInstances.get(this.uuid).forEach(this::modify);
			});
    	}
		if (this.magicNumber > 0)
			this.addToBot(new SwordBoomerangAction(AbstractDungeon.getMonsters().getRandomMonster(true),
					new DamageInfo(p, this.baseDamage), this.magicNumber));
		this.addTmpActionToBot(() -> {
	    	this.magicNumber--;
	    	this.baseMagicNumber--;
	    	this.initializeDescription();
    	});
    }
    
	public void modify(AbstractCard c) {
		c.baseMagicNumber += DELTA_MGC;
		c.magicNumber += DELTA_MGC;
    }
	
	public void applyPowers() {
		super.applyPowers();
		this.initializeDescription();
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard c = super.makeStatEquivalentCopy();
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