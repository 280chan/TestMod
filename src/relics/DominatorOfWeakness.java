package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DominatorOfWeakness extends AbstractTestRelic {
	
	public static boolean hasThis() {
		return AbstractDungeon.player.relics.stream().anyMatch(r -> r instanceof DominatorOfWeakness);
	}
	
	public DominatorOfWeakness() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}

}