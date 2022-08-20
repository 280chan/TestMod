package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.powers.VoidShardEventDamagePower;
import testmod.powers.VoidShardEventDamagePower.Shard;

public class VoidShardUp extends AbstractUpgradedRelic implements Shard, ClickableRelic {
	private static final double DAMAGE_RATE = 0.94;

	public double damageRate() {
		if (this.counter > 0)
			this.show();
		return rate();
	}
	
	private double rate() {
		return Math.pow(DAMAGE_RATE, this.counter);
	}
	
	public String getUpdatedDescription() {
		if (this.isObtained) {
			return DESCRIPTIONS[0] + DESCRIPTIONS[1] + String.format("%.2f", 100 * rate()) + DESCRIPTIONS[2];
		}
		return DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		this.addEnergy();
		this.counter = 0;
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	private void updatePower() {
		VoidShardEventDamagePower.getThis().stream().filter(v -> this.equals(v.vs)).forEach(this::removePower);
		this.addPower(new VoidShardEventDamagePower(this));
		this.updateDescription();
	}

	public void atPreBattle() {
		this.updatePower();
    }

	public void onVictory() {
		this.updatePower();
    }

	public void onEnterRoom(final AbstractRoom room) {
		this.updatePower();
    }

	@Override
	public void onRightClick() {
		if (p().gold > 9 + this.counter * 10) {
			p().loseGold(10 + this.counter * 10);
			this.counter++;
			this.updateDescription();
		}
	}

}