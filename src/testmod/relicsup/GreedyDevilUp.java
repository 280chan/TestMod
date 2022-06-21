package testmod.relicsup;

import java.util.ArrayList;
import java.util.HashMap;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.powers.AbstractTestPower;

public class GreedyDevilUp extends AbstractUpgradedRelic {
	private final HashMap<DamageInfo, AbstractCreature> INFO_MAP = new HashMap<DamageInfo, AbstractCreature>();
	private final ArrayList<AbstractPower> TO_REMOVE = new ArrayList<AbstractPower>();
	
	public GreedyDevilUp() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
		if (damageAmount > 0 && !target.isPlayer) {
			this.INFO_MAP.put(info, target);
			target.powers.add(new GreedyDevilPowerUp(target));
		}
    }
	
	public void onVictory() {
		this.INFO_MAP.clear();
		this.TO_REMOVE.clear();
	}
	
	public void update() {
		super.update();
		if (this.TO_REMOVE.isEmpty())
			return;
		this.TO_REMOVE.stream().forEach(p -> p.owner.powers.remove(p));
		this.TO_REMOVE.clear();
	}
	
	public class GreedyDevilPowerUp extends AbstractTestPower implements InvisiblePower {
		private GreedyDevilPowerUp(AbstractCreature target) {
			super("GreedyDevilUp");
			this.owner = target;
		}
		
		public int onAttacked(final DamageInfo i, final int damage) {
	    	if (INFO_MAP.containsKey(i)) {
				int dmg = damage - this.owner.currentHealth;
				if (dmg >= 0) {
					if (dmg > 0)
						p().increaseMaxHp(Math.min(dmg, Math.max(1, p().maxHealth / 10)), true);
					if (damage > 0)
						p().gainGold(damage);
					GreedyDevilUp.this.flash();
				}
				INFO_MAP.remove(i, this.owner);
				TO_REMOVE.add(this);
			}
			return damage;
	    }
	}

}