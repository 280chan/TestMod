package testmod.relicsup;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;

public class ShadowAmuletUp extends AbstractUpgradedRelic {
	private final ArrayList<Integer> BLOCK_TO_GAIN = new ArrayList<Integer>();
	
	public static void onLoseBlock(int amount) {
		MISC.relicStream(ShadowAmuletUp.class).forEach(r -> r.loseblock(amount));
	}
	
	public String getUpdatedDescription() {
		String temp = DESCRIPTIONS[0];
		if (this.BLOCK_TO_GAIN != null && this.BLOCK_TO_GAIN.size() > 0) {
			temp += DESCRIPTIONS[1] + BLOCK_TO_GAIN.stream().map(i -> i + ", ").reduce("", (a, b) -> a + b);
			temp = temp.substring(0, temp.length() - 2);
		}
		return temp;
	}
	
	public void onPlayerEndTurn() {
		if (this.BLOCK_TO_GAIN.size() > 0) {
			this.atb(new GainBlockAction(p(), p(), this.BLOCK_TO_GAIN.remove(0)));
			this.flash();
			this.counter = this.BLOCK_TO_GAIN.isEmpty() ? 0 : this.BLOCK_TO_GAIN.get(0);
		}
		this.updateDescription();
	}
	
	private ArrayList<Integer> list(int n) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i = 1; n > 0; i++) {
			n -= i;
			l.add(i);
		}
		return l;
	}
	
	public void loseblock(int amount) {
		ArrayList<Integer> delta = list(amount);
		for (int i = 0; i < delta.size(); i++) {
			if (this.BLOCK_TO_GAIN.size() > i)
				this.BLOCK_TO_GAIN.set(i, this.BLOCK_TO_GAIN.get(i) + delta.get(i));
			else
				this.BLOCK_TO_GAIN.add(delta.get(i));
		}
		if (amount > 0) {
			this.flash();
			this.counter = this.BLOCK_TO_GAIN.get(0);
			this.updateDescription();
			delta.clear();
		}
	}
	
	public void onVictory() {
		if (!this.BLOCK_TO_GAIN.isEmpty()) {
			p().gainGold(Math.min(this.BLOCK_TO_GAIN.get(0), 100));
			this.show();
			this.BLOCK_TO_GAIN.clear();
			this.updateDescription();
		}
		this.counter = -1;
	}
	
	public void atPreBattle() {
		this.BLOCK_TO_GAIN.clear();
		this.counter = 0;
		this.updateDescription();
	}

}