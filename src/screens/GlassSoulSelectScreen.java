package screens;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import relics.GlassSoul;
import utils.MiscMethods;

public class GlassSoulSelectScreen extends RelicSelectScreen implements MiscMethods {
	private static final UIStrings UI = INSTANCE.uiString();
	private GlassSoul gs;
	
	public GlassSoulSelectScreen(String bDesc, GlassSoul r) {
		super(null, true, true);
		this.gs = r;
		this.setDescription(bDesc, r.name, UI.TEXT[0]);
	}

	@Override
	protected void addRelics() {
		this.gs.relics.stream().map(RelicLibrary::getRelic).map(r -> r.makeCopy()).forEach(this.relics::add);
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
	
	@Override
	protected void afterSelected() {
		if (this.gs.counter >= gs.streamOf(this.selectedRelic.relicId).count()) {
			this.gs.counter -= gs.streamOf(this.selectedRelic.relicId).count();
		} else if (p().gold < price(this.selectedRelic)) {
			playCantBuySfx();
			createCantBuyMsg();
			this.rejectSelection = true;
			return;
		} else {
			p().loseGold(price(this.selectedRelic));
		}
		this.gs.relics.remove(this.selectedRelic.relicId);
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

	private int price(AbstractRelic r) {
		return Math.max(r.getPrice() * (int) (gs.streamOf(r.relicId).count()) / GlassSoul.PRICE_RATE, 10);
	}
	
	@Override
	protected String categoryOf(AbstractRelic r) {
		if (gs.counter >= gs.streamOf(r.relicId).count()) {
			return gs.streamOf(r.relicId).count() + "";
		}
		return price(r) + "g";
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