package testmod.relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.mymod.TestMod;

public class PatchyPatch extends AbstractTestRelic {
	private static ArrayList<String> MAP = new ArrayList<String>();
	private boolean act = false;
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			MAP.clear();
			this.act = true;
		}
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = 0;
		this.act = true;
		MAP.clear();
	}
	
	public void onVictory() {
		if (!this.isActive)
			return;
		MAP.clear();
		this.counter = -1;
		this.act = false;
	}
	
	public void update() {
		super.update();
		if (this.counter < 0 || this.inCombat())
			return;
		this.counter = -1;
	}
	
	public void patchAttack(String patch) {
		if (this.stackTrace().filter(e -> PatchyPatch.class.getCanonicalName().equals(e.getClassName())
				&& "patchAttack".equals(e.getMethodName())).count() > 1)
			return;
		if (!this.isActive || !this.act || AbstractDungeon.player == null || !inCombat() || MAP.contains(patch)
				|| relicStream(PatchyPatch.class).count() == 0)
			return;
		MAP.add(patch);
		this.counter++;
		if (this.counter > 0 && (this.counter & (this.counter - 1)) == 0)
			atb(randomDamage((int) relicStream(PatchyPatch.class).peek(r -> r.show()).count(), DamageType.THORNS));
	}

}