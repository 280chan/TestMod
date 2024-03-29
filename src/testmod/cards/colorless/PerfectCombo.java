package testmod.cards.colorless;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import testmod.cards.AbstractTestCard;

public class PerfectCombo extends AbstractTestCard {
	private static final int DELTA_BASE_MAGIC = 1;
	private static final int BASE_CHANCE = 20;
	public static final ArrayList<PerfectCombo> TO_UPDATE = new ArrayList<PerfectCombo>();
	private static Random rng;
	private static int deadLoopCounter = 0;
	private DamageInfo info;
	
	public static void setRng() {
		rng = AbstractDungeon.miscRng.copy();
	}
	
	private boolean roll() {
		return rng.random(99) < this.magicNumber;
	}

	public PerfectCombo() {
		this.misc = BASE_CHANCE;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.info = new DamageInfo(p, this.baseDamage, this.damageTypeForTurn);
		this.lambda(true, m);
	}
	
	private void lambda(boolean addToBot, AbstractMonster m) {
		if (addToBot) {
			this.addTmpActionToBot(() -> lambda(m));
		} else {
			this.addTmpActionToTop(() -> lambda(m));
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
		return Stream.concat(this.combatCards(), Stream.of(this)).distinct().mapToInt(c -> c.timesUpgraded).sum();
	}

	public void calculateCardDamage(AbstractMonster m) {
		super.calculateCardDamage(m);
		this.upgradeMagicNumber(this.countUpgrades() - this.magicNumber + this.misc);
		this.rawDescription = exDesc()[0];
		initializeDescription();
	}
	
	public void applyPowers() {
		super.applyPowers();
		this.upgradeMagicNumber(this.countUpgrades() - this.magicNumber + this.misc);
		this.rawDescription = exDesc()[0];
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
		this.name = name() + "+" + this.timesUpgraded;
		initializeDescription();
		initializeTitle();
	}
}