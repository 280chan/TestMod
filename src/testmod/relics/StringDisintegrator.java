package testmod.relics;

public class StringDisintegrator extends AbstractTestRelic {

	public StringDisintegrator() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
}