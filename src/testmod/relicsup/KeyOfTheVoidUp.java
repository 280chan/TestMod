package testmod.relicsup;

import java.util.ArrayList;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class KeyOfTheVoidUp extends AbstractUpgradedRelic implements ClickableRelic {
	private boolean cardSelected = true;
	
	public KeyOfTheVoidUp() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
		this.counter = -2;
	}
	
	public void onVictory() {
		this.counter = -1;
		this.beginLongPulse();
    }
	
	public void justEnteredRoom(final AbstractRoom room) {
		this.onVictory();
	}
	
	public void atPreBattle() {
		this.stopPulse();
	}
	
	public void onEquip() {
		this.onVictory();
	}
	
	private void initPurgeCard() {
		this.cardSelected = false;
		if (AbstractDungeon.isScreenUp) {
			AbstractDungeon.dynamicBanner.hide();
			AbstractDungeon.overlayMenu.cancelButton.hide();
			AbstractDungeon.previousScreen = AbstractDungeon.screen;
		}

		CardGroup forPurge = p().masterDeck.getPurgeableCards();
		if (forPurge.size() > 0) {
			AbstractDungeon.gridSelectScreen.open(forPurge, 1, DESCRIPTIONS[1], false, false, true, true);
		} else {
			this.counter = -2;
		}
	}

	public void update() {
		super.update();
		if ((!this.cardSelected) && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			this.cardSelected = true;
			purgeCards(AbstractDungeon.gridSelectScreen.selectedCards);
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			p().relics.stream().filter(r -> r.tier == RelicTier.BOSS).forEach(r -> addRandomKey());
		} else if (!this.cardSelected && AbstractDungeon.screen != CurrentScreen.GRID
				&& AbstractDungeon.previousScreen != CurrentScreen.GRID) {
			this.cardSelected = true;
		}
	}

	private void purgeCards(ArrayList<AbstractCard> list) {
		if (list.size() % 2 == 1) {
			int w = list.size() / 2;
			for (int i = -w; i < w + 1; i++) {
				AbstractDungeon.topLevelEffects
						.add(new PurgeCardEffect(list.get(i + w),
								Settings.WIDTH / 2.0F + i * 60.0F * Settings.scale
										+ (i * 2 + Integer.signum(i)) * AbstractCard.IMG_WIDTH / 2.0F,
								Settings.HEIGHT / 2.0F));
			}
		} else {
			int w = list.size() / 2;
			for (int i = 0; i < list.size(); i++) {
				AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(list.get(i), Settings.WIDTH / 2.0F
						+ ((i - w) * 60.0F + 30f) * Settings.scale + ((i - w) * 2 + 1) * AbstractCard.IMG_WIDTH / 2.0F,
						Settings.HEIGHT / 2.0F));
			}
		}
		list.forEach(p().masterDeck::removeCard);
	}

	@Override
	public void onRightClick() {
		if (this.counter == -1 && !this.inCombat()) {
			initPurgeCard();
			this.counter = -2;
		}
	}

}