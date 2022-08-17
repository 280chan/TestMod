package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import testmod.mymod.TestMod;
import testmod.relicsup.TemperanceUp;

public class Temperance extends AbstractTestRelic {
	public static final UIStrings UI = MISC.uiString();
	
	private static int sizeToRemove;
	private static boolean cardSelected = true;
	private static RoomPhase phase;
	private static CurrentScreen pre;
	
	public void onObtainCard(AbstractCard card) {
		if (!isActive || this.relicStream(TemperanceUp.class).peek(r -> r.counter++).count() > 0)
			return;
		counter++;
		if (counter == 3) {
			counter = 0;
			sizeToRemove++;
			pre = AbstractDungeon.screen;
		}
	}
	
	public void update() {
		super.update();
		if (!isActive)
			return;
		if (sizeToRemove > 0) {
			if (cardSelected) {
				cardSelected = false;
				if (AbstractDungeon.isScreenUp) {
					AbstractDungeon.dynamicBanner.hide();
					AbstractDungeon.previousScreen = AbstractDungeon.screen;
				}
				phase = AbstractDungeon.getCurrRoom().phase;
				AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;
				AbstractDungeon.gridSelectScreen.open(p().masterDeck.getPurgeableCards(),
						1, UI.TEXT[0] + sizeToRemove + UI.TEXT[1], false, false, true, true);
			} else if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
				cardSelected = true;
				AbstractDungeon.topLevelEffects
						.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards.get(0),
								Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				p().masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards.remove(0));
				AbstractDungeon.getCurrRoom().phase = phase;
				sizeToRemove--;
				this.show();
			} else if (AbstractDungeon.screen == pre) {
				TestMod.info("刚刚取消");
				cardSelected = true;
				AbstractDungeon.getCurrRoom().phase = phase;
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				sizeToRemove--;
			}
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		counter = 0;
    }
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}