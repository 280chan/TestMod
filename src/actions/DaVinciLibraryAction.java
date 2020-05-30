package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import utils.MiscMethods;

public class DaVinciLibraryAction extends AbstractGameAction implements MiscMethods {

	private float startingDuration;
	private CardGroup group;
	private boolean pickCard = false;
	private CurrentScreen pre, before;

	public DaVinciLibraryAction(ArrayList<AbstractCard> group, CurrentScreen pre) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.group = new CardGroup(CardGroupType.UNSPECIFIED);
		this.pre = pre;
		for (AbstractCard c : group) {
			this.group.addToBottom(c);
		}
	}

	private void openScreen() {
		this.pickCard = true;
		CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
		AbstractCard card = null;
		for (int i = 0; i < 20; i++) {
			card = this.group.getRandomCard(true);
			if (!group.contains(card)) {
				this.checkEggs(card);
				group.addToBottom(card);
			} else {
				i--;
			}
		}
		for (AbstractCard c : group.group) {
			UnlockTracker.markCardAsSeen(c.cardID);
		}
		AbstractDungeon.gridSelectScreen.open(group, 1, "达芬奇之心：选择获得1张牌。", false, false, true, false);
		AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
	}

	private void checkEggs(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		boolean hasEgg = false;
		if (c.type == CardType.ATTACK && p.hasRelic("Molten Egg 2")) {
			hasEgg = true;
		} else if (c.type == CardType.POWER && p.hasRelic("Frozen Egg 2")) {
			hasEgg = true;
		} else if (c.type == CardType.SKILL && p.hasRelic("Toxic Egg 2")) {
			hasEgg = true;
		}
		if (hasEgg) {
			c.upgrade();
		}
	}
	
	private boolean checkScreen(CurrentScreen s) {
		if (before == s)
			return true;
		if (AbstractDungeon.screen == s)
			return true;
		return false;
	}
	
	@Override
	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			System.out.println("准备打开图书馆界面");
			openScreen();
			tickDuration();
		} else if ((this.pickCard) && (this.checkScreen(CurrentScreen.GRID)) && (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty())) {
			AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeCopy();
			System.out.println("选择了" + c.name);
			for (AbstractRelic r : AbstractDungeon.player.relics) {
				r.onObtainCard(c);
			}
			c.shrink();
			CardGroup group = AbstractDungeon.player.masterDeck;
			group.addToTop(c);
			this.addHoarderCard(group, c);
			for (AbstractRelic r : AbstractDungeon.player.relics) {
				r.onMasterDeckChange();
			}
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
			System.out.println("已获得" + c.name);
		} else if (AbstractDungeon.screen == pre && AbstractDungeon.overlayMenu.cancelButton.isHidden) {
			this.isDone = true;
			System.out.println("已取消");
		}
		if (before != AbstractDungeon.screen) {
			System.out.print("检测到界面切换：之前界面: " + before);
			before = AbstractDungeon.screen;
			System.out.println("，当前界面: " + before);
		}
	}

}
