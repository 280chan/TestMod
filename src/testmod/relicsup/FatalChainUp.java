package testmod.relicsup;

import java.util.ArrayList;
import java.util.HashMap;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.powers.FatalChainCheckDamagePower;
import testmod.relics.FatalChain;

public class FatalChainUp extends AbstractUpgradedRelic {
	private static final HashMap<DamageInfo, AbstractCreature> MAP = FatalChain.MAP;
	private static final ArrayList<FatalChainCheckDamagePower> TO_REMOVE = FatalChain.TO_REMOVE;
	
	public FatalChainUp() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}

	public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
		if (this.isActive && damageAmount > 0 && !target.isPlayer) {
			MAP.put(info, target);
			target.powers.add(new FatalChainCheckDamagePower(target, MAP, TO_REMOVE, true));
		}
	}
	
	public void onVictory() {
		if (this.isActive) {
			MAP.clear();
			TO_REMOVE.clear();
		}
	}
	
	public void update() {
		super.update();
		if (this.isActive && !TO_REMOVE.isEmpty()) {
			TO_REMOVE.stream().forEach(p -> p.owner.powers.remove(p));
			TO_REMOVE.clear();
		}
	}
	
	public void wasHPLost(int damage) {
		if (this.inCombat())
			FatalChainCheckDamagePower.applyDamage(damage, true);
	}

}