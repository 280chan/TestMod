package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DominatorOfWeakness extends AbstractTestRelic {
	public static final String ID = "DominatorOfWeakness";
	
	public static boolean hasThis() {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (r instanceof DominatorOfWeakness)
				return true;
		return false;
	}
	
	public DominatorOfWeakness() {
		super(ID, RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

}