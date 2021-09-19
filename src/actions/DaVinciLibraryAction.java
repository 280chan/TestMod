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

import mymod.TestMod;
import utils.MiscMethods;

public class DaVinciLibraryAction extends AbstractGameAction implements MiscMethods {
	private static final int AMOUNT = 20;
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
		this.group.group.addAll(group);
	}

	private void openScreen() {
		this.pickCard = true;
		CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
		if (this.group.size() > AMOUNT) {
			ArrayList<AbstractCard> tmp = this.group.group.stream().collect(this.collectToArrayList());
			group.group = tmp;
			group.shuffle(AbstractDungeon.cardRng);
			group.group = group.group.stream().limit(AMOUNT).collect(this.collectToArrayList());
		} else {
			group.group.addAll(this.group.group);
			while (group.size() < AMOUNT) {
				AbstractCard card = AbstractDungeon.returnTrulyRandomColorlessCardInCombat();
				if (group.group.stream().map(c -> c.cardID).noneMatch(card.cardID::equals)) {
					group.addToBottom(card);
				}
			}
		}
		group.group.stream().peek(this::checkEggs).map(c -> c.cardID).forEach(UnlockTracker::markCardAsSeen);
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
		if (hasEgg && c.canUpgrade()) {
			c.upgrade();
		}
	}
	
	private boolean checkScreen(CurrentScreen s) {
		return before == s || AbstractDungeon.screen == s;
	}
	
	@Override
	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			TestMod.info("准备打开图书馆界面");
			openScreen();
			tickDuration();
		} else if (this.pickCard && this.checkScreen(CurrentScreen.GRID)
				&& !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			TestMod.info("选择了" + c.name);
			AbstractDungeon.player.relics.forEach(r -> { r.onObtainCard(c); });
			c.shrink();
			CardGroup group = AbstractDungeon.player.masterDeck;
			group.addToTop(c);
			this.addHoarderCard(group, c);
			AbstractDungeon.player.relics.forEach(AbstractRelic::onMasterDeckChange);
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
			TestMod.info("已获得" + c.name);
		} else if (AbstractDungeon.screen == pre && AbstractDungeon.overlayMenu.cancelButton.isHidden) {
			this.isDone = true;
			TestMod.info("已取消");
		}
		if (before != AbstractDungeon.screen) {
			String tmp = "检测到界面切换：之前界面: " + before;
			before = AbstractDungeon.screen;
			TestMod.info(tmp + "，当前界面: " + before);
		}
	}

}
