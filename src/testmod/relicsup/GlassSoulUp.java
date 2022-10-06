package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.mymod.TestMod;
import testmod.relics.GlassSoul;
import testmod.screens.GlassSoulSelectScreen;
import testmod.utils.CounterKeeper;
import testmod.utils.GetRelicTrigger;
import testmod.utils.GlassSoulPulser;

public class GlassSoulUp extends AbstractUpgradedRelic
		implements GetRelicTrigger, ClickableRelic, CounterKeeper, GlassSoulPulser {
	public static final int PRICE_RATE = 10, COUNTER_RATE = 1;
	private boolean damaged = false;
	
	public void onEquip() {
		TestMod.setActivity(this);
		this.counter = 0;
		if (!this.isActive)
			this.counter = -1;
		else if (!this.hasStack("relicupgradelib.ui.RelicUpgradePopup", "replaceRelic")) {
			if (this.relicStream(GlassSoul.class).count() == 0) {
				RELICS.clear();
				GlassSoul.save();
				TMP.clear();
			}
		}
    }
	
	public void onEnterRoom(AbstractRoom r) {
		if (this.isActive && !TMP.isEmpty()) {
			RELICS.addAll(TMP);
			GlassSoul.save();
			TMP.clear();
		}
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (this.isActive) {
			TMP.add(r.relicId);
			tryPulse(false);
		}
	}

	@Override
	public void onRightClick() {
		if (this.isActive && !RELICS.isEmpty() && !inCombat() && RELICS.stream().anyMatch(this::canBuy))
			new GlassSoulSelectScreen(RELICS, this).open();
		else
			GlassSoulSelectScreen.playCantBuySfx();
	}
	
	public boolean canBuy(String id) {
		AbstractRelic r = RelicLibrary.getRelic(id);
		int a = GlassSoulSelectScreen.amountRate(r);
		return (p().gold >= r.getPrice() * a / PRICE_RATE && p().gold > 9) || this.counter >= COUNTER_RATE * a;
	}
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		RELICS.clear();
		this.relicStream(GlassSoulUp.class).filter(r -> !r.isActive).limit(1).forEach(r -> r.counter = counter);
    }
	
	public void atPreBattle() {
		this.damaged = false;
		this.stopPulse();
    }
	
	public void onLoseHp(int amount) {
		this.damaged |= amount > 0;
	}
	
	public void onVictory() {
		if (this.isActive) {
			if (!this.damaged)
				this.counter += this.relicStream(GlassSoulUp.class).count();
			tryPulse(true);
		}
    }

}