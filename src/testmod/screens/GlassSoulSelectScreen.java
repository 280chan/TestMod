package testmod.screens;

import java.util.ArrayList;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

import testmod.relics.GlassSoul;
import testmod.relicsup.GlassSoulUp;
import testmod.utils.MiscMethods;

public class GlassSoulSelectScreen extends RelicSelectScreen implements MiscMethods {
	private static final UIStrings UI = MISC.uiString();
	private ArrayList<String> list;
	private AbstractRelic r;
	
	public GlassSoulSelectScreen(ArrayList<String> relics, AbstractRelic r) {
		super(null, true, true);
		this.list = relics;
		this.r = r;
		this.setDescription("", r.name, UI.TEXT[0]);
	}

	@Override
	protected void addRelics() {
		this.list.stream().map(RelicLibrary::getRelic).map(r -> tryUpgrade(r.makeCopy())).forEach(this.relics::add);
	}

	public static void playCantBuySfx() {
		double roll = 3 * Math.random();
		if (roll < 1) {
			CardCrawlGame.sound.play("VO_MERCHANT_2A");
		} else if (roll < 2) {
			CardCrawlGame.sound.play("VO_MERCHANT_2B");
		} else {
			CardCrawlGame.sound.play("VO_MERCHANT_2C");
		}
	}
	
	public void createCantBuyMsg() {
		String tmp = ShopScreen.getCantBuyMsg();
		AbstractDungeon.effectList.add(new SpeechBubble(p().dialogX, p().dialogY, 4.0F, tmp, true));
		print(tmp);
	}
	
	public static int amountRate(AbstractRelic r) {
		return (MISC.relicStream(GlassSoulUp.class).count() == 0 || !(MISC.canUpgrade(r) || MISC.upgraded(r)) ? 1 : 2)
				* (int) Math.max(MISC.p().relics.stream().filter(a -> a.relicId.equals(r.relicId)).count(), 1);
	}
	
	@Override
	protected void afterSelected() {
		if (this.r.counter >= counterRate() * amountRate(this.selectedRelic)) {
			this.r.counter -= counterRate() * amountRate(this.selectedRelic);
		} else if (p().gold < price(this.selectedRelic)) {
			playCantBuySfx();
			createCantBuyMsg();
			this.rejectSelection = true;
			return;
		} else {
			p().loseGold(price(this.selectedRelic));
		}
		this.list.remove(this.selectedRelic.relicId);
		if (AbstractDungeon.currMapNode == null) {
			this.selectedRelic.instantObtain();
		} else {
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
					this.selectedRelic);
		}
	}

	@Override
	protected void afterCanceled() {
	}
	
	private int counterRate() {
		return this.r instanceof GlassSoul ? GlassSoul.COUNTER_RATE : GlassSoulUp.COUNTER_RATE;
	}
	
	private int priceRate() {
		return this.r instanceof GlassSoul ? GlassSoul.PRICE_RATE : GlassSoulUp.PRICE_RATE;
	}

	private int price(AbstractRelic r) {
		return Math.max(r.getPrice() * amountRate(r) / priceRate(), 10);
	}
	
	@Override
	protected String categoryOf(AbstractRelic r) {
		return this.r.counter >= counterRate() * amountRate(r) ? amountRate(r) + "" : price(r) + "g";
	}

	@Override
	protected String descriptionOfCategory(String category) {
		if (category.endsWith("g")) {
			category = category.substring(0, category.length() - 1);
			return "10".equals(category) ? UI.TEXT[1] : (category + UI.TEXT[2]);
		} else {
			return category + UI.TEXT[0];
		}
	}
}