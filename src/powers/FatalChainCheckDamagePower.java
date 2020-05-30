package powers;

import java.util.HashMap;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import relics.FatalChain;

public class FatalChainCheckDamagePower extends AbstractPower implements InvisiblePower {
	public static final String POWER_ID = "FatalChainCheckDamagePower";
	public static final String NAME = "致命连锁结算";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "如果你看到这句话，请反馈bug。";
	private static int idFix = 0;

	private HashMap<DamageInfo, AbstractCreature> map;
	public static final double E = Math.E;
	public boolean finished = false;
	
	public FatalChainCheckDamagePower(AbstractCreature owner, HashMap<DamageInfo, AbstractCreature> map) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID + idFix++;
		this.owner = owner;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
		this.map = map;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
    
    public int onAttacked(final DamageInfo info, final int damageAmount) {//参数：info-伤害信息，damageAmount-伤害数值
    	if (map.containsKey(info)) {
			int dmg;
			if ((dmg = damageAmount - map.get(info).currentHealth) > 0) {
				int[] dmgArr = DamageInfo.createDamageMatrix((int) (dmg * E), true);
				AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, dmgArr, DamageType.THORNS, AttackEffect.POISON));
			}
			map.remove(info);
			FatalChain.TO_REMOVE.add(this);
		}
		return damageAmount;
    }
    
}
