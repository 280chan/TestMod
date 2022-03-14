package testmod.relics;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

import testmod.powers.InjuryResistancePower;

public class InjuryResistance extends AbstractTestRelic {
	
	public InjuryResistance() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL, BAD);
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	private void addPower() {
		if (!InjuryResistancePower.hasThis(p()))
			this.addPower(new InjuryResistancePower(p(), this));
	}
	
	public void justEnteredRoom(AbstractRoom r)	{
		this.addPower();
	}
	
	public void atPreBattle() {
		this.addPower();
    }

}