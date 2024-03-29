package testmod.relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import testmod.mymod.TestMod;
import testmod.relicsup.PatchyPatchUp;
import testmod.utils.PatchyTrigger;

public class PatchyPatch extends AbstractTestRelic implements PatchyTrigger {
	public static ArrayList<String> MAP = new ArrayList<String>();
	public static final ArrayList<PatchyPatch> RELICS = new ArrayList<PatchyPatch>();
	private boolean act = false;
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			MAP.clear();
			this.act = true;
		}
		if (this.isActive && !(CURRENT[0] instanceof PatchyPatchUp)) {
			CURRENT[0] = this;
		}
		RELICS.add(this);
	}
	
	public void onUnequip() {
		if (this.isActive)
			PatchyTrigger.load();
		RELICS.remove(this);
	}
	
	public void atPreBattle() {
		if (!this.isActive || this.relicStream(PatchyPatchUp.class).count() > 0)
			return;
		PatchyTrigger.load();
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

	@Override
	public void realAttack(String patch) {
		if (!this.isActive || !this.act || MAP.contains(patch))
			return;
		MAP.add(patch);
		this.counter++;
		if (this.counter > 0 && (this.counter & (this.counter - 1)) == 0)
			atb(randomDamage((int) RELICS.stream().peek(r -> r.show()).count(), DamageType.HP_LOSS));
	}

}