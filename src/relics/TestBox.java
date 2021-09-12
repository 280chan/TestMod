package relics;

import java.util.Random;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import mymod.TestMod;
import utils.MiscMethods;
import utils.TestBoxRelicSelectScreen;

public class TestBox extends AbstractDoubleClickableRelic implements MiscMethods {
	public static final String ID = "TestBox";
	
	public boolean relicSelected = true;
	private boolean cardSelected = true;
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
		super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
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
			cardSelected = true;
			AbstractDungeon.getCurrRoom().phase = phase;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
		}
	}
	
	private boolean checkFoolsDay() {
		return this.getMonth() == 4 && this.getDate() == 1;
	}
	
	private static final String FOOLS_DAY = "愚人节快乐";
	
	@Override
	protected void onRightClick() {
		if (this.checkFoolsDay()) {
			this.card();
		} else {
			this.relic();
		}
	}
	
	@Override
	protected void onDoubleRightClick() {
		if (this.checkFoolsDay()) {
			this.relic();
		} else {
			this.card();
		}
	}
	
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
		AbstractCard c = this.priority();
		if (c != null)
			g.group.add(c);
		while(g.group.size() < 3) {
			boolean repeat = false;
			c = TestMod.randomItem(TestMod.CARDS, this.rng);
			for (AbstractCard ca : g.group)
				if (ca.cardID.equals(c.cardID))
					repeat = true;
			if (repeat)
				continue;
			g.group.add(c);
			UnlockTracker.markCardAsSeen(c.cardID);
		}
		String desc = "选择一张牌";
		if (this.checkFoolsDay())
			desc = FOOLS_DAY;
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
			if ("BrkStarshine".equals(CardCrawlGame.playerName) || "280 chan".equals(CardCrawlGame.playerName)) {
				Object o = TestMod.checkLatest(false);
				if (o != null)
					return (AbstractCard) o;
			}
		}
		return null;
	}

}