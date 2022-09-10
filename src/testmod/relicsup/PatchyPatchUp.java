package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.utils.PatchyTrigger;

public class PatchyPatchUp extends AbstractUpgradedRelic implements PatchyTrigger {
	private boolean act = false;
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			this.act = true;
		}
		if (this.isActive) {
			LIST.add(this);
		}
	}
	
	public void onUnequip() {
		if (this.isActive)
			PatchyTrigger.load();
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		PatchyTrigger.load();
		this.counter = 0;
		this.act = true;
	}
	
	public void onVictory() {
		if (!this.isActive)
			return;
		this.counter = -1;
		this.act = false;
	}
	
	public void update() {
		super.update();
		if (this.counter < 0 || this.inCombat())
			return;
		this.counter = -1;
	}
	
	private int countAndShow() {
		return (int) p().relics.stream().filter(r -> r instanceof PatchyTrigger)
				.peek(r -> ((AbstractTestRelic) r).show()).count()
				* (!this.hasEnemies() ? 1 : AbstractDungeon.getMonsters().monsters.size());
	}

	@Override
	public void realAttack(String patch) {
		if (!this.isActive || !this.act)
			return;
		this.counter++;
		if (this.counter > 0 && (this.counter & (this.counter - 1)) == 0) {
			int count = countAndShow();
			int dmg = Math.max((int) (count * 1.0 * AbstractDungeon.getMonsters().monsters.stream()
					.filter(m -> !m.isDeadOrEscaped()).mapToInt(m -> m.maxHealth).max().orElse(100) / 100), count);
			atb(new DamageAllEnemiesAction(p(), DamageInfo.createDamageMatrix(dmg, true), DamageType.HP_LOSS,
					AttackEffect.FIRE));
		}
	}

}