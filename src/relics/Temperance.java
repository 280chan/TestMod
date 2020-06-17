package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import mymod.TestMod;

public class Temperance extends AbstractTestRelic {
	public static final String ID = "Temperance";
	
	public static int sizeToRemove;
	public static boolean cardSelected = true;
	public static RoomPhase phase;
	private static CurrentScreen pre;
	
	public Temperance() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onObtainCard(AbstractCard card) {
		if (!isActive)
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
				AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(),
						1, "选择移除1张牌" + sizeToRemove + "次", false, false, true, true);
			} else if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
				cardSelected = true;
				AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(
						(AbstractCard) AbstractDungeon.gridSelectScreen.selectedCards.get(0),
						(float) Settings.WIDTH / 2.0F - (float) AbstractCard.IMG_WIDTH / 2.0F,
						Settings.HEIGHT / 2.0F));
				for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
					AbstractDungeon.player.masterDeck.removeCard(card);
					AbstractDungeon.transformCard(card, true, AbstractDungeon.miscRng);
				}
				AbstractDungeon.getCurrRoom().phase = phase;
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				sizeToRemove--;
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
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}