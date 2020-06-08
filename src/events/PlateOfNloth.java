package events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

import mymod.TestMod;
import utils.PlateOfNlothSelectScreen;

public class PlateOfNloth extends AbstractImageEvent {
	public static final String IMG = TestMod.eventIMGPath("BoxForYourself");
	public static final String ID = TestMod.makeID("PlateOfNloth");
	public static final String NAME = "PlateOfNloth";
	public static final String CH_NAME = "恩洛斯的盘子";
	public static final String[] DESCRIPTIONS = { "你在一系列坟墓间穿行，前方出现了一个圆形房间，中间是一个小台子，上面有着一个 #p~带有挪动痕迹的~ 大盘子。 NL 你无法判断盘子的主人，但你能注意到当你盯着它看时，你有想把什么东西放上去的 #y~强烈欲望~ 。 NL 盘子旁边贴着一张纸条。纸条上写着： NL “收购宝物 NL ——恩洛斯。”",
			"你将要无法继续忍受。在欲望的驱使下开始从自己的包中选择 #y宝物 想要将其放到盘中。", "选择一件遗物放到盘中。", "遗物消失了。天上忽然落下许多金币。你收集齐了所有金币，然后满足的离开。", "你忍住心中的欲望，直接转身离开。" };
	public static final String[] OPTIONS = {"[靠近盘子]", "[卖出遗物] #r失去一件遗物。  #g然后获得金币。 ", "[忍住欲望] #r失去", "点生命。 ", "[离开]"};
	private static final String DIALOG_1 = DESCRIPTIONS[0];
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
		super(CH_NAME, DIALOG_1, IMG);
		this.imageEventText.setDialogOption(OPTIONS[0]);
		this.damage = AbstractDungeon.player.maxHealth;
		if (AbstractDungeon.ascensionLevel <= 14) {
			this.damage *= DAMAGE_PERCENT;
		} else {
			this.damage *= ASCENSION_DAMAGE_PERCENT;
		}
		this.damage /= 100;
	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
			this.screen = CUR_SCREEN.CHOOSE;
			this.imageEventText.updateDialogOption(0, OPTIONS[1]);
			this.imageEventText.setDialogOption(OPTIONS[2] + this.damage + OPTIONS[3]);
			break;
		case CHOOSE:
			switch (buttonPressed) {
			case 0:
				giveRelic();
				logMetric("Give Relic");
				this.screen = CUR_SCREEN.SECONDARY;
				this.imageEventText.updateDialogOption(0, OPTIONS[4]);
				this.imageEventText.removeDialogOption(1);
				return;
			default:
				logMetric("Damaged");
				AbstractDungeon.player.damage(new DamageInfo(null, this.damage));
				this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
				this.screen = CUR_SCREEN.COMPLETE;
				this.imageEventText.updateDialogOption(0, OPTIONS[4]);
				this.imageEventText.removeDialogOption(1);
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
			System.out.println("遗物为" + giveRelic.name + ",准备开始删除遗物");
			giveRelic.onUnequip();
			AbstractDungeon.player.relics.remove(giveRelic);
			AbstractDungeon.player.reorganizeRelics();
			System.out.println("删除完毕");
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
		System.out.println("选了第" + index + "个遗物");
		indexSelected = index;
	}
	
	private void giveRelic() {
		this.relicSelect = true;
		new PlateOfNlothSelectScreen(DESCRIPTIONS[2]).open();
	}
	
	public void logMetric(String result) {
		AbstractEvent.logMetric(NAME, result);
	}
}