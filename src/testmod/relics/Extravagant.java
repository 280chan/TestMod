package testmod.relics;

import java.util.function.Consumer;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import basemod.BaseMod;

public class Extravagant extends AbstractTestRelic implements ClickableRelic {
	private static int delta = 0;
	
	public Extravagant() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void updateAllPulse() {
		this.relicStream(Extravagant.class).forEach(r -> r.up(false));
	}
	
	private void up(boolean stop) {
		if (!stop && BaseMod.MAX_HAND_SIZE > 0)
			this.beginLongPulse();
		else
			this.stopPulse();
		this.counter = stop ? -1 : -2;
	}
	
	public void atPreBattle() {
		if (this.isActive)
			delta = 0;
    }
	
	public void atTurnStart() {
		this.up(false);
    }
	
	public void onPlayerEndTurn() {
		this.up(true);
    }
	
	public void onVictory() {
		this.up(true);
		if (delta > 0) {
			BaseMod.MAX_HAND_SIZE += delta;
			delta = 0;
		}
    }
	
	private void play(AbstractCard c, boolean purge) {
		c.exhaust = !purge;
		c.purgeOnUse = purge;
        att(new NewQueueCardAction(c, true, false, true));
	}
	
	private Consumer<AbstractCard> play(int time) {
		return c -> {
			AbstractDungeon.getCurrRoom().souls.remove(c);
			this.addTmpActionToTop(() -> {
				for (int i = 1; i < time; i++)
					play(c.makeSameInstanceOf(), true);
				play(c, false);
			});
		};
	}

	@Override
	public void onRightClick() {
		if (this.inCombat() && this.counter == -2 && BaseMod.MAX_HAND_SIZE > 0 && !p().hand.group.isEmpty()) {
			this.show();
			int tmp = Math.max(1, BaseMod.MAX_HAND_SIZE / 2);
			delta += tmp;
			BaseMod.MAX_HAND_SIZE -= tmp;
			this.updateAllPulse();
			tmp = p().hand.group.size();
			reverse(p().hand.group).forEach(play(tmp));
			p().hand.group.clear();
		}
	}

}