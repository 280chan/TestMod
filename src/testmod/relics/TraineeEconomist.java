package testmod.relics;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

import testmod.relicsup.TraineeEconomistUp;
import testmod.utils.Economist;

public class TraineeEconomist extends AbstractTestRelic implements Economist {
	public static final int DELTA_BONUS = 5;
	public static final int DELTA_PRICE = 5;
	public static final double PERCENTAGE = 100.0;
	private static boolean used = false;
	
	public String getUpdatedDescription() {
		return (this.counter < 1) ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2] + toPercent(this.rate())
						+ DESCRIPTIONS[3];
	}

	private static String toPercent(double input) {
		return ((int) (input * 10000)) / PERCENTAGE + "";
	}

	private String goldRatePercent() {
		return 100 + DELTA_BONUS * this.counter + "";
	}
	
	private double gainGoldRate() {
		return 1 + DELTA_BONUS * this.counter / PERCENTAGE;
	}
	
	@Override
	public float rate() {
		return priceRate(this.counter);
	}
	
	private static float priceRate(int counter) {
		return (float) Math.pow((100 - DELTA_PRICE) / PERCENTAGE, counter);
	}
	
	private static float totalRate() {
		return MISC.p().relics.stream().filter(r -> r instanceof Economist).map(r -> ((Economist) r).rate()).reduce(1f,
				(a, b) -> a * b);
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	public void onMonsterDeath(AbstractMonster m) {
		this.counter += m.type.ordinal() + 1;
		this.updateDescription();
	}
	
	public double gainGold(double amount) {
		this.flash();
		return amount * gainGoldRate();
	}
	
	public static void addDiscount(float rate) {
		AbstractDungeon.shopScreen.applyDiscount(rate, true);
	}
	/*public void onPreviewObtainCard(AbstractCard c) {
		System.out.println("生成卡牌降价前" + c.name + c.price);
		if (this.isActive && Stream.of(new Exception().getStackTrace()).noneMatch(
				e -> ShopScreen.class.getName().equals(e.getClassName()) && "initCards".equals(e.getMethodName())))
			c.price = MathUtils.round(c.price * totalRate());
		System.out.println("生成卡牌降价后" + c.name + c.price);
	}*/
	
	@SpirePatch(clz = ShopScreen.class, method = "setPrice")
	public static class ShopScreenCardPatch {
		@SpirePostfixPatch
		public static void Postfix(ShopScreen __instance, AbstractCard card) {
			card.price = MathUtils.round(card.price * totalRate());
		}
	}
	
	@SpirePatch(clz = ShopScreen.class, method = "getNewPrice", paramtypez = { StorePotion.class })
	public static class ShopScreenPotionPatch {
		@SpirePostfixPatch
		public static void Postfix(ShopScreen __instance, StorePotion p) {
			p.price = MathUtils.round(p.price * totalRate());
		}
	}
	
	@SpirePatch(clz = ShopScreen.class, method = "getNewPrice", paramtypez = { StoreRelic.class })
	public static class ShopScreenRelicPatch {
		@SpirePostfixPatch
		public static void Postfix(ShopScreen __instance, StoreRelic r) {
			r.price = MathUtils.round(r.price * totalRate());
		}
	}
	
	@SpirePatch(cls = "downfall.rooms.HeartShopRoom", method = "showHeartMerchant", optional = true)
	public static class HeartShopRoomPatch {
		@SpirePostfixPatch
		public static void Postfix(Object __instance) {
			addDiscount(MISC.p().relics.stream().filter(r -> r instanceof Economist).peek(r -> {
				r.flash();
				r.beginLongPulse();
			}).map(r -> ((Economist) r).rate()).reduce(1f, (a, b) -> a * b));
		}
	}

	public void update() {
		super.update();
		if (!this.isObtained || !this.isActive)
			return;
		if (AbstractDungeon.currMapNode != null && !used && this.relicStream(TraineeEconomistUp.class).count() == 0) {
			if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
				if (!checkDFshop(AbstractDungeon.getCurrRoom())) {
					addDiscount(this.relicStream(TraineeEconomist.class).peek(r -> r.beginLongPulse())
							.map(r -> r.rate()).reduce(1f, (a, b) -> a * b));
					used = true;
				}
			}
		}
	}
	
	public void onEnterRoom(AbstractRoom r) {
		used = false;
		if (r instanceof ShopRoom) {
			if (!checkDFshop(r)) {
				this.flash();
				this.beginLongPulse();
			}
		} else {
			this.stopPulse();
		}
	}
	
	public static boolean checkDFshop(AbstractRoom r) {
		return "downfall.rooms.HeartShopRoom".equals(r.getClass().getName());
	}

	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}

}