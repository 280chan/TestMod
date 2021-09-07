package events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

import mymod.TestMod;
import utils.PlateOfNlothSelectScreen;

public class PlateOfNloth extends AbstractTestEvent {
	public static final String IMG = TestMod.eventIMGPath("BoxForYourself");
	public static final String ID = TestMod.makeID("PlateOfNloth");
	private static final EventStrings ES = Strings(ID);
	private static final String NAME = ES.NAME;
	private static final String[] DESCRIPTIONS = ES.DESCRIPTIONS;
	private static final String[] OPTIONS = ES.OPTIONS;
	private static final int DAMAGE_PERCENT = 10;
	private static final int ASCENSION_DAMAGE_PERCENT = 15;
	private CUR_SCREEN screen = CUR_SCREEN.INTRO;

	private int damage = 0;
	private static AbstractRelic giveRelic = null;
	private boolean relicSelect = false;
	private static int indexSelected = -1;
	
	private static enum CUR_SCREEN {
		INTRO, CHOOSE, SECONDARY, COMPLETE;

		private CUR_SCREEN() {
		}
	}

	public PlateOfNloth() {
		super(NAME, DESCRIPTIONS[0], IMG);
		this.imageEventText.setDialogOption(OPTIONS[0]);
		this.damage = AbstractDungeon.player.maxHealth;
		this.damage *= (AbstractDungeon.ascensionLevel <= 14 ? DAMAGE_PERCENT : ASCENSION_DAMAGE_PERCENT);
		this.damage /= 100;
	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
			this.screen = CUR_SCREEN.CHOOSE;
			if (AbstractDungeon.player.relics.size() > 0)
				this.imageEventText.updateDialogOption(0, OPTIONS[1]);
			else
				this.imageEventText.updateDialogOption(0, OPTIONS[5], true);
			this.imageEventText.setDialogOption(OPTIONS[2] + this.damage + OPTIONS[3]);
			break;
		case CHOOSE:
			switch (buttonPressed) {
			case 0:
				giveRelic();
				this.screen = CUR_SCREEN.SECONDARY;
				this.imageEventText.updateDialogOption(0, OPTIONS[4]);
				this.imageEventText.removeDialogOption(1);
				return;
			default:
				logMetricTakeDamage(NAME, "Ignored", this.damage);
				AbstractDungeon.player.damage(new DamageInfo(null, this.damage));
				this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
				this.screen = CUR_SCREEN.COMPLETE;
				this.imageEventText.updateDialogOption(0, OPTIONS[4]);
				this.imageEventText.clearRemainingOptions();
				return;
			}
		case SECONDARY:
			openMap();
			break;
		case COMPLETE:
			openMap();
		}
	}
	
	public void update() {
		super.update();
		if ((this.relicSelect) && (indexSelected != -1)) {
			giveRelic = AbstractDungeon.player.relics.get(indexSelected);
			logMetricGainGoldAndLoseRelic(NAME, "Sell", giveRelic, giveRelic.getPrice());
			TestMod.info("遗物为" + giveRelic.name + ",准备开始删除遗物");
			giveRelic.onUnequip();
			AbstractDungeon.player.relics.remove(giveRelic);
			AbstractDungeon.player.reorganizeRelics();
			TestMod.info("删除完毕");
			indexSelected = -1;
			this.getGold(giveRelic);
			this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
			this.relicSelect = false;
		}
	}
	
	private void getGold(AbstractRelic r) {
        AbstractDungeon.effectList.add(new RainingGoldEffect(r.getPrice()));
		AbstractDungeon.player.gainGold(r.getPrice());
		giveRelic = null;
	}
	
	public static void receiveIndexSelected(int index) {
		TestMod.info("选了第" + index + "个遗物");
		indexSelected = index;
	}
	
	private void giveRelic() {
		this.relicSelect = true;
		new PlateOfNlothSelectScreen(DESCRIPTIONS[2]).open();
	}
}