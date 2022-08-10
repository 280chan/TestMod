package testmod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import basemod.ReflectionHacks;
import testmod.mymod.TestMod;
import testmod.relics.StringDisintegrator;
import testmod.relicsup.StringDisintegratorUp;
import testmod.utils.MiscMethods;

@SuppressWarnings("rawtypes")
public class StringDisintegratorPatch implements MiscMethods {

	private static AbstractCard card(SingleCardViewPopup scvp) {
		return ReflectionHacks.getPrivate(scvp, SingleCardViewPopup.class, "card");
	}
	
	private static SpireReturn checkUp(AbstractCard c, SpireReturn ret) {
		return !hasUp() || StringDisintegratorUp.CARDS.contains(c) ? ret : SpireReturn.Continue();
	}
	
	private static SpireReturn costType(SpireReturn ret) {
		return hasUp() ? SpireReturn.Continue() : ret;
	}
	
	private static boolean hasUp() {
		return MISC.p() != null && MISC.p().relics.stream().anyMatch(r -> r instanceof StringDisintegratorUp);
	}
	
	private static boolean checkRelic() {
		return hasUp() || MISC.p().relics.stream().anyMatch(r -> r instanceof StringDisintegrator);
	}
	
	private static boolean check() {
		return MISC.inCombat() && checkRelic();
	}
	
	private static SpireReturn setReturn(Object _return, Lambda act) {
		if (check() && act != null) act.run();
		return check() ? SpireReturn.Return(_return) : SpireReturn.Continue();
	}

	private static SpireReturn setReturn(Lambda act) {
		return setReturn(null, act);
	}
	
	private static SpireReturn setReturn() {
		return setReturn(null, null);
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderCost")
	public static class RenderCostPatch {
		@SpireInsertPatch(rloc = 32)
		public static SpireReturn Insert(SingleCardViewPopup scvp, SpriteBatch sb) {
			return costType(setReturn());
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardTypeText")
	public static class RenderTypePatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(SingleCardViewPopup scvp, SpriteBatch sb) {
			return costType(setReturn());
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTitle")
	public static class RenderTitlePatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(SingleCardViewPopup scvp, SpriteBatch sb) {
			return checkUp(card(scvp), setReturn());
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN")
	public static class RenderDescriptionCNPatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(SingleCardViewPopup scvp, SpriteBatch sb) {
			return checkUp(card(scvp), setReturn());
		}
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription")
	public static class RenderDescriptionPatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(SingleCardViewPopup scvp, SpriteBatch sb) {
			return checkUp(card(scvp), setReturn());
		}
	}
	
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
	public static class RenderTipsPatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(SingleCardViewPopup scvp, SpriteBatch sb) {
			return checkUp(card(scvp), setReturn());
		}
	}
	
	private static void methodInvoke(String name, AbstractCard c, SpriteBatch sb) {
		ReflectionHacks.privateMethod(AbstractCard.class, name, SpriteBatch.class).invoke(c, sb);
	}
	
	private static void methodInvokeWithSelected(String name, AbstractCard c, SpriteBatch sb, boolean selected) {
		ReflectionHacks.privateMethod(AbstractCard.class, name, SpriteBatch.class, boolean.class, boolean.class)
				.invoke(c, sb, selected, false);
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
				if (hasUp())
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
	
	@SpirePatch(clz = AbstractCard.class, method = "getCost")
	public static class RenderEnergyPatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(AbstractCard c) {
			return costType(setReturn("", null));
		}
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "renderCard")
	public static class renderCardPatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(AbstractCard c, SpriteBatch sb, boolean hovered, boolean selected) {
			return checkUp(c, setReturn(() -> renderRewardCard(c, sb, selected)));
		}
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "renderCardTip")
	public static class renderCardTipPatch {
		@SpirePrefixPatch
		public static SpireReturn Prefix(AbstractCard c, SpriteBatch sb) {
			return checkUp(c, setReturn());
		}
	}

	@SpirePatch(clz = FontHelper.class, method = "ClearSCPFontTextures")
	public static class FontHelperPatch {
		@SpireInsertPatch(rloc = 3)
		public static void Insert() {
			if (FontHelper.SCP_cardTitleFont_small == null) {
				FontHelper.SCP_cardTitleFont_small = FontHelper.prepFont(46.0F, true);
				if (FontHelper.SCP_cardTitleFont_small == null) {
					TestMod.info("确认SCP_cardTitleFont_small为null，开始重新构建...仍旧为null？？？");
				}
			}
		}
	}
	
}
