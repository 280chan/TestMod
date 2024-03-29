package testmod.relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;

public class ShadowAmulet extends AbstractTestRelic {
	private final ArrayList<Integer> BLOCK_TO_GAIN = new ArrayList<Integer>();
	
	public static void onLoseBlock(int amount) {
		MISC.relicStream(ShadowAmulet.class).forEach(r -> r.loseblock(amount));
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
			this.addToBot(new GainBlockAction(p(), p(), this.BLOCK_TO_GAIN.remove(0)));
			this.flash();
			this.counter = this.BLOCK_TO_GAIN.isEmpty() ? 0 : this.BLOCK_TO_GAIN.get(0);
		}
		this.updateDescription();
	}
	
	public void loseblock(int amount) {
		for (int i = 0; i < amount; i++) {
			if (this.BLOCK_TO_GAIN.size() > i)
				this.BLOCK_TO_GAIN.set(i, this.BLOCK_TO_GAIN.get(i) + 1);
			else
				this.BLOCK_TO_GAIN.add(1);
		}
		if (amount > 0) {
			this.flash();
			this.counter = this.BLOCK_TO_GAIN.get(0);
			this.updateDescription();
		}
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
	public void atPreBattle() {
		this.BLOCK_TO_GAIN.clear();
		this.counter = 0;
		this.updateDescription();
	}

}