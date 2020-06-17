package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import powers.InjuryResistancePower;

public class InjuryResistance extends AbstractTestRelic {
	public static final String ID = "InjuryResistance";
	
	public InjuryResistance() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	private void addPower(AbstractPlayer p) {
		if (!InjuryResistancePower.hasThis(p))
			p.powers.add(new InjuryResistancePower(p, this));
	}
	
	public void justEnteredRoom(AbstractRoom r)	{
		this.addPower(AbstractDungeon.player);
	}
	
	public void atPreBattle() {
		this.addPower(AbstractDungeon.player);
    }

}