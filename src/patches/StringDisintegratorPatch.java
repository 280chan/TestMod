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
import relics.StringDisintegrator;
import utils.MiscMethods.Lambda;

@SuppressWarnings("rawtypes")
public class StringDisintegratorPatch {

	private static boolean check() {
		return AbstractDungeon.floorNum > 0 && AbstractDungeon.currMapNode != null
				&& AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT
				&& AbstractDungeon.player.relics.stream().anyMatch(r -> r instanceof StringDisintegrator);
	}

	private static SpireReturn setReturn(Lambda act) {
		if (check() && act != null) act.run();
		return check() ? SpireReturn.Return(null) : SpireReturn.Continue();
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTitle")
	public static class RenderTitlePatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return setReturn(null);
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN")
	public static class RenderDescriptionCNPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return setReturn(null);
		}
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription")
	public static class RenderDescriptionPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return setReturn(null);
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
	public static class RenderTipsPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return setReturn(null);
		}
	}

	@SpirePatch(clz = CardGroup.class, method = "render")
	public static class RenderPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(CardGroup g, SpriteBatch sb) {
			return setReturn(() -> g.group.stream().forEach(c -> renderRewardCard(c, sb)));
		}
	}

	@SpirePatch(clz = CardGroup.class, method = "renderExceptOneCard")
	public static class renderExceptOneCardPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(CardGroup g, SpriteBatch sb, AbstractCard c) {
			return setReturn(() -> g.group.stream().filter(a -> !a.equals(c)).forEach(a -> renderRewardCard(a, sb)));
		}
	}
	
	@SpirePatch(clz = CardRewardScreen.class, method = "renderCardReward")
	public static class RenderCardRewardPatch {
		@SpireInsertPatch(rloc = 38)
		public static SpireReturn Insert(CardRewardScreen crs, SpriteBatch sb) {
			return setReturn(() -> crs.rewardGroup.stream().forEach(c -> renderRewardCard(c, sb)));
		}
	}
	
	private static void methodInvoke(String name, AbstractCard c, SpriteBatch sb) {
		ReflectionHacks.privateMethod(AbstractCard.class, name, SpriteBatch.class).invoke(c, sb);
	}
	
	private static void methodInvokeWithHovered(String name, AbstractCard c, SpriteBatch sb) {
		methodInvokeWithSelected(name, c, sb, ReflectionHacks.getPrivate(c, AbstractCard.class, "hovered"));
	}
	
	private static void methodInvokeWithSelected(String name, AbstractCard c, SpriteBatch sb, boolean selected) {
		ReflectionHacks.privateMethod(AbstractCard.class, name, SpriteBatch.class, boolean.class, boolean.class)
				.invoke(c, sb, selected, false);
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
				methodInvokeWithHovered("renderImage", c, sb);
				methodInvoke("renderType", c, sb);
				methodInvoke("renderTint", c, sb);
				methodInvoke("renderEnergy", c, sb);
			} else {
				methodInvokeWithHovered("renderBack", c, sb);
			}
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		c.hb.render(sb);
	}
	
	private static void renderRewardCard(AbstractCard c, SpriteBatch sb, boolean selected) {
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
				methodInvokeWithSelected("renderImage", c, sb, selected);
				methodInvoke("renderType", c, sb);
				methodInvoke("renderTint", c, sb);
				methodInvoke("renderEnergy", c, sb);
			} else {
				methodInvokeWithSelected("renderBack", c, sb, selected);
			}
		} catch (SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		c.hb.render(sb);
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "renderCard")
	public static class renderCardPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(AbstractCard c, SpriteBatch sb, boolean hovered, boolean selected) {
			return setReturn(() -> renderRewardCard(c, sb, selected));
		}
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "renderCardTip")
	public static class renderCardTipPatch {
		@SpireInsertPatch(rloc = 0)
		public static SpireReturn Insert(AbstractCard c, SpriteBatch sb) {
			return setReturn(null);
		}
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
