package relics;

import java.util.ArrayList;
import java.util.HashMap;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.PowerTip;

import powers.FatalChainCheckDamagePower;

public class FatalChain extends AbstractTestRelic {
	public static final String ID = "FatalChain";
	
	private final HashMap<DamageInfo, AbstractCreature> MAP = new HashMap<DamageInfo, AbstractCreature>();
	public final ArrayList<FatalChainCheckDamagePower> TO_REMOVE = new ArrayList<FatalChainCheckDamagePower>();
	
	public FatalChain() {
		super(ID, RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
		if (this.isActive && damageAmount > 0 && !target.isPlayer) {
			this.MAP.put(info, target);
			FatalChainCheckDamagePower p = new FatalChainCheckDamagePower(target, this.MAP, this);
			target.powers.add(p);
		}
	}
	
	public void onVictory() {
		if (this.isActive) {
			this.MAP.clear();
			this.TO_REMOVE.clear();
		}
	}
	
	public void update() {
		super.update();
		if (this.TO_REMOVE.isEmpty())
			return;
		this.TO_REMOVE.stream().forEach(p -> {p.owner.powers.remove(p);});
		this.TO_REMOVE.clear();
	}

}