package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import testmod.mymod.TestMod;
import testmod.relics.Temperance;
import testmod.utils.CounterKeeper;

public class TemperanceUp extends AbstractUpgradedRelic implements CounterKeeper, ClickableRelic {
	private static final UIStrings UI = Temperance.UI;
	
	public boolean cardSelected = true;
	public static RoomPhase phase;
	private static CurrentScreen pre;
	
	public TemperanceUp() {
		this.counter = 0;
	}
	
	public void onObtainCard(AbstractCard card) {
		if (!this.inCombat() && this.counter == 0)
			this.beginLongPulse();
		p().increaseMaxHp(++counter, true);
		this.show();
	}
	
	public void update() {
		super.update();
		if (cardSelected)
			return;
		if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
			cardSelected = true;
			AbstractDungeon.topLevelEffects
					.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards.get(0),
							Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			p().masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards.remove(0));
			AbstractDungeon.getCurrRoom().phase = phase;
			if (--this.counter == 0)
				this.stopPulse();
			this.show();
		} else if (AbstractDungeon.screen == pre) {
			TestMod.info("刚刚取消");
			cardSelected = true;
			AbstractDungeon.getCurrRoom().phase = phase;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
		}
	}
	
	public void atPreBattle() {
		this.stopPulse();
	}
	
	public void onVictory() {
		if (this.counter > 0)
			this.beginLongPulse();
	}

	@Override
	public void onRightClick() {
		if (!this.inCombat() && this.counter > 0 && cardSelected && !p().masterDeck.getPurgeableCards().isEmpty()) {
			cardSelected = false;
			pre = AbstractDungeon.screen;
			if (AbstractDungeon.isScreenUp) {
				AbstractDungeon.dynamicBanner.hide();
				AbstractDungeon.previousScreen = AbstractDungeon.screen;
			}
			phase = AbstractDungeon.getCurrRoom().phase;
			AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;
			AbstractDungeon.gridSelectScreen.open(p().masterDeck.getPurgeableCards(),
					1, UI.TEXT[0] + 1 + UI.TEXT[1], false, false, true, true);
		}
	}
	
}