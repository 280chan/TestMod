package events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;
import screens.BoxForYourselfSelectScreen;

public class BoxForYourself extends AbstractTestEvent {
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
		super();
		this.imageEventText.setDialogOption(option()[0]);
		initializeObtainRelic();
	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.imageEventText.updateBodyText(desc()[1]);
			this.screen = CUR_SCREEN.CHOOSE;
			this.imageEventText.updateDialogOption(0, option()[1] + this.obtainRelic.name + option()[2]);
			this.imageEventText.setDialogOption(option()[3]);
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
				logMetricIgnored(this.title);
			}
			this.imageEventText.updateBodyText(desc()[3]);
			this.imageEventText.updateDialogOption(0, option()[4]);
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
		this.obtainRelic = RelicLibrary.getRelic(RelicLibrary.isARelic(tmp) ? tmp : "Juzu Bracelet").makeCopy();
	}
	
	public void update() {
		super.update();
		if ((this.relicSelect) && (indexSelected != -1)) {
			floorNum = AbstractDungeon.floorNum;
			saveRelic = p().relics.get(indexSelected);
			logMetricRelicSwap(this.title, "Swap Relic", this.obtainRelic, saveRelic);
			TestMod.info("遗物为" + saveRelic.name + ",准备开始删除遗物");
			saveRelic.onUnequip();
			p().relics.remove(saveRelic);
			p().reorganizeRelics();
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
		TestMod.obtain(p(), this.obtainRelic, true);
	}
	
	private void saveRelic() {
		this.relicSelect = true;
		new BoxForYourselfSelectScreen(desc()[2], this.title, "").open();
	}
}