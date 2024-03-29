package testmod.relics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Predicate;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import testmod.mymod.TestMod;
import testmod.screens.TestBoxRelicSelectScreen;
import testmod.utils.AdvanceClickableRelic;

public class TestBox extends AbstractTestRelic implements AdvanceClickableRelic<TestBox> {
	public static final UIStrings UI = MISC.uiString();
	
	public boolean relicSelected = true;
	private boolean cardSelected = true;
	private boolean remove = false;
	public static RoomPhase phase;
	private static CurrentScreen pre;
	public Random rng;
	
	private boolean invalidFloor() {
		boolean tmp = AbstractDungeon.floorNum > 0;
		if (tmp || this.counter == -2) {
			this.stopPulse();
		}
		if (!tmp && this.counter == -1 && !this.pulse) { 
			this.beginLongPulse();
		}
		return tmp;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + DESCRIPTIONS[Loader.isModLoaded("RelicUpgradeLib") ? 2 : 1];
	}
	
	public TestBox() {
		this.setDuration(300).addRightClickActions(null, () -> {
			if (this.checkFoolsDay()) {
				this.relic();
			} else {
				this.card();
			}
		});
	}

	public void onEquip() {
		this.counter = -1;
		this.setRandom();
	}
	
	public void update() {
		super.update();
		if (!this.invalidFloor() && !cardSelected) {
			if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
				AbstractDungeon.effectList.add(
						new ShowCardAndObtainEffect(AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeCopy(),
								Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			} else if (AbstractDungeon.screen != pre) {
				return;
			}
			remove = cardSelected = true;
			AbstractDungeon.getCurrRoom().phase = phase;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			AbstractDungeon.gridSelectScreen.targetGroup.group.forEach(TestBox::removePreview);
			AbstractDungeon.gridSelectScreen.targetGroup.group.clear();
		}
	}
	
	public void onEnterRoom(AbstractRoom r) {
		if (AbstractDungeon.floorNum > 0)
			remove = true;
	}
	
	public void postUpdate() {
		if (remove) {
			p().relics.remove(this);
			p().reorganizeRelics();
		}
	}
	
	private boolean checkFoolsDay() {
		return this.getMonth() == 4 && this.getDate() == 1;
	}
	
	private void relic() {
		if (this.counter == -2 || this.invalidFloor())
			return;
		this.checkRandomSet();
		new TestBoxRelicSelectScreen(UI.TEXT[0], this.checkFoolsDay() ? UI.TEXT[1] : UI.TEXT[2], UI.TEXT[3], this.rng,
				false).open();
		this.counter = -2;
	}
	
	public static void removePreview(AbstractCard c) {
		if (MultiCardPreview.multiCardPreview.get(c) != null) {
			MultiCardPreview.clear(c);
		} else {
			c.cardsToPreview = null;
		}
	}
	
	public static void createPreview(AbstractCard c) {
		ArrayList<AbstractCard> tmp = MultiCardPreview.multiCardPreview.get(c);
		if (tmp != null && !tmp.isEmpty()) {
			tmp.add(0, c.makeCopy());
			tmp.get(0).upgrade();
		} else if (c.cardsToPreview == null) {
			c.cardsToPreview = c.makeCopy();
			c.cardsToPreview.upgrade();
		} else {
			AbstractCard pre = c.makeCopy();
			pre.upgrade();
			MultiCardPreview.add(c, pre, c.cardsToPreview);
			c.cardsToPreview = null;
		}
	}
	
	private void card() {
		if (this.counter == -2 || this.invalidFloor())
			return;
		this.checkRandomSet();
		pre = AbstractDungeon.screen;
		if (AbstractDungeon.isScreenUp) {
			AbstractDungeon.dynamicBanner.hide();
			AbstractDungeon.previousScreen = AbstractDungeon.screen;
		}
		phase = AbstractDungeon.getCurrRoom().phase;
		AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;
		this.cardSelected = false;
		CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
		g.group = TestMod.CARDS.stream().map(c -> c.makeCopy()).collect(this.toArrayList());
		Collections.shuffle(g.group, this.rng);
		AbstractCard c = this.priority();
		Predicate<AbstractCard> p = a -> c == null || !a.cardID.equals(c.cardID);
		g.group = g.group.stream().filter(p).limit(c != null ? 2 : 3).collect(this.toArrayList());
		if (c != null)
			g.group.add(0, c);
		g.group.forEach(this::markAsSeen);
		g.group.forEach(TestBox::createPreview);
		AbstractDungeon.gridSelectScreen.open(g, 1, this.checkFoolsDay() ? UI.TEXT[1] : UI.TEXT[4], false, false, true,
				false);
		AbstractDungeon.overlayMenu.cancelButton.show(UI.TEXT[5]);
		this.counter = -2;
	}
	
	private void checkRandomSet() {
		if (this.rng == null)
			this.setRandom();
	}
	
	public void setRandom() {
		this.rng = new Random(Settings.seed);
	}
	
	private AbstractCard priority() {
		if (!Settings.seedSet) {
			/*if (Stream.of("1023dba2e158f257fba87f85d932b1df69c1989dc87c14389787a681f056cc5e",
						"c870968e2499df3ec4a1e386c21f19628af4cef6e5aaa8aa6da2071ab1fba5e4",
						"a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
						"342840f6340d15691f4be1c0e0157fb0983992c4f436c18267d41dbe6bb74a2")
						.anyMatch(s -> TestMod.checkHash(CardCrawlGame.playerName, s))) {
				Object o = TestMod.checkLatest(false);
				if (o != null)
					return (AbstractCard) o;
			}*/
			ArrayList<AbstractCard> l = TestMod.CARDS.stream().map(c -> CardLibrary.getCard(c.cardID))
					.filter(c -> !c.isSeen).map(c -> (AbstractCard) c).collect(toArrayList());
			return Loader.isModLoaded("chronoMods") || l.size() == 0 ? null : TestMod.randomItem(l, this.rng);
		}
		return null;
	}

	@Override
	public void onSingleRightClick() {
		if (this.checkFoolsDay()) {
			this.card();
		} else {
			this.relic();
		}
	}

	@Override
	public void onEachRightClick() {
	}

	@Override
	public void onDurationEnd() {
	}

}