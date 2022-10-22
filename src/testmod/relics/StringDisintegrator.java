package testmod.relics;

public class StringDisintegrator extends AbstractTestRelic {
	
	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
}