package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
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
import testmod.utils.CounterKeeper;
import testmod.utils.InfiniteUpgradeRelic;
import testmod.utils.MiscMethods;

public class AllUpgradeRelic implements MiscMethods {
	
	private static class Register {
		private static Proxy p;
		private static void set(AbstractTestRelic r, boolean red, boolean green, boolean blue,
				int gold) {
			set(r, red, green, blue, gold, null);
		}
		private static void set(AbstractTestRelic r, boolean red, boolean green, boolean blue,
				int gold, CounterKeeper f) {
			UpgradeBranch b = new UpgradeBranch(r, null, red, green, blue, gold);
			if (f != null)
				b.addFuncAfterObtained((a, u) -> f.run(a, (AbstractUpgradedRelic) u));
			p.addBranch(b);
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
			TestMod.UP_RELICS.add(up);
			up = (AbstractUpgradedRelic) up.makeCopy();
			Register.init(r);
			UIStrings tmp = CardCrawlGame.languagePack.getUIString(UIID(original));
			for (int i = 0; i < tmp.TEXT.length; i += 2) {
				int key = Integer.parseInt(tmp.TEXT[i]);
				int gold = Integer.parseInt(tmp.TEXT[i + 1]);
				// blue * 4 + green * 2 + red
				boolean red = key % 2 == 1;
				boolean green = (key % 4) / 2 == 1;
				boolean blue = key / 4 == 1;
				if (up instanceof CounterKeeper && up.tier != RelicTier.BOSS) {
					Register.set(up, red, green, blue, gold, (CounterKeeper) up);
				} else {
					Register.set(up, red, green, blue, gold);
				}
			}
			if (up instanceof InfiniteUpgradeRelic) {
				Register.init(up);
				for (int i = 0; i < tmp.TEXT.length; i += 2) {
					int key = Integer.parseInt(tmp.TEXT[i]);
					int gold = Integer.parseInt(tmp.TEXT[i + 1]);
					boolean red = key % 2 == 1;
					boolean green = (key % 4) / 2 == 1;
					boolean blue = key / 4 == 1;
					if (up instanceof CounterKeeper && up.tier != RelicTier.BOSS) {
						Register.set(up, red, green, blue, gold, (CounterKeeper) up);
					} else {
						Register.set(up, red, green, blue, gold);
					}
				}
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
	
	public static boolean upgradable(AbstractRelic r) {
		return ProxyManager.getProxyByRelic(r) != null;
	}
	
	public static boolean isUpgraded(AbstractRelic r) {
		return ProxyManager.upgradeRelics.containsKey(r.relicId);
	}
	
	public static BiConsumer<AbstractRelic, AbstractRelic> getUpgradeFunction(AbstractRelic r) {
		return ProxyManager.getProxyByRelic(r).braches.get(0).obtainedFunc;
	}
	
	public static int keyCount() {
		return IntStream.of(MultiKey.KEY).sum();
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
					long rate = MISC.relicStream(HarvestTotemUp.class).count() + 1;
					if (k.ordinal() < 3)
						KEY[k.ordinal()] += rate;
					if (k.ordinal() == 1 && MISC.relicStream(AscensionHeartUp.class).count() > 0) {
						int d = AbstractDungeon.currMapNode.hasEmeraldKey ? 0 : -1;
						KEY[1] += rate * (MISC.relicStream(AscensionHeartUp.class).count() + d);
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
				if (p.braches.get(i - 1).gemred && KEY[0] > 0)
					Settings.hasRubyKey = --KEY[0] > 0;
				if (p.braches.get(i - 1).gemgreen && KEY[1] > 0)
					Settings.hasEmeraldKey = --KEY[1] > 0;
				if (p.braches.get(i - 1).gemblue && KEY[2] > 0)
					Settings.hasSapphireKey = --KEY[2] > 0;
				RelicTier t = p.braches.get(i - 1).relic.tier;
				if (t != RelicTier.BOSS)
					MISC.relicStream(ResonanceStoneUp.class).forEach(r -> r.upgrade(t));
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.MethodCallMatcher(SingleRelicViewPopup.class, "close");
					return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
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
		
		private static boolean valid(boolean red) {
			return (Settings.isFinalActAvailable && (red || Settings.hasSapphireKey))
					|| (!Settings.isFinalActAvailable && Settings.isEndless);
		}
		
		@SuppressWarnings("rawtypes")
		private static boolean checkCoop() {
			try {
				Object[] par = { "Coop" };
				Class c = Class.forName("chronoMods.TogetherManager.enum");
				Enum a = ReflectionHacks.privateStaticMethod(c, "valueOf", String.class).invoke(par);
				Enum b = ReflectionHacks.getPrivateStatic(Class.forName("chronoMods.TogetherManager"), "gameMode");
				return a == b;
			} catch (ClassNotFoundException e) {
				return false;
			}
		}
		
		@SuppressWarnings("rawtypes")
		private static boolean swfKey(int type) {
			if (Loader.isModLoaded("chronoMods") && checkCoop()) {
				try {
					Class c = Class.forName("chronoMods.coop.CoopKeySharing");
					switch (type) {
					case 0: return ReflectionHacks.privateStaticMethod(c, "redKeyNeeded").invoke();
					case 1: return ReflectionHacks.privateStaticMethod(c, "greenKeyNeeded").invoke();
					case 2: return ReflectionHacks.privateStaticMethod(c, "blueKeyNeeded").invoke();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		
		@SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
		public static class KeepRecallOptionPatch {
			@SpirePostfixPatch
			public static void Postfix(CampfireUI ui) {
				if (swfKey(0) || !Loader.isModLoaded("RelicUpgradeLib") || MISC.p().hasRelic("RU Singing Bowl"))
					return;
				ArrayList<AbstractCampfireOption> l = ReflectionHacks.getPrivate(ui, CampfireUI.class, "buttons");
				if (valid(true) && l.stream().noneMatch(o -> o instanceof RecallOption)) {
					l.add(new RecallOption());
					AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;
				}
			}
		}
		
		public static boolean sapphireAdded = false;
		public static boolean mySapphireAdded = false;
		
		@SpirePatch(clz = AbstractChest.class, method = "open")
		public static class KeepSapphireKeyPatch {
			@SpireInsertPatch(locator = Locator.class)
			public static void Insert(AbstractChest ui, boolean bossChest) {
				if ((sapphireAdded && !(sapphireAdded = false)) || !Loader.isModLoaded("RelicUpgradeLib")
						|| MISC.p().hasRelic("RU Singing Bowl"))
					return;
				if (valid(false)) {
					mySapphireAdded = true;
					AbstractDungeon.getCurrRoom().addSapphireKey(AbstractDungeon.getCurrRoom().rewards
							.get(AbstractDungeon.getCurrRoom().rewards.size() - 1));
				}
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "hasSapphireKey");
					return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
				}
			}
		}

		@SpirePatch(cls = "chronoMods.coop.CoopKeySharing$enableBlueKeyChest", method = "Insert", optional = true)
		public static class StupidSwfSapphireKeyPatch {
			@SpirePrefixPatch
			public static SpireReturn<Void> Prefix(AbstractChest r, boolean bossChest) {
				return mySapphireAdded && !(mySapphireAdded = false) ? SpireReturn.Return() : SpireReturn.Continue();
			}
			
			@SpireInsertPatch(locator = Locator.class)
			public static void Insert(AbstractChest r, boolean bossChest) {
				sapphireAdded = true;
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRoom.class, "addSapphireKey");
					return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
				}
			}
		}

		@SpirePatch(cls = "chronoMods.coop.CoopKeySharing$updateGreenKeyReward", method = "Insert", optional = true)
		public static class StupidSwfEmeraldKeyClaimPatch {
			@SpireInsertPatch(locator = Locator.class)
			public static SpireReturn<SpireReturn<Boolean>> Insert(RewardItem r) {
				return Loader.isModLoaded("RelicUpgradeLib") ? SpireReturn.Return(SpireReturn.Continue())
						: SpireReturn.Continue();
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.MethodCallMatcher(Texture.class, "dispose");
					return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
				}
			}
		}
		
		private static boolean checkEmerald() {
			return Settings.isFinalActAvailable && Loader.isModLoaded("RelicUpgradeLib") && Settings.hasEmeraldKey
					&& !swfKey(1) && !MISC.p().hasRelic("RU Singing Bowl");
		}

		@SpirePatch(clz = AbstractDungeon.class, method = "setEmeraldElite")
		public static class KeepEmeraldKeyPatch {
			@SpirePostfixPatch
			public static void Postfix() {
				if (Settings.isFinalActAvailable && Loader.isModLoaded("RelicUpgradeLib") && !swfKey(1)) {
					ArrayList<MapRoomNode> nodes = AbstractDungeon.map.stream().flatMap(r -> r.stream())
							.filter(n -> n.room instanceof MonsterRoomElite).collect(MISC.toArrayList());
					int had = (int) nodes.stream().filter(n -> n.hasEmeraldKey).count();
					Collections.shuffle(nodes, new Random(AbstractDungeon.mapRng.copy().randomLong()));
					int num = Math.min(3, AbstractDungeon.actNum - had);
					nodes.stream().filter(n -> !n.hasEmeraldKey).limit(Math.max(0, num))
							.forEach(n -> n.hasEmeraldKey = true);
					nodes.clear();
				}
			}
		}

		@SpirePatch(clz = MonsterRoomElite.class, method = "addEmeraldKey")
		public static class KeepEmeraldKeyPatch2 {
			@SpirePostfixPatch
			public static void PostFix(MonsterRoomElite r) {
				if (checkEmerald() && !r.rewards.isEmpty() && AbstractDungeon.getCurrMapNode().hasEmeraldKey)
					r.rewards.add(new RewardItem(r.rewards.get(r.rewards.size() - 1), RewardType.EMERALD_KEY));
			}
		}
		
		@SpirePatch(clz = TopPanel.class, method = "renderName")
		public static class RenderKeyPatch {
			private static final float x = 42.0F * Settings.scale;
			private static final float y = Settings.HEIGHT - 40.0F * Settings.scale;
			private static final float[] dx = { -15F, 0F, 15F };
			private static final float[] dy = { 0F, 20F, 0F };
			private static final float ICON_Y = ReflectionHacks.getPrivateStatic(TopPanel.class, "ICON_Y");
			
			@SpireInsertPatch(locator = Locator.class)
			public static void Insert(TopPanel p, SpriteBatch sb) {
				if (Loader.isModLoaded("RelicUpgradeLib")) {
					Settings.hasRubyKey = KEY[0] > 0;
					Settings.hasEmeraldKey = KEY[1] > 0;
					Settings.hasSapphireKey = KEY[2] > 0;
					if (Settings.isEndless) {
						if (Settings.hasRubyKey || Settings.hasEmeraldKey || Settings.hasSapphireKey)
							draw(0, sb);
						if (Settings.hasRubyKey)
							draw(1, sb);
						if (Settings.hasEmeraldKey)
							draw(2, sb);
						if (Settings.hasSapphireKey)
							draw(3, sb);
					}
					for (int i = 0; i < 3; i++)
						drawAmount(i, sb);
				}
			}
			
			private static void drawAmount(int i, SpriteBatch sb) {
				if (KEY[i] > 1)
					FontHelper.renderSmartText(sb, FontHelper.powerAmountFont, KEY[i] + "", x + dx[i] * Settings.scale,
							y + dy[i] * Settings.scale, Settings.GOLD_COLOR);
			}
			
			private static Texture mode(int mode) {
				switch (mode) {
				case 0:
					return ImageMaster.KEY_SLOTS_ICON;
				case 1:
					return ImageMaster.RUBY_KEY;
				case 2:
					return ImageMaster.EMERALD_KEY;
				}
				return ImageMaster.SAPPHIRE_KEY;
			}
			
			private static void draw(int mode, SpriteBatch sb) {
				sb.draw(mode(mode), -32.0F + 46.0F * Settings.scale, ICON_Y - 32.0F + 29.0F * Settings.scale, 32.0F,
						32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
			}
			
			private static class Locator extends SpireInsertLocator {
				public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
					Matcher finalMatcher = new Matcher.FieldAccessMatcher(TopPanel.class, "name");
					return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
				}
			}
		}
	}
	
}
