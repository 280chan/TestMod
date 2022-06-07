package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.powers.FatalChainCheckDamagePower;
import testmod.relicsup.FatalChainUp;

public class FatalChain extends AbstractTestRelic {
	public static final HashMap<DamageInfo, AbstractCreature> MAP = new HashMap<DamageInfo, AbstractCreature>();
	public static final ArrayList<FatalChainCheckDamagePower> TO_REMOVE = new ArrayList<FatalChainCheckDamagePower>();
	
	public FatalChain() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}
	
	private boolean check() {
		return this.isActive && this.relicStream(FatalChainUp.class).count() == 0;
	}

	public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
		if (check() && damageAmount > 0 && !target.isPlayer) {
			MAP.put(info, target);
			target.powers.add(new FatalChainCheckDamagePower(target, MAP, TO_REMOVE, false));
		}
	}
	
	public void onVictory() {
		if (check()) {
			MAP.clear();
			TO_REMOVE.clear();
		}
	}
	
	public void update() {
		super.update();
		if (check() && !TO_REMOVE.isEmpty()) {
			TO_REMOVE.stream().forEach(p -> p.owner.powers.remove(p));
			TO_REMOVE.clear();
		}
	}

}