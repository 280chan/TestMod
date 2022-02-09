package relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;
import screens.GlassSoulSelectScreen;
import utils.GetRelicTrigger;

public class GlassSoul extends AbstractTestRelic implements GetRelicTrigger, ClickableRelic {
	public static final String ID = "GlassSoul";
	public static final int PRICE_RATE = 10;
	public ArrayList<String> relics = new ArrayList<String>();
	public ArrayList<String> tmpRelics = new ArrayList<String>();
	private boolean damaged = false;
	
	public GlassSoul() {
		super(RelicTier.SPECIAL, LandingSound.CLINK);
		this.counter = 0;
	}
	
	public static void load(ArrayList<String> list) {
		INSTANCE.relicStream(GlassSoul.class).filter(r -> r.isActive).peek(r -> r.setList(list))
				.forEach(r -> r.tryPulse(false));
	}
	
	private void save() {
		TestMod.saveString(ID, relics);
	}
	
	private void setList(ArrayList<String> list) {
		this.relics.clear();
		this.relics.addAll(list);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			save();
		} else {
			this.counter = -1;
		}
    }
	
	public void onEnterRoom(AbstractRoom r) {
		if (!tmpRelics.isEmpty()) {
			relics.addAll(tmpRelics);
			save();
			tmpRelics.clear();
		}
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (this.isActive) {
			tmpRelics.add(r.relicId);
			this.print("获得了" + r.name);
			tryPulse(false);
		}
	}

	@Override
	public void onRightClick() {
		if (this.isActive && !relics.isEmpty() && !inCombat() && relics.stream().anyMatch(this::canBuy)) {
			new GlassSoulSelectScreen("", this).open();
		} else {
			GlassSoulSelectScreen.playCantBuySfx();
		}
	}
	
	public Stream<AbstractRelic> streamOf(String id) {
		return p().relics.stream().filter(a -> a.relicId.equals(id));
	}
	
	private boolean canBuy(String id) {
		return (p().gold >= RelicLibrary.getRelic(id).getPrice() * (int) (streamOf(id).count()) / GlassSoul.PRICE_RATE
				&& p().gold > 9) || this.counter >= streamOf(id).count();
	}
	
	public void onUnequip() {
		this.relics.clear();
    }
	
	public void atPreBattle() {
		this.damaged = false;
		this.stopPulse();
    }
	
	public void onLoseHp(int amount) {
		this.damaged |= amount > 0;
	}
	
	public void tryPulse(boolean skip) {
		if ((skip || !this.inCombat()) && !this.relics.isEmpty() && relics.stream().anyMatch(this::canBuy))
			this.beginLongPulse();
	}
	
	public void onVictory() {
		if (this.isActive) {
			if (!this.damaged)
				this.counter += this.relicStream().filter(r -> r instanceof GlassSoul).count();
			tryPulse(true);
		}
    }

}