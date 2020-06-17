package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Iteration extends AbstractTestRelic{
	
	public static final String ID = "Recursion";
	public static int baseHandSize;
	
	public Iteration() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		baseHandSize = p.gameHandSize;
		this.counter = 0;
		p.gameHandSize += 5;
    }
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		counter++;
		if (counter == 10) {
			counter = 0;
		}
		p.gameHandSize = baseHandSize + 5 - counter;
    }
	
}