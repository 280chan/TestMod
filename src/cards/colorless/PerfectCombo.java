
package cards.colorless;

import actions.PerfectComboAction;
import cards.AbstractEquivalentableCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import java.util.ArrayList;

public class PerfectCombo extends AbstractEquivalentableCard {
    public static final String ID = "PerfectCombo";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final int COST = 1;
    private static final int ATTACK_DMG = 15;
    private static final int DELTA_BASE_MAGIC = 1;
    private static final int BASE_CHANCE = 20;

    public static final ArrayList<PerfectCombo> TO_UPDATE = new ArrayList<PerfectCombo>();

    public PerfectCombo() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.magicNumber = this.baseMagicNumber = this.misc = BASE_CHANCE;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new PerfectComboAction(m, new DamageInfo(p, this.baseDamage, this.damageTypeForTurn), AttackEffect.SLASH_HORIZONTAL, this.magicNumber));//造成伤害
    }
    
    public int countUpgrades() {
        AbstractPlayer p = AbstractDungeon.player;
        ArrayList<AbstractCard> group = new ArrayList<AbstractCard>();
        group.addAll(p.drawPile.group);
        group.addAll(p.discardPile.group);
        group.addAll(p.hand.group);
        if (!group.contains(this)) {
        	group.add(this);
        }
        return group.stream().mapToInt(c -> c.timesUpgraded).sum();
    }

    public void calculateCardDamage(AbstractMonster m) {
    	super.calculateCardDamage(m);
		this.upgradeMagicNumber(this.countUpgrades() - this.magicNumber + this.misc);
		this.rawDescription = EXTENDED_DESCRIPTION[0];
		initializeDescription();
    }
    
    public void applyPowers() {
    	super.applyPowers();
		this.upgradeMagicNumber(this.countUpgrades() - this.magicNumber + this.misc);
		this.rawDescription = EXTENDED_DESCRIPTION[0];
		initializeDescription();
    }
    
    public AbstractCard makeCopy() {
    	PerfectCombo c = new PerfectCombo();
    	TO_UPDATE.add(c);
        return c;
    }

    public boolean canUpgrade() {
		return true;
    }
    
    public void upgrade() {
    	this.timesUpgraded++;
    	this.misc += DELTA_BASE_MAGIC;
    	this.upgradeMagicNumber(DELTA_BASE_MAGIC);
        this.upgraded = true;
        this.name = (NAME + "+" + this.timesUpgraded);
        initializeDescription();
        initializeTitle();
    }
}