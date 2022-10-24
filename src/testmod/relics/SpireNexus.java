package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;

import testmod.events.SpireNexusEvent;
import testmod.relicsup.SpireNexusUp;

public class SpireNexus extends AbstractTestRelic implements ClickableRelic {
	public static boolean skipEffect = false;
	
	public void onEquip() {
		if (!this.inCombat())
			this.changePulse();
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!(room instanceof EventRoom)) {
			this.stopPulse();
		}
	}

	private void changePulse() {
		if (this.grayscale = skipEffect)
			this.stopPulse();
		else
			this.beginLongPulse();
	}
	
	private void updateSkipEffect() {
		skipEffect = !skipEffect;
		this.relicStream(SpireNexus.class).forEach(r -> r.changePulse());
	}
	
	public void atBattleStart() {
		this.stopPulse();
	}
	
	public void onVictory() {
		this.changePulse();
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}
	
	@Override
	public void onRightClick() {
		if (!this.inCombat())
			this.updateSkipEffect();
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "generateEvent")
	public static class GenerateEventPatch {
		@SpirePrefixPatch
		public static SpireReturn<AbstractEvent> Prefix(Random rng) {
			boolean flag = skipEffect;
			if (flag) {
				MISC.relicStream(SpireNexusUp.class).forEach(r -> MISC.addRandomKey());
			}
			int c = (int) (MISC.relicStream(SpireNexusUp.class).count() + MISC.relicStream(SpireNexus.class).count());
			return flag || c == 0 ? SpireReturn.Continue() : SpireReturn.Return(new SpireNexusEvent());
		}
	}
}
