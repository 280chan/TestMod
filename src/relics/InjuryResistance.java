package relics;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

import powers.InjuryResistancePower;

public class InjuryResistance extends AbstractTestRelic {
	
	public InjuryResistance() {
		super(RelicTier.RARE, LandingSound.MAGICAL, BAD);
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