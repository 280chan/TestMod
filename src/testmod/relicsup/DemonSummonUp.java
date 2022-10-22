package testmod.relicsup;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import testmod.mymod.TestMod;
import testmod.relics.DemonSummon;

public class DemonSummonUp extends AbstractUpgradedRelic implements OnReceivePowerRelic {
	private static ArrayList<Class<? extends AbstractPower>> used = new ArrayList<Class<? extends AbstractPower>>();

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature s) {
		if (!(p instanceof InvisiblePower) && used.stream().noneMatch(c -> c.isInstance(p))) {
			used.add(p.getClass());
			this.att(apply(p(), new DexterityPower(p(), 1)));
			this.show();
		}
		return true;
	}
	
	private void clear() {
		if (this.isActive) {
			used.clear();
		}
	}
	
	public void onEquip() {
		if (this.inCombat()) {
			this.counter = GameActionManager.turn;
			TestMod.setActivity(this);
			this.clear();
		}
	}
	
	public void atPreBattle() {
		this.counter = 0;
		this.clear();
	}
	
	public void onVictory() {
		this.counter = -1;
		this.clear();
	}
	
	public void atTurnStartPostDraw() {
		this.counter++;
		this.att(this.apply(this.p(), DemonSummon.demon(p(), this.counter, this.counter)));
		this.show();
	}
	
}