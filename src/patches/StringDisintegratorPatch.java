package patches;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import mymod.TestMod;
import relics.StringDisintegrator;
import relics.TestBox;

@SuppressWarnings("rawtypes")
public class StringDisintegratorPatch {

	private static boolean check() {
		String id = TestMod.isLocalTest() ? TestMod.makeID(TestBox.ID) : TestMod.makeID(StringDisintegrator.ID);
		return AbstractDungeon.floorNum > 0 && AbstractDungeon.currMapNode != null
				&& AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT && AbstractDungeon.player.hasRelic(id);
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTitle")
	public static class RenderTitlePatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			if (check()) {
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN")
	public static class RenderDescriptionCNPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			if (check()) {
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription")
	public static class RenderDescriptionPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			if (check()) {
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
	public static class RenderTipsPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			if (check()) {
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = CardGroup.class, method = "render")
	public static class RenderPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(CardGroup g, SpriteBatch sb) {
			if (check()) {
				for (AbstractCard c : g.group)
					renderRewardCard(c, sb);
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = CardGroup.class, method = "renderExceptOneCard")
	public static class renderExceptOneCardPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(CardGroup g, SpriteBatch sb, AbstractCard card) {
			if (check()) {
				for (AbstractCard c : g.group)
					if (!c.equals(card))
						renderRewardCard(c, sb);
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}
	
	@SpirePatch(clz = CardRewardScreen.class, method = "renderCardReward")
	public static class RenderCardRewardPatch {
		@SpireInsertPatch(rloc = 38)
		public static SpireReturn Insert(CardRewardScreen crs, SpriteBatch sb) {
			if (check()) {
				for (AbstractCard c : crs.rewardGroup) {
					renderRewardCard(c, sb);
				}
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}
	
	private static void renderRewardCard(AbstractCard c, SpriteBatch sb) {
		if (Settings.hideCards)
			return;
		if (c.flashVfx != null)
			c.flashVfx.render(sb);
		
		boolean isOnScreen = c.current_y >= -200.0F * Settings.scale
				&& c.current_y <= (float) Settings.HEIGHT + 200.0F * Settings.scale;
		if (!isOnScreen) {
			return;
		}
		Method tmp;
		Field f;
		try {
			if (!c.isFlipped) {
				tmp = AbstractCard.class.getDeclaredMethod("updateGlow");
				tmp.setAccessible(true);
				tmp.invoke(c);
				tmp.setAccessible(false);
				
				tmp = AbstractCard.class.getDeclaredMethod("renderGlow", SpriteBatch.class);
				tmp.setAccessible(true);
				tmp.invoke(c, sb);
				tmp.setAccessible(false);
				
				f = AbstractCard.class.getDeclaredField("hovered");
				f.setAccessible(true);
				tmp = AbstractCard.class.getDeclaredMethod("renderImage", SpriteBatch.class, boolean.class, boolean.class);
				tmp.setAccessible(true);
				tmp.invoke(c, sb, f.getBoolean(c), false);
				tmp.setAccessible(false);
				f.setAccessible(false);

				tmp = AbstractCard.class.getDeclaredMethod("renderType", SpriteBatch.class);
				tmp.setAccessible(true);
				tmp.invoke(c, sb);
				tmp.setAccessible(false);

				tmp = AbstractCard.class.getDeclaredMethod("renderTint", SpriteBatch.class);
				tmp.setAccessible(true);
				tmp.invoke(c, sb);
				tmp.setAccessible(false);

				tmp = AbstractCard.class.getDeclaredMethod("renderEnergy", SpriteBatch.class);
				tmp.setAccessible(true);
				tmp.invoke(c, sb);
				tmp.setAccessible(false);
			} else {
				f = AbstractCard.class.getDeclaredField("hovered");
				f.setAccessible(true);
				tmp = AbstractCard.class.getDeclaredMethod("renderBack", SpriteBatch.class, Boolean.class, Boolean.class);
				tmp.setAccessible(true);
				tmp.invoke(c, sb, f.getBoolean(c), false);
				tmp.setAccessible(false);
				f.setAccessible(false);
				c.hb.render(sb);
			}
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		c.hb.render(sb);
	}
	
}
