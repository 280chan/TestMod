package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import powers.IntensifyImprintPower;

public class IntensifyImprint extends MyRelic {
	public static final String ID = "IntensifyImprint";
	
	public IntensifyImprint() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
		this.counter = -1;
	}
	
	public String getUpdatedDescription() {
		if (this.counter < 0)
			return DESCRIPTIONS[0];
		return DESCRIPTIONS[0] + " NL " + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private void modifyCounter(int newValue) {
		this.counter = newValue;
		this.updateDescription(null);
	}
	
	private void resetCounter() {
		this.modifyCounter(0);
	}
	
	private void stopCounter() {
		this.modifyCounter(-1);
	}
	
	public void incrementCounter() {
		this.counter++;
		this.updateDescription(null);
	}
	
	public void atPreBattle() {
		this.resetCounter();
    }
	
	public void atTurnStart() {
		this.resetCounter();
    }
	
	public void onVictory() {
		this.stopCounter();
    }
	
	public void update() {
		super.update();
		if (this.counter < 0)
			return;
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (!m.hasPower(IntensifyImprintPower.POWER_ID)) {
					m.powers.add(new IntensifyImprintPower(m, this));
				} else if(!m.powers.get(m.powers.size() - 1).ID.equals(IntensifyImprintPower.POWER_ID)) {
					int index = 0;
					for (int i = 0; i < m.powers.size(); i++) {
						if (m.powers.get(i).ID.equals(IntensifyImprintPower.POWER_ID)) {
							index = i;
							break;
						}
					}
					m.powers.remove(index);
					m.powers.add(new IntensifyImprintPower(m, this));
				}
			}
		}
	}
	
}