package events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;
import screens.BoxForYourselfSelectScreen;

public class BoxForYourself extends AbstractTestEvent {
	private static final String S_ID = "BoxForYourself";
	public static final String ID = TestMod.makeID(S_ID);
	private static final String IMG = TestMod.eventIMGPath(S_ID);
	private static final EventStrings ES = Strings(ID);
	private static final String NAME = ES.NAME;
	private static final String[] DESCRIPTIONS = ES.DESCRIPTIONS;
	private static final String[] OPTIONS = ES.OPTIONS;
	private CUR_SCREEN screen = CUR_SCREEN.INTRO;

	private AbstractRelic obtainRelic = null;
	private static AbstractRelic saveRelic = null;
	private boolean relicSelect = false;
	private static int indexSelected = -1;
	private static int floorNum = -2;
	
	private static enum CUR_SCREEN {
		INTRO, CHOOSE, COMPLETE;

		private CUR_SCREEN() {
		}
	}

	public BoxForYourself() {
		super(NAME, DESCRIPTIONS[0], IMG);
		this.imageEventText.setDialogOption(OPTIONS[0]);
		initializeObtainRelic();
	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
			this.screen = CUR_SCREEN.CHOOSE;
			this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.obtainRelic.name + OPTIONS[2]);
			this.imageEventText.setDialogOption(OPTIONS[3]);
			break;
		case CHOOSE:
			this.screen = CUR_SCREEN.COMPLETE;
			AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
			switch (buttonPressed) {
			case 0:
				getRelic();
				saveRelic();
				break;
			default:
				logMetricIgnored(NAME);
			}
			this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
			this.imageEventText.updateDialogOption(0, OPTIONS[4]);
			this.imageEventText.clearRemainingOptions();
			this.screen = CUR_SCREEN.COMPLETE;
			AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
			break;
		case COMPLETE:
			openMap();
		}
	}

	private void initializeObtainRelic() {
		String tmp = CardCrawlGame.playerPref.getString("BOX_RELIC", "Juzu Bracelet");
		this.obtainRelic = RelicLibrary
				.getRelic(RelicLibrary.isARelic(tmp) ? tmp
						: (RelicLibrary.isARelic(TestMod.makeID(tmp)) ? TestMod.makeID(tmp) : "Juzu Bracelet"))
				.makeCopy();
	}
	
	public void update() {
		super.update();
		if ((this.relicSelect) && (indexSelected != -1)) {
			floorNum = AbstractDungeon.floorNum;
			saveRelic = AbstractDungeon.player.relics.get(indexSelected);
			logMetricRelicSwap(NAME, "Swap Relic", this.obtainRelic, saveRelic);
			TestMod.info("遗物为" + saveRelic.name + ",准备开始删除遗物");
			saveRelic.onUnequip();
			AbstractDungeon.player.relics.remove(saveRelic);
			AbstractDungeon.player.reorganizeRelics();
			TestMod.info("删除完毕");
			indexSelected = -1;
			this.relicSelect = false;
		}
	}
	
	public static void updateThis() {
		checkSaveToFile();
	}
	
	private static void checkSaveToFile() {
		if (AbstractDungeon.floorNum == floorNum + 1) {
			CardCrawlGame.playerPref.putString("BOX_RELIC", saveRelic.relicId);
			saveRelic = null;
			floorNum = -2;
		}
	}
	
	public static void receiveIndexSelected(int index) {
		TestMod.info("选了第" + index + "个遗物");
		indexSelected = index;
	}
	
	private void getRelic() {
		TestMod.obtain(AbstractDungeon.player, this.obtainRelic, true);
	}
	
	private void saveRelic() {
		this.relicSelect = true;
		new BoxForYourselfSelectScreen(DESCRIPTIONS[2], NAME, "").open();
	}
}