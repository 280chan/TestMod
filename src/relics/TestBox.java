package relics;

import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import mymod.TestMod;
import utils.AdvanceClickableRelic;
import utils.TestBoxRelicSelectScreen;

public class TestBox extends AbstractTestRelic implements AdvanceClickableRelic<TestBox> {
	
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
	
	public TestBox() {
		super(RelicTier.SPECIAL, LandingSound.MAGICAL);
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
			AbstractDungeon.player.relics.remove(this);
			AbstractDungeon.player.reorganizeRelics();
		}
	}
	
	private boolean checkFoolsDay() {
		return this.getMonth() == 4 && this.getDate() == 1;
	}
	
	private static final String FOOLS_DAY = "愚人节快乐";
	
	private void relic() {
		if (this.counter == -2 || this.invalidFloor())
			return;
		String desc = this.checkFoolsDay() ? FOOLS_DAY : "Test礼物盒";
		this.checkRandomSet();
		new TestBoxRelicSelectScreen(true, "选择获得一件遗物，或者跳过。", desc, "礼物，道具，灾厄，或者逃避", this).open();
		this.counter = -2;
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
		g.group = TestMod.CARDS.stream().collect(this.collectToArrayList());
		Collections.shuffle(g.group, this.rng);
		AbstractCard c = this.priority();
		if (c != null)
			g.group.add(0, c);
		g.group = g.group.stream().limit(3).peek(this::markAsSeen).collect(this.collectToArrayList());
		String desc = this.checkFoolsDay() ? FOOLS_DAY : "选择一张牌";
		AbstractDungeon.gridSelectScreen.open(g, 1, desc, false, false, true, false);
		AbstractDungeon.overlayMenu.cancelButton.show("跳过");
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
			Stream.of("BrkStarshine", "280 chan");
			if ("BrkStarshine".equals(CardCrawlGame.playerName) || "280 chan".equals(CardCrawlGame.playerName)) {
				Object o = TestMod.checkLatest(false);
				if (o != null)
					return (AbstractCard) o;
			}
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