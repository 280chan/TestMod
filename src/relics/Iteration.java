package relics;

public class Iteration extends AbstractTestRelic {
	public static final String ID = "Recursion"; // 不要删除或更改这个id
	public static int baseHandSize;
	
	public Iteration() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		baseHandSize = p().gameHandSize;
		this.counter = 0;
		p().gameHandSize += 5;
    }
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		counter++;
		if (counter == 10) {
			counter = 0;
		}
		p().gameHandSize = baseHandSize + 5 - counter;
    }
	
}