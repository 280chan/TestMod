package testmod.relicsup;

import java.util.function.Consumer;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import basemod.BaseMod;
import testmod.utils.HandSizeCounterUpdater;

public class ExtravagantUp extends AbstractUpgradedRelic implements ClickableRelic, HandSizeCounterUpdater {
	private static int delta = 0;
	private boolean playerTurn = false;
	
	public ExtravagantUp() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	private void updateAllPulse() {
		this.relicStream(ExtravagantUp.class).forEach(r -> r.up(false));
	}
	
	private void up(boolean stop) {
		if ((this.playerTurn = !stop) && BaseMod.MAX_HAND_SIZE > 0)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onEquip() {
		if (this.inCombat())
			this.atPreBattle();
	}
	
	public void onUnequip() {
		if (this.isActive)
			this.onVictory();
	}
	
	public void atPreBattle() {
		if (this.isActive)
			delta = 0;
		this.counter = BaseMod.MAX_HAND_SIZE;
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
			updateHandSize();
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
			for (int i = 1; i < time; i++)
				play(c.makeSameInstanceOf(), true);
			play(c, false);
		};
	}

	@Override
	public void onRightClick() {
		if (this.inCombat() && this.playerTurn && BaseMod.MAX_HAND_SIZE > 0 && !p().hand.group.isEmpty()) {
			this.show();
			delta++;
			BaseMod.MAX_HAND_SIZE--;
			updateHandSize();
			this.updateAllPulse();
			int tmp = p().hand.group.size();
			reverse(p().hand.group).forEach(play(tmp));
			p().hand.group.clear();
		}
	}

}