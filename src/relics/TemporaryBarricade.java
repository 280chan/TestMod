package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class TemporaryBarricade extends AbstractClickRelic {
	public static final String ID = "TemporaryBarricade";
	
	public TemporaryBarricade() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atPreBattle() {
		if (this.active()) {
			this.flash();
			this.stopPulse();
			AbstractPlayer p = AbstractDungeon.player;
			this.addToBot(new ApplyPowerAction(p, p, new BarricadePower(p)));
		}
    }
	
	private static int f(int x) {
		if (x > 1)
			return (int) Math.pow(Math.log(x), 2.5);
		return 0;
	}
	
	public void atTurnStart() {
		AbstractPlayer p = AbstractDungeon.player;
		int x = p.currentBlock;
		if (x > 0)
			this.addToBot(new GainEnergyAction(1));
		if (this.active()) {
			int newx = f(x);
			if (newx < x) {
				p.loseBlock(x - newx);
			}
		}
    }

	public void onVictory() {
		if (this.active()) {
			this.beginLongPulse();
		}
    }
	
	private boolean active() {
		return this.counter == -2;
	}
	
	public static void pulseLoader() {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (r instanceof TemporaryBarricade)
				r.onVictory();
	}
	
	@Override
	protected void onRightClick() {
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT) {
			this.counter = -3 - this.counter;
			if (this.active()) {
				this.beginLongPulse();
			} else {
				this.stopPulse();
			}
		}
	}

}