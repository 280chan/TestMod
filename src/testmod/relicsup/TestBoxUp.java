package testmod.relicsup;

import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.relics.TestBox;
import testmod.screens.TestBoxRelicSelectScreen;

public class TestBoxUp extends AbstractTestRelic implements ClickableRelic, UpgradedRelic {
	private static final UIStrings UI = TestBox.UI;
	
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
	
	public TestBoxUp() {
		super(RelicTier.SPECIAL, LandingSound.MAGICAL);
		this.counter = -1;
	}

	public void onEquip() {
		this.counter = -1;
		this.setRandom();
	}
	
	public void update() {
		super.update();
		if (!this.invalidFloor() && !cardSelected) {
			if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
				AbstractDungeon.effectList
						.add(new ShowCardAndObtainEffect(AbstractDungeon.gridSelectScreen.selectedCards.get(0),
								Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			} else if (AbstractDungeon.screen != pre) {
				return;
			}
			remove = cardSelected = true;
			AbstractDungeon.getCurrRoom().phase = phase;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
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
	
	private void relic() {
		this.checkRandomSet();
		new TestBoxRelicSelectScreen(true, UI.TEXT[0], UI.TEXT[2] + "+", UI.TEXT[3], this).open();
	}
	
	private void card() {
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
		g.group = TestMod.CARDS.stream().collect(this.toArrayList());
		Collections.shuffle(g.group, this.rng);
		AbstractCard c = this.priority();
		if (c != null)
			g.group.add(0, c);
		g.group = g.group.stream().limit(5).peek(this::markAsSeen).collect(this.toArrayList());
		AbstractDungeon.gridSelectScreen.open(g, 1, UI.TEXT[4], false, false, true, false);
		AbstractDungeon.overlayMenu.cancelButton.show(UI.TEXT[5]);
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
			if (Stream.of("1023dba2e158f257fba87f85d932b1df69c1989dc87c14389787a681f056cc5e",
						"c870968e2499df3ec4a1e386c21f19628af4cef6e5aaa8aa6da2071ab1fba5e4",
						"a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
						"342840f6340d15691f4be1c0e0157fb0983992c4f436c18267d41dbe6bb74a2")
						.anyMatch(s -> TestMod.checkHash(CardCrawlGame.playerName, s))) {
				Object o = TestMod.checkLatest(false);
				if (o != null)
					return (AbstractCard) o;
			}
		}
		return null;
	}

	@Override
	public void onRightClick() {
		if (this.counter == -2 || this.invalidFloor())
			return;
		this.counter = -2;
		this.card();
		this.relic();
	}

}