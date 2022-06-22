package testmod.relicsup;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.mymod.TestMod;
import testmod.relics.GlassSoul;
import testmod.screens.GlassSoulSelectScreen;
import testmod.utils.CounterKeeper;
import testmod.utils.GetRelicTrigger;

public class GlassSoulUp extends AbstractUpgradedRelic implements GetRelicTrigger, ClickableRelic, CounterKeeper {
	public static final int PRICE_RATE = 10, COUNTER_RATE = 1;
	public static ArrayList<String> relics = GlassSoul.relics, tmpRelics = GlassSoul.tmpRelics;
	private boolean damaged = false;
	
	public GlassSoulUp() {
		super(RelicTier.SPECIAL, LandingSound.CLINK);
		this.counter = 0;
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			this.counter = -1;
    }
	
	public void onEnterRoom(AbstractRoom r) {
		if (this.isActive && !tmpRelics.isEmpty()) {
			relics.addAll(tmpRelics);
			GlassSoul.save();
			tmpRelics.clear();
		}
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (this.isActive) {
			tmpRelics.add(r.relicId);
			tryPulse(false);
		}
	}

	@Override
	public void onRightClick() {
		if (this.isActive && !relics.isEmpty() && !inCombat() && relics.stream().anyMatch(this::canBuy))
			new GlassSoulSelectScreen(relics, this).open();
		else
			GlassSoulSelectScreen.playCantBuySfx();
	}
	
	private boolean canBuy(String id) {
		AbstractRelic r = RelicLibrary.getRelic(id);
		int a = GlassSoulSelectScreen.amount(r);
		return (p().gold >= r.getPrice() * a / PRICE_RATE && p().gold > 9) || this.counter >= COUNTER_RATE * a;
	}
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		relics.clear();
		this.relicStream(GlassSoulUp.class).filter(r -> !r.isActive).limit(1).forEach(r -> r.counter = counter);
    }
	
	public void atPreBattle() {
		this.damaged = false;
		this.stopPulse();
    }
	
	public void onLoseHp(int amount) {
		this.damaged |= amount > 0;
	}
	
	public void tryPulse(boolean skip) {
		if ((skip || !this.inCombat()) && !relics.isEmpty() && relics.stream().anyMatch(this::canBuy))
			this.beginLongPulse();
	}
	
	public void onVictory() {
		if (this.isActive) {
			if (!this.damaged)
				this.counter += this.relicStream(GlassSoulUp.class).count();
			tryPulse(true);
		}
    }

}