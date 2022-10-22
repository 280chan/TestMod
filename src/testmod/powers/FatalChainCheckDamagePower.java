package testmod.powers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.relics.FatalChain;
import testmod.relicsup.FatalChainUp;

public class FatalChainCheckDamagePower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "FatalChainCheckDamagePower";
	private static final Function<Boolean, Long> AMOUNT = f -> MISC.relicStream(FatalChain.class).count()
			+ (f ? MISC.relicStream(FatalChainUp.class).count() : 0);
	private static int idFix = 0;
	private HashMap<DamageInfo, AbstractCreature> map;
	public boolean finished = false;
	private ArrayList<FatalChainCheckDamagePower> toRemove;
	private boolean upgrade;
	
	public FatalChainCheckDamagePower(AbstractCreature owner, HashMap<DamageInfo, AbstractCreature> map,
			ArrayList<FatalChainCheckDamagePower> toRemove, boolean upgrade) {
		this.ID += idFix++;
		this.owner = owner;
		updateDescription();
		this.type = PowerType.BUFF;
		this.map = map;
		this.toRemove = toRemove;
		this.upgrade = upgrade;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	private static int damage(int dmg, boolean flag) {
		return (int) (dmg * (flag ? Math.PI : Math.E) * AMOUNT.apply(flag));
	}

	public static void applyDamage(int original, boolean upgrade) {
		MISC.att(new DamageAllEnemiesAction(MISC.p(), DamageInfo.createDamageMatrix(damage(original, upgrade), true),
				DamageType.THORNS, AttackEffect.POISON));
	}
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (this.map.containsKey(info)) {
			int dmg = damageAmount - this.map.get(info).currentHealth;
			if (dmg > 0)
				applyDamage(dmg, this.upgrade);
			this.map.remove(info);
			this.toRemove.add(this);
		}
		return damageAmount;
	}

}
