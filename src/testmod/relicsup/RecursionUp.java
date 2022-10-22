package testmod.relicsup;

public class RecursionUp extends AbstractUpgradedRelic {
	
	public void onEquip() {
		this.updateHandSize(10);
		if (this.inCombat()) {
			this.atPreBattle();
		}
	}
	
	public void onUnequip() {
		this.updateHandSize(-10);
		if (this.inCombat()) {
			p().gameHandSize -= this.counter;
		}
	}
	
	public void atPreBattle() {
		p().gameHandSize += this.counter = 10;
	}
	
	public void onPlayerEndTurn() {
		counter--;
		if (counter == 0) {
			counter = 10;
		}
		p().gameHandSize += counter == 10 ? 9 : -1;
	}
	
}