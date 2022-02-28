package events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

import mymod.TestMod;
import screens.PlateOfNlothSelectScreen;

public class PlateOfNloth extends AbstractTestEvent {
	private static final int DAMAGE_PERCENT = 10;
	private static final int ASCENSION_DAMAGE_PERCENT = 15;

	private int damage = 0;
	private static AbstractRelic giveRelic = null;
	private boolean relicSelect = false;
	private static int indexSelected = -1;

	@Override
	protected void intro() {
		this.damage = (int) (p().maxHealth / 100.0
				* (AbstractDungeon.ascensionLevel <= 14 ? DAMAGE_PERCENT : ASCENSION_DAMAGE_PERCENT));
		this.imageEventText.updateBodyText(desc()[1]);
		this.imageEventText.updateDialogOption(0, option()[1], p().relics.size() < 1);
		this.imageEventText.setDialogOption(option()[2] + this.damage + option()[3]);
	}

	@Override
	protected void choose(int choice) {
		this.imageEventText.updateDialogOption(0, option()[4]);
		this.imageEventText.clearRemainingOptions();
		switch (choice) {
		case 0:
			giveRelic();
			return;
		default:
			logMetricTakeDamage(this.title, "Ignored", this.damage);
			p().damage(new DamageInfo(null, this.damage));
			this.imageEventText.updateBodyText(desc()[4]);
			return;
		}
	}
	
	public void update() {
		super.update();
		if ((this.relicSelect) && (indexSelected != -1)) {
			giveRelic = p().relics.get(indexSelected);
			logMetricGainGoldAndLoseRelic(this.title, "Sell", giveRelic, giveRelic.getPrice());
			TestMod.info("遗物为" + giveRelic.name + ",准备开始删除遗物");
			giveRelic.onUnequip();
			p().relics.remove(giveRelic);
			p().reorganizeRelics();
			TestMod.info("删除完毕");
			indexSelected = -1;
			this.getGold(giveRelic);
			this.imageEventText.updateBodyText(desc()[3]);
			this.relicSelect = false;
		}
	}
	
	private void getGold(AbstractRelic r) {
        AbstractDungeon.effectList.add(new RainingGoldEffect(r.getPrice()));
		p().gainGold(r.getPrice());
		giveRelic = null;
	}
	
	public static void receiveIndexSelected(int index) {
		TestMod.info("选了第" + index + "个遗物");
		indexSelected = index;
	}
	
	private void giveRelic() {
		this.relicSelect = true;
		new PlateOfNlothSelectScreen(desc()[2]).open();
	}
}