package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.powers.AbstractTestPower;

public class GreedyDevil extends AbstractTestRelic {
	private final HashMap<DamageInfo, AbstractCreature> INFO_MAP = new HashMap<DamageInfo, AbstractCreature>();
	private final ArrayList<AbstractPower> TO_REMOVE = new ArrayList<AbstractPower>();
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
		if (damageAmount > 0 && !target.isPlayer) {
			this.INFO_MAP.put(info, target);
			target.powers.add(new GreedyDevilPower(target));
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
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}
	
	public class GreedyDevilPower extends AbstractTestPower implements InvisiblePower {
		private GreedyDevilPower(AbstractCreature target) {
			super("GreedyDevil");
			this.owner = target;
		}
		
		public int onAttacked(final DamageInfo i, final int damage) {
			if (INFO_MAP.containsKey(i)) {
				int dmg = damage - this.owner.currentHealth;
				if (dmg > 0) {
					p().gainGold(Math.min(dmg, 100));
					GreedyDevil.this.flash();
				}
				INFO_MAP.remove(i, this.owner);
				TO_REMOVE.add(this);
			}
			return damage;
		}
	}

}