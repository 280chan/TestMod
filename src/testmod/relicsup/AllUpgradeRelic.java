package testmod.relicsup;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.RecallOption;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect.KeyColor;

import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicupgradelib.arch.*;
import relicupgradelib.ui.RelicUpgradePopup;
import testmod.mymod.TestMod;
import testmod.relics.*;
import testmod.utils.MiscMethods;

public class AllUpgradeRelic implements MiscMethods {
	
	private static class Register {
		private static Proxy p;
		private static void set(AbstractTestRelic r, boolean red, boolean green, boolean blue,
				int gold) {
			p.addBranch(new UpgradeBranch(r, null, red, green, blue, gold));
			ProxyManager.register(p);
		}
		private static void init(AbstractTestRelic r) {
			p = new Proxy(r);
		}
	}
	
	public static void add(AbstractTestRelic r) {
		String original = r.getClass().getSimpleName();
		AbstractUpgradedRelic up = upgrade(original);
		if (up != null) {
			up.isSeen = true;
			Register.init(r);
			UIStrings tmp = CardCrawlGame.languagePack.getUIString(UIID(original));
			for (int i = 0; i < tmp.TEXT.length; i += 2) {
				int key = Integer.parseInt(tmp.TEXT[i]);
				int gold = Integer.parseInt(tmp.TEXT[i + 1]);
				// blue * 4 + green * 2 + red
				boolean red = key % 2 == 1;
				boolean green = (key % 4) / 2 == 1;
				boolean blue = key / 4 == 1;
				Register.set(up, red, green, blue, gold);
			}
		}
	}
	
	private static String UIID(String original) {
		return "testmod-upgrade-" + original;
	}
	
	private static String fullName(String original) {
		return "testmod.relicsup." + original + "Up";
	}
	
	public static AbstractUpgradedRelic upgrade(String original) {
		try {
			return (AbstractUpgradedRelic) Class.forName(fullName(original)).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static AbstractRelic getUpgrade(AbstractRelic r) {
		return ProxyManager.getProxyByRelic(r).braches.get(0).relic;
	}
	
	public static boolean canUpgrade(AbstractRelic r) {
		return ProxyManager.getProxyByRelic(r) != null;
	}
	
	public static class MultiKey {
		public static final String[] SAVE_NAME = { "MultiKey0", "MultiKey1", "MultiKey2" };
		public static final int[] KEY = { 0, 0, 0 };
		
		public static void reset() {
			for (int i = 0; i < 3; i++)
				TestMod.save(SAVE_NAME[i], KEY[i] = 0);
		}
		
		public static void load() {
			for (int i = 0; i < 3; i++)
				KEY[i] = TestMod.getInt(SAVE_NAME[i]);
		}
		
		public static void save() {
			for (int i = 0; i < 3; i++)
				TestMod.save(SAVE_NAME[i], KEY[i]);
		}
		
		@SpirePatch(clz = ObtainKeyEffect.class, method = "update")
		public static class ObtainKeyEffectPatch {
			@SpirePostfixPatch
			public static void Postfix(ObtainKeyEffect c) {
				if (c.isDone) {
					KeyColor k = ReflectionHacks.getPrivate(c, ObtainKeyEffect.class, "keyColor");
					if (k.ordinal() < 3)
						KEY[k.ordinal()]++;
					if (k.ordinal() == 1 && MISC.relicStream(AscensionHeartUp.class).count() > 0) {
						int d = AbstractDungeon.currMapNode.hasEmeraldKey ? 0 : -1;
						KEY[1] += MISC.relicStream(AscensionHeartUp.class).count() + d;
					}
					TestMod.info("获得了钥匙:" + (k.ordinal() == 0 ? "R" : (k.ordinal() == 1 ? "G" : "B")));
				}
			}
		}
		
		@SpirePatch(cls = "relicupgradelib.ui.RelicUpgradePopup", method = "replaceRelic", optional = true)
		public static class RelicUpgradePopupPatch {
			@SpireInsertPatch(locator = Locator.class)
			public static void Insert(RelicUpgradePopup rup) {
				Proxy p = ReflectionHacks.getPrivate(rup, RelicUpgradePopup.class, "currentProxy");
				int i = ReflectionHacks.getPrivate(rup, RelicUpgradePopup.class, "index");
				if (((UpgradeBranch) p.braches.get(i - 1)).gemred && KEY[0] > 0)
					Settings.hasRubyKey = --KEY[0] > 0;
				if (((UpgradeBranch) p.braches.get(i - 1)).gemgreen && KEY[1] > 0)
					Settings.hasEmeraldKey = --KEY[1] > 0;
				if (((UpgradeBranch) p.braches.get(i - 1)).gemblue && KEY[2] > 0)
					Settings.hasSapphireKey = --KEY[2] > 0;
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.MethodCallMatcher(SingleRelicViewPopup.class, "close");
					return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				}
			}
		}
		
		@SpirePatch(cls = "relicupgradelib.arch.EndingLogic$removeKeysPatches", method = "Insert", optional = true)
		public static class EndingLogicPatch {
			@SpirePostfixPatch
			public static void Postfix(DoorUnlockScreen _inst) {
				if (KEY[0] > 0)
					Settings.hasRubyKey = --KEY[0] > 0;
				if (KEY[1] > 0)
					Settings.hasEmeraldKey = --KEY[1] > 0;
				if (KEY[2] > 0)
					Settings.hasSapphireKey = --KEY[2] > 0;
			}
		}
		
		@SpirePatch(clz = SaveAndContinue.class, method = "save")
		public static class SaveAndContinuePatch {
			@SpirePostfixPatch
			public static void Postfix(SaveFile save) {
				save();
			}
		}
		
		@SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
		public static class KeepRecallOptionPatch {
			@SpirePostfixPatch
			public static void Postfix(CampfireUI ui) {
				if (!Loader.isModLoaded("RelicUpgradeLib"))
					return;
				ArrayList<AbstractCampfireOption> l = ReflectionHacks.getPrivate(ui, CampfireUI.class, "buttons");
				if (Settings.isFinalActAvailable && l.stream().noneMatch(o -> o instanceof RecallOption)) {
					l.add(new RecallOption());
					AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;
				}
			}
		}
		
		@SpirePatch(clz = AbstractChest.class, method = "open")
		public static class KeepSapphireKeyPatch {
			@SpireInsertPatch(locator = Locator.class)
			public static void Insert(AbstractChest ui, boolean bossChest) {
				if (Loader.isModLoaded("RelicUpgradeLib") && Settings.isFinalActAvailable && Settings.hasSapphireKey) {
					AbstractDungeon.getCurrRoom().addSapphireKey(AbstractDungeon.getCurrRoom().rewards
							.get(AbstractDungeon.getCurrRoom().rewards.size() - 1));
				}
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "hasSapphireKey");
					return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				}
			}
		}
		
		@SpirePatch(clz = TopPanel.class, method = "renderName")
		public static class RenderKeyPatch {
			private static final float x = 42.0F * Settings.scale;
			private static final float y = Settings.HEIGHT - 40.0F * Settings.scale;
			private static final float[] dx = { -15F, 0F, 15F };
			private static final float[] dy = { 0F, 20F, 0F };
			
			@SpireInsertPatch(locator = Locator.class)
			public static void Insert(TopPanel p, SpriteBatch sb) {
				if (Loader.isModLoaded("RelicUpgradeLib")) {
					for (int i = 0; i < 3; i++)
						if (KEY[i] > 1)
							FontHelper.renderSmartText(sb, FontHelper.powerAmountFont, KEY[i] + "",
									x + dx[i] * Settings.scale, y + dy[i] * Settings.scale, Settings.GOLD_COLOR);
				}
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.FieldAccessMatcher(TopPanel.class, "name");
					return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				}
			}
		}
	}
	
}
