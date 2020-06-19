package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class PulseDistributorPower extends AbstractTestPower {
	public static final String POWER_ID = "PulseDistributorPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final String DAMAGE_DESCRIPTION = DESCRIPTIONS[2];
	private static final String SPLITTER = DESCRIPTIONS[3];
	public int magic;
	
	public final ArrayList<Integer> DAMAGES = new ArrayList<Integer>();
	
	public static boolean hasThis(AbstractPlayer owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof PulseDistributorPower)
				return true;
		return false;
	}
	
	public static PulseDistributorPower getThis(AbstractPlayer owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof PulseDistributorPower)
				return (PulseDistributorPower) p;
		return null;
	}
	
	public PulseDistributorPower(AbstractPlayer owner, int magic) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = -1;
		this.magic = magic;
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
		p.damage(new DamageInfo(p, damage, DamageType.HP_LOSS));
	}
    
}
