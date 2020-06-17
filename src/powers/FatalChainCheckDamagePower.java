package powers;

import java.util.HashMap;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relics.FatalChain;

public class FatalChainCheckDamagePower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "FatalChainCheckDamagePower";
	private static int idFix = 0;
	private HashMap<DamageInfo, AbstractCreature> map;
	public static final double E = Math.E;
	public boolean finished = false;
	
	public FatalChainCheckDamagePower(AbstractCreature owner, HashMap<DamageInfo, AbstractCreature> map) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.ID += idFix++;
		this.owner = owner;
		updateDescription();
		this.type = PowerType.BUFF;
		this.map = map;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
    
    public int onAttacked(final DamageInfo info, final int damageAmount) {
    	if (map.containsKey(info)) {
			int dmg;
			if ((dmg = damageAmount - map.get(info).currentHealth) > 0) {
				int[] dmgArr = DamageInfo.createDamageMatrix((int) (dmg * E), true);
				this.addToBot(new DamageAllEnemiesAction(AbstractDungeon.player, dmgArr, DamageType.THORNS, AttackEffect.POISON));
			}
			map.remove(info);
			FatalChain.TO_REMOVE.add(this);
		}
		return damageAmount;
    }
    
}
