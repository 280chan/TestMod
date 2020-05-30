package events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;

public class 示例事件 extends AbstractImageEvent {
	public static final String ID = TestMod.makeID("id");
	public static final String NAME = "";
	public static final String[] DESCRIPTIONS = {};
	public static final String[] OPTIONS = {};
	private static final String DIALOG_1 = DESCRIPTIONS[0];
	private CUR_SCREEN screen = CUR_SCREEN.INTRO;

	private static enum CUR_SCREEN {
		INTRO, CHOOSE, COMPLETE;

		private CUR_SCREEN() {
		}
	}

	public 示例事件() {
		super(NAME, DIALOG_1, "images/events/selfNote.jpg");
		this.imageEventText.setDialogOption(OPTIONS[0]);

	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
			this.screen = CUR_SCREEN.CHOOSE;
			this.imageEventText.updateDialogOption(0, OPTIONS[1] + OPTIONS[2], null);
			this.imageEventText.setDialogOption(OPTIONS[3]);
			break;
		case CHOOSE:
			this.screen = CUR_SCREEN.COMPLETE;
			AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
			switch (buttonPressed) {
			case 0:

				logMetric("Took Card");
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

	public void update() {
		super.update();
	}

	public void logMetric(String result) {
		AbstractEvent.logMetric(NAME, result);
	}
}