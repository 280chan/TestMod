package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class PulseDistributorPower extends AbstractPower{
	
	public static final String POWER_ID = "PulseDistributorPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "脉冲分配";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"受到" + toBlue("n") + "点非失去生命的伤 NL 害，不直接损失生命值，而是在此回合开始的", "个回 NL 合内每次敌人回合结束时失去" + toBlue(1) + "点生命。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	private static final String DAMAGE_DESCRIPTION = " NL 每回合失去生命值为: NL ";
	private static final String SPLITTER = "，";
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	public int magic;
	
	public final ArrayList<Integer> DAMAGES = new ArrayList<Integer>();
	
	public PulseDistributorPower(AbstractPlayer owner, int magic) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.magic = magic;
		this.img = ImageMaster.loadImage(IMG);
		//以上五句不可缺少，照抄即可。记得修改this.img的图片路径。
		this.updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public PulseDistributorPower(AbstractPlayer owner, int magic, ArrayList<Integer> oldDamages) {
		this(owner, magic);
		this.DAMAGES.clear();
		this.DAMAGES.addAll(oldDamages);
		this.updateDescription();
	}
	
	private static String rawDescription(int magic) {
		String temp = "n";
		if (magic != 0) {
			if (magic > 0) {
				temp += "+";
			}
			temp += magic;
		}
		return DESCRIPTIONS[0] + toBlue(temp) + DESCRIPTIONS[1];
	}
	
	private static String toBlue(int num) {
		return toBlue(num + "");
	}
	
	private static String toBlue(String num) {
		return " #b" + num + " ";
	}
	
	private String damages() {
		String retVal = "";
		for (int num : DAMAGES) {
			retVal += toBlue(num) + SPLITTER;
		}
		return retVal.substring(0, retVal.length() - 1);
	}
	
	public void updateDescription() {
		this.description = rawDescription(this.magic);
		if (!this.DAMAGES.isEmpty()) {
			this.description += DAMAGE_DESCRIPTION;
			this.description += damages();
		}
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount = -1;
	}//可通过添加if判定this.amount来限制层数上限。
	
    public int onAttacked(final DamageInfo info, int damage) {//参数：info-伤害信息，damageAmount-伤害数值
		if (info.type == DamageType.HP_LOSS)
			return damage;
		boolean onCurrent = false;
		if (info.owner != null) {
			for (AbstractPower p : owner.powers) {
				if (!p.ID.equals(ID)) {
					if (onCurrent)
						damage = p.onAttacked(info, damage);
				} else {
					onCurrent = true;
				}
			}
		}
        GameActionManager.damageReceivedThisTurn += damage;
        GameActionManager.damageReceivedThisCombat += damage;
        if (damage > 0)
        	AbstractDungeon.player.damagedThisCombat += 1;
    	this.updateList(damage);
    	return 0;
    }//触发时机：当玩家被攻击时，返回伤害数值，可用来修改伤害数值。info.可调用伤害信息。
    
    private void updateList(int damage) {
    	if (damage < 1)
    		return;
		System.out.println("伤害前:" + DAMAGES);
    	for (int i = 0; i < damage + this.magic; i++) {
    		if (DAMAGES.size() == i) {
    			DAMAGES.add(1);
    		} else {
    			DAMAGES.set(i, DAMAGES.get(i) + 1);
    		}
    	}
		System.out.println("伤害后:" + DAMAGES);
    }
    
    public void atEndOfRound() {
    	if (!this.DAMAGES.isEmpty()) {
    		System.out.println("伤害前:" + DAMAGES);
    		this.pretendAttack(AbstractDungeon.player, DAMAGES.remove(0));
    		System.out.println("伤害后:" + DAMAGES);
    		this.updateDescription();
		}
    }//触发时机：当怪物回合结束时触发。

	private void pretendAttack(AbstractPlayer p, int damage) {
		AbstractDungeon.player.damage(new DamageInfo(p, damage, DamageType.HP_LOSS));
	}
    
}
