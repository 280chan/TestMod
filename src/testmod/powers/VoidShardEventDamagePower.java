package testmod.powers;

import java.util.List;
import java.util.stream.Collectors;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import testmod.relics.VoidShard;

public class VoidShardEventDamagePower extends AbstractTestPower implements InvisiblePower {
	private VoidShard vs = null;
	
	public static boolean hasThis() {
		return AbstractDungeon.player.powers.stream().anyMatch(p -> p instanceof VoidShardEventDamagePower);
	}
	
	public static List<AbstractPower> getThis() {
		return AbstractDungeon.player.powers.stream().filter(p -> p instanceof VoidShardEventDamagePower)
				.collect(Collectors.toList());
	}
	
	public VoidShardEventDamagePower(AbstractCreature owner, VoidShard relic) {
		this.owner = owner;
		this.amount = -1;
		this.vs = relic;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public int onLoseHp(int damage) {
		return AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT
				? (int) (damage * this.vs.damageRate()) : damage;
	}

}
