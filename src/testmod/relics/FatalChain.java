package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.powers.FatalChainCheckDamagePower;

public class FatalChain extends AbstractTestRelic {
	private final HashMap<DamageInfo, AbstractCreature> MAP = new HashMap<DamageInfo, AbstractCreature>();
	public final ArrayList<FatalChainCheckDamagePower> TO_REMOVE = new ArrayList<FatalChainCheckDamagePower>();
	
	public FatalChain() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}

	public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
		if (damageAmount > 0 && !target.isPlayer) {
			this.MAP.put(info, target);
			FatalChainCheckDamagePower p = new FatalChainCheckDamagePower(target, this.MAP, this);
			target.powers.add(p);
		}
	}
	
	public void onVictory() {
		this.MAP.clear();
		this.TO_REMOVE.clear();
	}
	
	public void update() {
		super.update();
		if (this.TO_REMOVE.isEmpty())
			return;
		this.TO_REMOVE.stream().forEach(p -> p.owner.powers.remove(p));
		this.TO_REMOVE.clear();
	}

}