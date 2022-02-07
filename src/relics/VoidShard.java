package relics;

import com.megacrit.cardcrawl.rooms.AbstractRoom;
import powers.VoidShardEventDamagePower;

public class VoidShard extends AbstractTestRelic {
	private static final double DAMAGE_RATE = 2.5;
	private VoidShardEventDamagePower p;
	
	public double damageRate() {
		this.flash();
		return DAMAGE_RATE;
	}
	
	public VoidShard() {
		super(RelicTier.BOSS, LandingSound.SOLID, BAD);
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }

	public void atPreBattle() {
		if (VoidShardEventDamagePower.hasThis()) {
			VoidShardEventDamagePower.getThis().forEach(this::removePower);
		}
		p = null;
    }

	public void onVictory() {
		this.addPower(p = new VoidShardEventDamagePower(p(), this));
    }

	public void onEnterRoom(final AbstractRoom room) {
		if (p != null)
			this.removePower(p);
		this.onVictory();
    }

}