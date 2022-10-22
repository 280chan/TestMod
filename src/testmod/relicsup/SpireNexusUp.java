package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;

import testmod.relics.SpireNexus;

public class SpireNexusUp extends AbstractUpgradedRelic implements ClickableRelic {
	
	public void onEquip() {
		if (!this.inCombat())
			this.changePulse();
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!(room instanceof EventRoom)) {
			this.stopPulse();
		}
	}

	private void changePulse() {
		if (SpireNexus.skipEffect)
			this.stopPulse();
		else
			this.beginLongPulse();
	}
	
	private void updateSkipEffect() {
		SpireNexus.skipEffect = !SpireNexus.skipEffect;
		this.relicStream(SpireNexusUp.class).forEach(r -> r.changePulse());
	}
	
	public void atBattleStart() {
		this.stopPulse();
	}
	
	public void onVictory() {
		this.changePulse();
	}
	
	@Override
	public void onRightClick() {
		if (!this.inCombat())
			this.updateSkipEffect();
	}
}
