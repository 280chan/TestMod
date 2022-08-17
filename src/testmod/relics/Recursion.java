package testmod.relics;

public class Recursion extends AbstractTestRelic {
	
	private int relicAmount() {
		return (int) this.relicStream(Recursion.class).count();
	}
	
	public void atPreBattle() {
		this.counter = 0;
		if (!isActive)
			return;
		p().gameHandSize += 5 * relicAmount();
    }
	
	public void onPlayerEndTurn() {
		counter++;
		if (counter == 10) {
			counter = 0;
		}
		if (!isActive)
			return;
		p().gameHandSize += (counter == 0 ? 9 : -1) * relicAmount();
    }
	
}