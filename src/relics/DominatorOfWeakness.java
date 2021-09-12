package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class DominatorOfWeakness extends AbstractTestRelic {
	public static final String ID = "DominatorOfWeakness";
	
	public static boolean hasThis() {
		return AbstractDungeon.player.relics.stream().anyMatch(r -> {return r instanceof DominatorOfWeakness;});
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