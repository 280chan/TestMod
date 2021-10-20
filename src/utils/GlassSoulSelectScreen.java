package utils;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import relics.GlassSoul;

public class GlassSoulSelectScreen extends RelicSelectScreen implements MiscMethods {
	private GlassSoul gs;
	
	public GlassSoulSelectScreen(String bDesc, GlassSoul r) {
		super(null, true, r.counter < 1);
		this.gs = r;
		this.setDescription(bDesc, r.name, "免费选择一个遗物获得");
	}

	@Override
	protected void addRelics() {
		this.gs.relics.stream().map(RelicLibrary::getRelic).map(r -> r.makeCopy()).forEach(this.relics::add);
	}

	public void playCantBuySfx() {
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
		if (this.gs.counter > 0) {
			this.gs.counter--;
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
		return Math.max(r.getPrice() * (int) (p().relics.stream().filter(a -> a.relicId.equals(r.relicId)).count())
				/ GlassSoul.PRICE_RATE, 10);
	}
	
	@Override
	protected String categoryOf(AbstractRelic r) {
		return price(r) + "";
	}

	@Override
	protected String descriptionOfCategory(String category) {
		return "10".equals(category) ? "费用过低的遗物，需要10金币兑换" : (category + "金币可兑换的遗物");
	}
}