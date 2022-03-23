package testmod.cards.colorless;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import testmod.cards.AbstractEquivalentableCard;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import java.util.ArrayList;
import java.util.stream.Stream;

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
    private static PerfectCombo INSTANCE = new PerfectCombo();
	private static Random rng;
	private static int deadLoopCounter = 0;
	private DamageInfo info;
	
	public static void setRng() {
		rng = INSTANCE.copyRNG(AbstractDungeon.miscRng);
	}
	
	private boolean roll() {
		return rng.random(99) < this.magicNumber;
	}

    public PerfectCombo() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.magicNumber = this.baseMagicNumber = this.misc = BASE_CHANCE;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.info = new DamageInfo(p, this.baseDamage, this.damageTypeForTurn);
        this.lambda(true, m);
    }
    
    private void lambda(boolean addToBot, AbstractMonster m) {
    	if (addToBot) {
    		this.addTmpActionToBot(() -> {lambda(m);});
    	} else {
    		this.addTmpActionToTop(() -> {lambda(m);});
    	}
    }
    
    private void lambda(AbstractMonster m) {
		if (!(m == null || this.info.owner.isDying || m.isDeadOrEscaped())) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.SLASH_HORIZONTAL));
			this.info.applyPowers(this.info.owner, m);
			m.damage(this.info);
			if (checkAndAddNext(m))
				return;
		} else if (checkAndAddNext(m))
			return;
		deadLoopCounter = 0;
	}

	private boolean checkAndAddNext(AbstractMonster m) {
		if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
			AbstractDungeon.actionManager.clearPostCombatActions();
		} else if (!(m.hasPower("Invincible") && m.getPower("Invincible").amount == 0) && roll()
				&& deadLoopCounter++ < 100) {
			ArrayList<AbstractMonster> avalible = AbstractDungeon.getMonsters().monsters.stream()
					.filter(c -> !(c == null || c.isDead || c.halfDead || c.isDying || c.isEscaping))
					.collect(this.toArrayList());
			if (avalible.isEmpty()) {
				deadLoopCounter = 0;
				return false;
			}
			this.lambda(false, avalible.get((int) (Math.random() * avalible.size())));
			return true;
		}
		return false;
	}
	
	
	public int countUpgrades() {
		AbstractPlayer p = AbstractDungeon.player;
		return Stream
				.concat(Stream.of(p.drawPile, p.discardPile, p.hand).flatMap(g -> g.group.stream()), Stream.of(this))
				.distinct().mapToInt(c -> c.timesUpgraded).sum();
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