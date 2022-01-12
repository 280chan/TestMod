package patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import basemod.ReflectionHacks;
import mymod.TestMod;

@SuppressWarnings("rawtypes")
public class StringDisintegratorPatch {

	private static boolean check() {
		String id = TestMod.isLocalTest() ? TestMod.makeID("TestBox") : TestMod.makeID("StringDisintegrator");
		return AbstractDungeon.floorNum > 0 && AbstractDungeon.currMapNode != null
				&& AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT && AbstractDungeon.player.hasRelic(id);
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTitle")
	public static class RenderTitlePatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return check() ? SpireReturn.Return(null) : SpireReturn.Continue();
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN")
	public static class RenderDescriptionCNPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return check() ? SpireReturn.Return(null) : SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription")
	public static class RenderDescriptionPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return check() ? SpireReturn.Return(null) : SpireReturn.Continue();
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
	public static class RenderTipsPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return check() ? SpireReturn.Return(null) : SpireReturn.Continue();
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
	
	private static void methodInvoke(String name, AbstractCard c, SpriteBatch sb) {
		ReflectionHacks.privateMethod(AbstractCard.class, name, SpriteBatch.class).invoke(c, sb);
	}
	
	private static void methodInvokeWithBoolean(String name, AbstractCard c, SpriteBatch sb) {
		boolean hovered = ReflectionHacks.getPrivate(c, AbstractCard.class, "hovered");
		ReflectionHacks.privateMethod(AbstractCard.class, name, SpriteBatch.class, boolean.class, boolean.class)
				.invoke(c, sb, hovered, false);
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
		try {
			if (!c.isFlipped) {
				ReflectionHacks.privateMethod(AbstractCard.class, "updateGlow").invoke(c);
				methodInvoke("renderGlow", c, sb);
				methodInvokeWithBoolean("renderImage", c, sb);
				methodInvoke("renderType", c, sb);
				methodInvoke("renderTint", c, sb);
				methodInvoke("renderEnergy", c, sb);
			} else {
				methodInvokeWithBoolean("renderBack", c, sb);
			}
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		c.hb.render(sb);
	}
	

	@SpirePatch(clz = FontHelper.class, method = "ClearSCPFontTextures")
	public static class FontHelperPatch {
		@SpireInsertPatch(rloc = 3)
		public static void Insert() {
			if (FontHelper.SCP_cardTitleFont_small == null) {
				TestMod.info("确认SCP_cardTitleFont_small为null，开始重新构建");
				FontHelper.SCP_cardTitleFont_small = FontHelper.prepFont(46.0F, true);
				if (FontHelper.SCP_cardTitleFont_small == null) {
					TestMod.info("仍旧为null？？？");
				} else {
					TestMod.info("构建完毕");
				}
			}
		}
	}
	
}
