package testmod.powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.mymod.TestMod;

public class PulseDistributorPower extends AbstractTestPower {
	public int magic;
	
	public final ArrayList<Integer> DAMAGES = new ArrayList<Integer>();
	
	public static boolean hasThis(AbstractPlayer owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof PulseDistributorPower);
	}
	
	public static PulseDistributorPower getThis(AbstractPlayer owner) {
		return (PulseDistributorPower) owner.powers.stream().filter(p -> p instanceof PulseDistributorPower).findAny()
				.orElse(null);
	}
	
	public PulseDistributorPower(AbstractPlayer owner, int magic) {
		if ((this.magic = magic) != 0) {
			this.name += magic > 0 ? " + " + magic : magic;
		}
		this.owner = owner;
		this.amount = -1;
		this.updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public PulseDistributorPower(AbstractPlayer owner, int magic, ArrayList<Integer> oldDamages) {
		this(owner, magic);
		this.DAMAGES.clear();
		this.DAMAGES.addAll(oldDamages);
		this.updateDescription();
	}
	
	private String rawDescription(int magic) {
		String temp = "n";
		if (magic != 0) {
			temp += magic > 0 ? " + " + magic : magic;
		}
		return desc(0) + toBlue(temp) + desc(1);
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
			retVal += toBlue(num) + desc(3);
		}
		return retVal.substring(0, retVal.length() - 1);
	}
	
	public void updateDescription() {
		this.description = rawDescription(this.magic);
		if (!this.DAMAGES.isEmpty()) {
			this.description += desc(2);
			this.description += damages();
		}
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount = -1;
	}
	
	private boolean onCurrent = false;
	
	private boolean filter(AbstractPower p) {
		if (this.onCurrent)
			return true;
		if (this.equals(p))
			this.onCurrent = true;
		return false;
	}
	
	private static class Attacker {
		DamageInfo info;
		int damage;
		Attacker(DamageInfo info, int damage) {
			this.info = info;
			this.damage = damage;
		}
		void act(AbstractPower p) {
			this.damage = p.onAttacked(this.info, this.damage);
		}
	}
	
	public int onAttacked(final DamageInfo info, int damage) {
		if (info.type == DamageType.HP_LOSS)
			return damage;
		if (info.owner != null) {
			Attacker attacker = new Attacker(info, damage);
			this.owner.powers.stream().filter(this::filter).forEachOrdered(attacker::act);
			if (this.onCurrent)
				this.onCurrent = false;
			damage = attacker.damage;
		}
		GameActionManager.damageReceivedThisTurn += damage;
		GameActionManager.damageReceivedThisCombat += damage;
		if (damage > 0)
			p().damagedThisCombat += 1;
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
			this.pretendAttack(DAMAGES.remove(0));
			TestMod.info(this.name + ":伤害后:" + DAMAGES);
			this.updateDescription();
		}
	}

	private void pretendAttack(int damage) {
		p().damage(new DamageInfo(p(), damage, DamageType.HP_LOSS));
	}

}
