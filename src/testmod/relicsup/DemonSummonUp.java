package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import com.megacrit.cardcrawl.powers.DexterityPower;
import testmod.relics.DemonSummon;

public class DemonSummonUp extends AbstractUpgradedRelic {
	
	public DemonSummonUp() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void onEquip() {
		if (this.inCombat())
			this.counter = GameActionManager.turn;
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
	public void atTurnStartPostDraw() {
		this.counter++;
		this.att(this.apply(this.p(), DemonSummon.demon(p(), this.counter, this.counter)));
		this.addTmpActionToBot(() -> att(apply(p(), new DexterityPower(p(), cal()))));
		this.show();
    }
	
	private int cal() {
		return (int) p().powers.stream().filter(p -> p.type == PowerType.BUFF && !(p instanceof InvisiblePower)).count();
	}
	
}