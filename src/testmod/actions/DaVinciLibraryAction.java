package testmod.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public class DaVinciLibraryAction extends AbstractGameAction implements MiscMethods {
	private static final UIStrings UI = MISC.uiString();
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
		group.group.addAll(this.group.group);
		ArrayList<AbstractCard> tmp = AbstractDungeon.srcColorlessCardPool.group.stream()
				.filter(c -> !c.hasTag(CardTags.HEALING)).collect(this.toArrayList());
		Collections.shuffle(tmp, new Random(AbstractDungeon.cardRng.randomLong()));
		tmp.stream().limit(AMOUNT - group.size()).forEach(group::addToBottom);
		group.group.stream().peek(this::checkEggs).map(c -> c.cardID).forEach(UnlockTracker::markCardAsSeen);
		AbstractDungeon.gridSelectScreen.open(group, 1, UI.TEXT[0], false, false, true, false);
		AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
	}

	private void checkEggs(AbstractCard c) {
		AbstractPlayer p = p();
		boolean hasEgg = (c.type == CardType.ATTACK && p.hasRelic("Molten Egg 2"))
				|| (c.type == CardType.POWER && p.hasRelic("Frozen Egg 2"))
				|| (c.type == CardType.SKILL && p.hasRelic("Toxic Egg 2"));
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
			p().relics.forEach(r -> r.onObtainCard(c));
			c.shrink();
			CardGroup group = p().masterDeck;
			group.addToTop(c);
			this.addHoarderCard(group, c);
			p().relics.forEach(AbstractRelic::onMasterDeckChange);
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
