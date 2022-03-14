package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class TemporaryBarricade extends AbstractTestRelic implements ClickableRelic {
	
	public TemporaryBarricade() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public void atPreBattle() {
		if (this.active()) {
			this.flash();
			this.stopPulse();
			this.addToBot(apply(p(), new BarricadePower(p())));
		}
    }
	
	private static int f(int x) {
		return (x > 1) ? (int) Math.pow(Math.log(x), 2.5) : 0;
	}
	
	public void atTurnStart() {
		this.addTmpActionToBot(() -> {
			int x = p().currentBlock;
			if (x > 0)
				this.addToTop(new GainEnergyAction(1));
			if (this.active()) {
				int newx = f(x);
				if (newx < x) {
					p().loseBlock(x - newx);
				}
			}
		});
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
		INSTANCE.relicStream(TemporaryBarricade.class).forEach(AbstractRelic::onVictory);
	}
	
	@Override
	public void onRightClick() {
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT) {
			this.counter = -3 - this.counter;
			this.togglePulse(this, this.active());
		}
	}

}