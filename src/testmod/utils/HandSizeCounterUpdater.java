package testmod.utils;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import basemod.BaseMod;

public interface HandSizeCounterUpdater {
	public default void updateHandSize() {
		AbstractDungeon.player.relics.stream().filter(r -> r instanceof HandSizeCounterUpdater)
				.forEach(r -> r.counter = BaseMod.MAX_HAND_SIZE);
	}
}
