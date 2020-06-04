package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class KeyOfTheVoid extends MyRelic {
	public static final String ID = "KeyOfTheVoid";
	
	private int victoryFloor = -1;
	private boolean cardSelected = true;
	private boolean finished = false;
	
	public KeyOfTheVoid() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
		this.counter = -2;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private int getNum() {
		int count = 0;
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (r.tier == RelicTier.BOSS)
				count++;
		return count;
	}
	
	public void onVictory() {
		this.counter = -1;
		this.victoryFloor = AbstractDungeon.floorNum;
    }
	
	public void justEnteredRoom(final AbstractRoom room) {
		if (this.victoryFloor == -1 && !(room instanceof TreasureRoom)) {
			this.onVictory();
		} else if (this.victoryFloor != AbstractDungeon.floorNum) {
			this.counter = -2;
			this.finished = false;
		}
	}
	
	private void initPurgeCard() {
		this.cardSelected = false;
		if (AbstractDungeon.isScreenUp) {
			AbstractDungeon.dynamicBanner.hide();
			AbstractDungeon.overlayMenu.cancelButton.hide();
			AbstractDungeon.previousScreen = AbstractDungeon.screen;
		}
		AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;

		CardGroup forPurge = AbstractDungeon.player.masterDeck.getPurgeableCards();
		if (forPurge.size() > this.getNum())
			AbstractDungeon.gridSelectScreen.open(forPurge, this.getNum(), this.DESCRIPTIONS[1] + this.getNum() + this.DESCRIPTIONS[2], false, false, false, true);
		else {
			if (forPurge.size() > 0) {
				purgeCards(forPurge.group);
			}
			this.counter = -2;
			this.cardSelected = true;
			this.finished = true;
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
		}
	}

	public void update() {
		super.update();
		if ((!this.cardSelected) && (AbstractDungeon.gridSelectScreen.selectedCards.size() == this.getNum())) {
			this.cardSelected = true;

			purgeCards(AbstractDungeon.gridSelectScreen.selectedCards);
			
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.finished = true;
		} else if (this.counter == -1 && AbstractDungeon.screen == CurrentScreen.COMBAT_REWARD && !this.finished) {
			initPurgeCard();
		}
	}

	private void purgeCards(ArrayList<AbstractCard> list) {
		if (list.size() % 2 == 1) {
			int w = list.size() / 2;
			for (int i = -w; i < w + 1; i++) {
				AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(
						list.get(i + w),
						Settings.WIDTH / 2.0F + i * 60.0F * Settings.scale + (i * 2 + Integer.signum(i)) * AbstractCard.IMG_WIDTH / 2.0F,
						Settings.HEIGHT / 2.0F));
			}
		} else {
			int w = list.size() / 2;
			for (int i = 0; i < list.size(); i++) {
				AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(
						list.get(i),
						Settings.WIDTH / 2.0F + ((i - w) * 60.0F + 30f) * Settings.scale + ((i - w) * 2 + 1) * AbstractCard.IMG_WIDTH / 2.0F,
						Settings.HEIGHT / 2.0F));
			}
		}

		for (AbstractCard card : list) {
			AbstractDungeon.player.masterDeck.removeCard(card);
			AbstractDungeon.transformCard(card, true, AbstractDungeon.miscRng);
		}
	}

}