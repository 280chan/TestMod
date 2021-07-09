package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class ShadowAmulet extends AbstractTestRelic {
	public static final String ID = "ShadowAmulet";
	private final ArrayList<Integer> BLOCK_TO_GAIN = new ArrayList<Integer>();
	
	public static ArrayList<ShadowAmulet> getThis() {
		ArrayList<ShadowAmulet> list = new ArrayList<ShadowAmulet>();
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (r instanceof ShadowAmulet)
				list.add((ShadowAmulet) r);
		return list;
	}
	
	public ShadowAmulet() {
		super(ID, RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		String temp = DESCRIPTIONS[0];
		if (this.BLOCK_TO_GAIN != null && this.BLOCK_TO_GAIN.size() > 0) {
			temp += DESCRIPTIONS[1];
			for (int i : BLOCK_TO_GAIN)
				temp += i + ", ";
			temp.substring(0, temp.length() - 1);
		}
		return temp;
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onPlayerEndTurn() {
		AbstractPlayer p = AbstractDungeon.player;
		if (this.BLOCK_TO_GAIN.size() > 0) {
			this.addToBot(new GainBlockAction(p, p, this.BLOCK_TO_GAIN.remove(0)));
			this.flash();
			if (this.BLOCK_TO_GAIN.size() > 0)
				this.counter = this.BLOCK_TO_GAIN.get(0);
			else
				this.counter = 0;
		}
		this.updateDescription(p.chosenClass);
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
			this.updateDescription(AbstractDungeon.player.chosenClass);
		}
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
	public void atPreBattle() {
		this.BLOCK_TO_GAIN.clear();
		this.counter = 0;
		this.updateDescription(AbstractDungeon.player.chosenClass);
	}

}