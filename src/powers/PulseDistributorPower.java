package powers;

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
	public static final String POWER_ID = "PulseDistributorPower";
	public static final String NAME = "脉冲分配";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"受到" + toBlue("n") + "点非失去生命的伤 NL 害，不直接损失生命值，而是在此回合开始的", "个回 NL 合内每次敌人回合结束时失去" + toBlue(1) + "点生命。"};
	private static final String DAMAGE_DESCRIPTION = " NL 每回合失去生命值为: NL ";
	private static final String SPLITTER = "，";
	public int magic;
	
	public final ArrayList<Integer> DAMAGES = new ArrayList<Integer>();
	
	public PulseDistributorPower(AbstractPlayer owner, int magic) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.magic = magic;
		this.img = ImageMaster.loadImage(IMG);
		this.updateDescription();
		this.type = PowerType.BUFF;
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
	}
	
    public int onAttacked(final DamageInfo info, int damage) {
		if (info.type == DamageType.HP_LOSS)
			return damage;
		boolean onCurrent = false;
		if (info.owner != null) {
			for (AbstractPower p : owner.powers) {
				if (!p.ID.equals(this.ID)) {
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
    }
    
    private void updateList(int damage) {
    	if (damage < 1)
    		return;
		TestMod.info(this.name + ":伤害前:" + DAMAGES);
    	for (int i = 0; i < damage + this.magic; i++) {
    		if (DAMAGES.size() == i) {
    			DAMAGES.add(1);
    		} else {
    			DAMAGES.set(i, DAMAGES.get(i) + 1);
    		}
    	}
    	TestMod.info(this.name + ":伤害后:" + DAMAGES);
    }
    
    public void atEndOfRound() {
    	if (!this.DAMAGES.isEmpty()) {
    		TestMod.info(this.name + ":伤害前:" + DAMAGES);
    		this.pretendAttack(AbstractDungeon.player, DAMAGES.remove(0));
    		TestMod.info(this.name + ":伤害后:" + DAMAGES);
    		this.updateDescription();
		}
    }

	private void pretendAttack(AbstractPlayer p, int damage) {
		AbstractDungeon.player.damage(new DamageInfo(p, damage, DamageType.HP_LOSS));
	}
    
}
