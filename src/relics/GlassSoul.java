package relics;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import mymod.TestMod;
import utils.GetRelicTrigger;
import utils.GlassSoulSelectScreen;

public class GlassSoul extends AbstractTestRelic implements GetRelicTrigger, ClickableRelic {
	public static final String ID = "GlassSoul";
	public static final int PRICE_RATE = 10;
	public ArrayList<String> relics = new ArrayList<String>();
	private boolean damaged = false;
	
	public GlassSoul() {
		super(RelicTier.SPECIAL, LandingSound.CLINK);
		this.counter = 0;
	}
	
	public static void load(ArrayList<String> list) {
		INSTANCE.relicStream().filter(r -> r instanceof GlassSoul).filter(r -> r.isActive).map(r -> (GlassSoul) r)
				.peek(r -> r.setList(list)).forEach(r -> r.tryPulse(false));
	}
	
	private void setList(ArrayList<String> list) {
		this.relics.clear();
		this.relics.addAll(list);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			TestMod.saveString(ID, relics);
		} else {
			this.counter = -1;
		}
    }
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (this.isActive) {
			relics.add(r.relicId);
			TestMod.saveString(ID, relics);
			this.print("获得了" + r.name);
			tryPulse(false);
		}
	}

	@Override
	public void onRightClick() {
		if (this.isActive && !this.relics.isEmpty() && !this.inCombat() && (this.counter > 0 || p().gold > 9)) {
			new GlassSoulSelectScreen("", this).open();
		}
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
		if ((skip || !this.inCombat()) && !this.relics.isEmpty() && (this.counter > 0 || p().gold > 9))
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