package testmod.relics;

import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.powers.VoidShardEventDamagePower;
import testmod.powers.VoidShardEventDamagePower.Shard;

public class VoidShard extends AbstractTestRelic implements Shard {
	private static final double DAMAGE_RATE = 2;
	private VoidShardEventDamagePower p;

	public double damageRate() {
		if (!this.inCombat()) {
			this.flash();
			return DAMAGE_RATE;
		}
		return 1;
	}
	
	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}

	public void atPreBattle() {
		VoidShardEventDamagePower.getThis().stream().filter(v -> v.vs instanceof VoidShard).forEach(this::removePower);
		p = null;
	}

	public void onVictory() {
		this.addPower(p = new VoidShardEventDamagePower(this));
	}

	public void onEnterRoom(final AbstractRoom room) {
		if (p != null)
			this.removePower(p);
		this.onVictory();
	}

}