package relics;

public class Iteration extends AbstractTestRelic {
	public static final String ID = "Recursion"; // 不要删除或更改这个id
	public static int baseHandSize;
	
	public Iteration() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	private int relicAmount() {
		return (int) this.relicStream(Iteration.class).count();
	}
	
	public void atPreBattle() {
		this.counter = 0;
		if (!isActive)
			return;
		baseHandSize = p().gameHandSize;
		p().gameHandSize += 5 * relicAmount();
    }
	
	public void onPlayerEndTurn() {
		counter++;
		if (counter == 10) {
			counter = 0;
		}
		if (!isActive)
			return;
		p().gameHandSize = baseHandSize + (5 - counter) * relicAmount();
    }
	
}