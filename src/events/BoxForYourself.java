package events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;
import utils.BoxForYourselfSelectScreen;

public class BoxForYourself extends AbstractImageEvent {
	public static final String IMG = TestMod.eventIMGPath("BoxForYourself");
	public static final String ID = TestMod.makeID("BoxForYourself");
	public static final String NAME = "BoxForYourself";
	public static final String CH_NAME = "给自己的盒子";
	public static final String[] DESCRIPTIONS = { "你在一系列坟墓间穿行，前方出现了一个圆形房间，中间是一个小台子，上面有着一个 #p~带有挪动痕迹的~ 大盒子。 NL 你无法判断盒子里有什么，但你能注意到有 #y~强大的力量~ 从盒子的两侧渗透出来。 NL 盒子上方贴着一张纸条。纸条上写着： NL “高塔之心在等待。” NL NL 这……是你自己的笔迹。",
			"打开盒子，你在里面找到了一件 #y宝物 。", "选择一件遗物存放。", "这究竟是怎么回事？" };
	public static final String[] OPTIONS = {"打开看看", "[取之与之] #g获得  #b", " #r然后存放一件遗物。 ", "[无视]", "[离开]"};
	private static final String DIALOG_1 = DESCRIPTIONS[0];
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
		super(CH_NAME, DIALOG_1, IMG);
		this.imageEventText.setDialogOption(OPTIONS[0]);
		initializeObtainRelic();
	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
			this.screen = CUR_SCREEN.CHOOSE;
			this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.obtainRelic.name + OPTIONS[2], null);
			this.imageEventText.setDialogOption(OPTIONS[3]);
			break;
		case CHOOSE:
			this.screen = CUR_SCREEN.COMPLETE;
			AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
			switch (buttonPressed) {
			case 0:
				getRelic();
				saveRelic();
				logMetric("Took Relic");
				break;
			default:
				logMetric("Ignored");
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
		if (RelicLibrary.isARelic(tmp))
			this.obtainRelic = RelicLibrary.getRelic(tmp).makeCopy();
		else if (RelicLibrary.isARelic(TestMod.makeID(tmp)))
			this.obtainRelic = RelicLibrary.getRelic(TestMod.makeID(tmp));
		else
			this.obtainRelic = RelicLibrary.getRelic("Juzu Bracelet");
	}
	
	public void update() {
		super.update();
		if ((this.relicSelect) && (indexSelected != -1)) {
			floorNum = AbstractDungeon.floorNum;
			saveRelic = AbstractDungeon.player.relics.get(indexSelected);
			System.out.println("遗物为" + saveRelic.name + ",准备开始删除遗物");
			saveRelic.onUnequip();
			AbstractDungeon.player.relics.remove(saveRelic);
			AbstractDungeon.player.reorganizeRelics();
			System.out.println("删除完毕");
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
		System.out.println("选了第" + index + "个遗物");
		indexSelected = index;
	}
	
	private void getRelic() {
		TestMod.obtain(AbstractDungeon.player, this.obtainRelic, true);
	}
	
	private void saveRelic() {
		this.relicSelect = true;
		new BoxForYourselfSelectScreen(DESCRIPTIONS[2], CH_NAME, "").open();
	}
	
	public void logMetric(String result) {
		AbstractEvent.logMetric(NAME, result);
	}
}