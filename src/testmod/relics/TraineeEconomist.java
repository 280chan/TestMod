package testmod.relics;

import java.util.stream.Stream;

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

public class TraineeEconomist extends AbstractTestRelic {
	private static final int DELTA_BONUS = 5;
	private static final int DELTA_PRICE = 5;
	private static final double PERCENTAGE = 100.0;
	private boolean used = false;
	
	public TraineeEconomist() {
		super(RelicTier.RARE, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return (this.counter < 1) ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2] + toPercent(this.priceRate())
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
	
	private float priceRate() {
		return priceRate(this.counter);
	}
	
	private static float priceRate(int counter) {
		return (float) Math.pow((100 - DELTA_PRICE) / PERCENTAGE, counter);
	}
	
	private static float totalRate() {
		int c = INSTANCE.relicStream(TraineeEconomist.class).mapToInt(a -> a.counter).sum();
		return c == 0 ? 1f : priceRate(c);
	}
	
	public void onEquip() {
		this.counter = 0;
    }
	
	public void onMonsterDeath(AbstractMonster m) {
		this.counter += m.type.ordinal() + 1;
		this.updateDescription(p().chosenClass);
    }
	
	public double gainGold(double amount) {
		this.flash();
		return amount * gainGoldRate();
	}
	
	private void addDiscount() {
		AbstractDungeon.shopScreen.applyDiscount(this.priceRate(), true);
	}
	
	public void onPreviewObtainCard(AbstractCard c) {
		if (Stream.of(new Exception().getStackTrace()).peek(e -> this.print(e.getClassName())).noneMatch(
				e -> ShopScreen.class.getName().equals(e.getClassName()) && "initCards".equals(e.getMethodName())))
			c.price = MathUtils.round(c.price * totalRate());
	}
	
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
			if (INSTANCE.relicStream(TraineeEconomist.class).count() > 0) {
				AbstractDungeon.shopScreen.applyDiscount(totalRate(), true);
				INSTANCE.relicStream(TraineeEconomist.class).forEach(r -> {
					r.flash();
					r.beginLongPulse();
					r.used = true;
				});
			}
		}
	}

	public void update() {
		super.update();
		if (!this.isObtained)
			return;
		if (AbstractDungeon.currMapNode != null && !this.used) {
			if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
				if (!checkDFshop(AbstractDungeon.getCurrRoom())) {
					this.beginLongPulse();
					this.addDiscount();
					this.used = true;
				}
			}
		}
	}
	
	public void onEnterRoom(AbstractRoom r) {
		this.used = false;
		if (r instanceof ShopRoom) {
			if (!checkDFshop(r)) {
				this.flash();
			}
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}
	
	private static boolean checkDFshop(AbstractRoom r) {
		return "downfall.rooms.HeartShopRoom".equals(r.getClass().getName());
	}

	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}

}