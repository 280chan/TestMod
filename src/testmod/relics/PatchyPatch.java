package testmod.relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.mymod.TestMod;

public class PatchyPatch extends AbstractTestRelic {
	private static ArrayList<String> MAP = new ArrayList<String>();
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			MAP.clear();
		}
	}
	
	public void atTurnStart() {
		MAP.clear();
	}
	
	public static void patchAttack(String patch) {
		if (AbstractDungeon.player == null || !MISC.inCombat())
			return;
		if (MAP.contains(patch)) {
			MISC.print("已有: " + patch);
			return;
		}
		MISC.print("遗物数量:" + MISC.relicStream(PatchyPatch.class).count());
		if (MISC.relicStream(PatchyPatch.class).count() > 0) {
			MISC.print("初次执行patch: " + patch);
			MAP.add(patch);
			ArrayList<AbstractCreature> l = new ArrayList<AbstractCreature>();
			if (AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null) {
				AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDeadOrEscaped()).forEach(l::add);
			}
			if (!l.isEmpty())
				MISC.atb(new DamageAction(TestMod.randomItem(l, AbstractDungeon.miscRng.copy()),
						new DamageInfo(null, (int) MISC.relicStream(PatchyPatch.class).peek(r -> r.show()).count())));
		}
	}

}