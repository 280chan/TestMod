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

public class GlassSoul extends AbstractTestRelic implements GetRelicTrigger, ClickableRelic {
	public static final String ID = "GlassSoul";
	public static final int PRICE_RATE = 2, COUNTER_RATE = 3;
	public static ArrayList<String> relics = new ArrayList<String>(), tmpRelics = new ArrayList<String>();
	private boolean damaged = false;
	
	public GlassSoul() {
		super(RelicTier.SPECIAL, LandingSound.CLINK);
		this.counter = 0;
	}
	
	public static void load(ArrayList<String> list) {
		relics.clear();
		relics.addAll(list);
		MISC.relicStream(GlassSoul.class).filter(r -> r.isActive).forEach(r -> r.tryPulse(false));
	}
	
	public static void save() {
		TestMod.saveString(ID, relics);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0)
			save();
		else
			this.counter = -1;
    }
	
	public void onEnterRoom(AbstractRoom r) {
		if (this.isActive && !tmpRelics.isEmpty() && this.relicStream(GlassSoulUp.class).count() == 0) {
			relics.addAll(tmpRelics);
			save();
			tmpRelics.clear();
		}
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0) {
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
		int a = GlassSoulSelectScreen.amountRate(r);
		return (p().gold >= r.getPrice() * a / PRICE_RATE && p().gold > 9) || this.counter >= COUNTER_RATE * a;
	}
	
	public void onUnequip() {
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0) {
			relics.clear();
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
	
	public void tryPulse(boolean skip) {
		if ((skip || !this.inCombat()) && !relics.isEmpty() && relics.stream().anyMatch(this::canBuy))
			this.beginLongPulse();
	}
	
	public void onVictory() {
		if (this.isActive && this.relicStream(GlassSoulUp.class).count() == 0) {
			if (!this.damaged)
				this.counter += this.relicStream(GlassSoul.class).count();
			tryPulse(true);
		}
    }
}