package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import powers.VoidShardEventDamagePower;

public class VoidShard extends AbstractTestRelic {
	public static final String ID = "VoidShard";
	private static final int DAMAGE_RATE = 3;
	
	public int damageRate() {
		this.flash();
		return DAMAGE_RATE;
	}
	
	public VoidShard() {
		super(ID, RelicTier.BOSS, LandingSound.SOLID);
		this.testTier = BAD;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
    }

	public void atPreBattle() {
		if (VoidShardEventDamagePower.hasThis()) {
			VoidShardEventDamagePower.getThis().forEach(AbstractDungeon.player.powers::remove);
		}
    }

	public void onVictory() {
		AbstractDungeon.player.powers.add(new VoidShardEventDamagePower(AbstractDungeon.player, this));
    }

	public void onEnterRoom(final AbstractRoom room) {
		this.onVictory();
    }

}