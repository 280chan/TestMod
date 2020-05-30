package relics;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import mymod.TestMod;
import utils.TestBoxRelicSelectScreen;

public class TestBox extends AbstractDoubleClickableRelic {
	public static final String ID = "TestBox";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "右键单击或右键双击本遗物，从遗物和卡牌中选择一类，再从随机的 #b3 件本 #bmod 内的该类物品中选择 #b1 件获得。使用后本遗物失效。";//遗物效果的文本描叙。
	
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
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.SPECIAL, LandingSound.MAGICAL);
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
		if (this.invalidFloor())
			return;
		if (!cardSelected) {
			if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
				cardSelected = true;
				for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
					AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F,
							Settings.HEIGHT / 2.0F));
				}
				AbstractDungeon.getCurrRoom().phase = phase;
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				
			} else if (AbstractDungeon.screen == pre) {
				cardSelected = true;
				AbstractDungeon.getCurrRoom().phase = phase;
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
			}
		}
	}
	
	@Override
	protected void onRightClick() {
		if (this.counter == -2 || this.invalidFloor())
			return;
		this.checkRandomSet();
		new TestBoxRelicSelectScreen(true, "选择获得一件遗物，或者跳过。", "Test礼物盒", "礼物，道具，灾厄，或者逃避", this).open();
		this.counter = -2;
	}
	
	private void checkRandomSet() {
		if (this.rng == null)
			this.setRandom();
	}
	
	public void setRandom() {
		this.rng = new Random(Settings.seed);
	}
	
	@Override
	protected void onDoubleRightClick() {
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
		while(g.group.size() < 3) {
			boolean repeat = false;
			AbstractCard c = TestMod.randomItem(TestMod.CARDS, this.rng);
			for (AbstractCard ca : g.group)
				if (ca.cardID.equals(c.cardID))
					repeat = true;
			if (repeat)
				continue;
			g.group.add(c);
			UnlockTracker.markCardAsSeen(c.cardID);
		}
		AbstractDungeon.gridSelectScreen.open(g, 1, "选择一张牌", false, false, true, false);
		AbstractDungeon.overlayMenu.cancelButton.show("跳过");
		this.counter = -2;
	}

}