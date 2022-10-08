package testmod.relics;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.mymod.TestMod;
import testmod.relicsup.GlassSoulUp;
import testmod.screens.GlassSoulSelectScreen;
import testmod.utils.GetRelicTrigger;
import testmod.utils.GlassSoulPulser;

public class GlassSoul extends AbstractTestRelic implements GetRelicTrigger, ClickableRelic, GlassSoulPulser {
	public static final String ID = "GlassSoul";
	public static final int PRICE_RATE = 2, COUNTER_RATE = 3;
	private boolean damaged = false;
	
	public static void load(ArrayList<String> list) {
		RELICS.clear();
		TMP.clear();
		RELICS.addAll(list);
		MISC.relicStream().filter(r -> r instanceof GlassSoulPulser).forEach(r -> ((GlassSoulPulser)r).tryPulse(false));
	}
	
	public static void save() {
		TestMod.saveString(ID, RELICS);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		this.counter = 0;
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0) {
			RELICS.clear();
			save();
			TMP.clear();
		} else
			this.counter = -1;
    }
	
	public void onEnterRoom(AbstractRoom r) {
		if (this.isActive && !TMP.isEmpty() && this.relicStream(GlassSoulUp.class).count() == 0) {
			RELICS.addAll(TMP);
			save();
			TMP.clear();
		}
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0) {
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
		if (this.relicStream(GlassSoulUp.class).count() == 0
				&& !this.hasStack("relicupgradelib.ui.RelicUpgradePopup", "replaceRelic")) {
			if (this.relicStream(GlassSoul.class).count() == 1)
				RELICS.clear();
			else if (this.isActive)
				this.relicStream(GlassSoul.class).filter(r -> !r.isActive).limit(1).forEach(r -> r.counter = counter);
		}
    }
	
	public void atPreBattle() {
		this.damaged = false;
		this.stopPulse();
    }
	
	public void onLoseHp(int amount) {
		this.damaged |= amount > 0;
	}
	
	public void onVictory() {
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0) {
			if (!this.damaged)
				this.counter += this.relicStream(GlassSoul.class).count();
			tryPulse(true);
		}
    }
}