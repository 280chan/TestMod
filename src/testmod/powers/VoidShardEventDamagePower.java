package testmod.powers;

import java.util.List;
import java.util.stream.Collectors;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.relics.AbstractTestRelic;
import testmod.relics.VoidShard;
import testmod.relicsup.VoidShardUp;

public class VoidShardEventDamagePower extends AbstractTestPower implements InvisiblePower {
	public Shard vs = null;
	
	public static interface Shard {
		double damageRate();
	}
	
	public static boolean hasThis() {
		return AbstractDungeon.player.powers.stream().anyMatch(p -> p instanceof VoidShardEventDamagePower);
	}
	
	public static List<VoidShardEventDamagePower> getThis() {
		return AbstractDungeon.player.powers.stream().filter(p -> p instanceof VoidShardEventDamagePower)
				.map(p -> (VoidShardEventDamagePower) p).collect(Collectors.toList());
	}
	
	private static boolean hasUp() {
		return MISC.relicStream(VoidShardUp.class).count() > 0;
	}
	
	private boolean notActive() {
		return this.vs instanceof VoidShard && (!((AbstractTestRelic) this.vs).isActive || hasUp());
	}
	
	public VoidShardEventDamagePower(Shard relic) {
		this.owner = p();
		this.amount = -1;
		this.vs = relic;
		updateDescription();
		this.type = PowerType.BUFF;
		this.addMap(p -> new VoidShardEventDamagePower(this.vs));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public int onLoseHp(int damage) {
		return notActive() ? damage : (int) (damage * this.vs.damageRate());
	}

}
