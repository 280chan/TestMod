package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;
import com.megacrit.cardcrawl.vfx.MapDot;

import basemod.ReflectionHacks;
import testmod.mymod.TestMod;

public class PortablePortalUp extends AbstractUpgradedRelic implements ClickableRelic {
	public static final String SAVE_NAME = "PortablePortalUp";
	public static ArrayList<String> DUNGEON_ID = new ArrayList<String>();
	
	public static void clear() {
		DUNGEON_ID.clear();
		TestMod.saveString(SAVE_NAME, DUNGEON_ID);
	}
	
	public static void load() {
		DUNGEON_ID.clear();
		DUNGEON_ID.addAll(TestMod.getStringList(SAVE_NAME));
	}
	
	public static void next() {
		DUNGEON_ID.add(0, AbstractDungeon.id);
		TestMod.saveString(SAVE_NAME, DUNGEON_ID);
		MISC.print(DUNGEON_ID);
	}
	
	public void onEquip() {
		this.addEnergy();
		if (Loader.isModLoaded("RelicUpgradeLib")) {
			int k;
			if ((k = AllUpgradeRelic.keyCount()) > 2) {
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				for (int i = 0; i < 3; i++)
					tmp.addAll(this.getIdenticalList(i, AllUpgradeRelic.MultiKey.KEY[i]));
				Collections.shuffle(tmp, new Random(AbstractDungeon.miscRng.copy().randomLong()));
				tmp.stream().limit(3).forEach(i -> AllUpgradeRelic.MultiKey.KEY[i]--);
			} else {
				double t;
				if ((t = p().gold * 1.0 * (3 - k) / 3) >= 1) {
					p().loseGold((int) Math.min(t, 999));
				}
				for (int i = 0; i < 3; i++)
					AllUpgradeRelic.MultiKey.KEY[i] = 0;
				Settings.hasRubyKey = Settings.hasEmeraldKey = Settings.hasSapphireKey = false;
			}
		} else {
			int k = 0;
			for (boolean b : new boolean[] { Settings.hasRubyKey, Settings.hasEmeraldKey, Settings.hasSapphireKey })
				k += b ? 1 : 0;
			double t;
			if ((t = p().gold * 1.0 * (3 - k) / 3) >= 1) {
				p().loseGold((int) Math.min(t, 999));
			}
			Settings.hasRubyKey = Settings.hasEmeraldKey = Settings.hasSapphireKey = false;
		}
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
	public void atPreBattle() {
		this.stopPulse();
	}
	
	public void onVictory() {
		if (DUNGEON_ID.size() > 1)
			this.beginLongPulse();
	}
	
	private static void completeRoom() {
		AbstractRoom r = AbstractDungeon.currMapNode == null ? null: AbstractDungeon.getCurrRoom();
		if (r != null)
			r.phase = AbstractRoom.RoomPhase.COMPLETE;
	}

	@Override
	public void onRightClick() {
		if (!p().isDead && !this.inCombat() && DUNGEON_ID.size() > 1) {
			AbstractDungeon.resetPlayer();
			p().movePosition(Settings.WIDTH * 0.25F, AbstractDungeon.floorY);
			String pre = DUNGEON_ID.remove(0);
			CardCrawlGame.nextDungeon = DUNGEON_ID.get(0);
			AbstractDungeon.isDungeonBeaten = PendingBossUpgradePatch.using = true;
			CardCrawlGame.music.fadeOutBGM();
			CardCrawlGame.music.fadeOutTempBGM();
			AbstractDungeon.fadeOut();
			AbstractDungeon.topLevelEffects.clear();
			AbstractDungeon.actionManager.actions.clear();
			AbstractDungeon.effectList.clear();
			AbstractDungeon.effectsQueue.clear();
			completeRoom();
			AbstractDungeon.id = DUNGEON_ID.remove(0);
			
			SkipCoopBossBlightPatch.remain++;
			SkipCoopKeyLockPatch.skip |= (SkipCoopKeyLockPatch.canProceed = TheEnding.ID.equals(pre));

			this.addTmpEffect(() -> this.addTmpEffect(() -> {
				this.show();
				completeRoom();
				AbstractDungeon.nextRoom = new MapRoomNode(-1, 15);
				AbstractDungeon.nextRoom.room = new MonsterRoomBoss();
				CardCrawlGame.music.fadeOutTempBGM();
				AbstractDungeon.pathX.add(Integer.valueOf(1));
				AbstractDungeon.pathY.add(Integer.valueOf(15));
				AbstractDungeon.topLevelEffectsQueue.add(new FadeWipeParticle());
				AbstractDungeon.nextRoomTransitionStart();
			}));
			
			if (Loader.isModLoaded("actlikeit")) {
				try {
					Class<?> cls = Class.forName("actlikeit.savefields.BehindTheScenesActNum");
					int act = ReflectionHacks.privateStaticMethod(cls, "getActNum").invoke();
					ReflectionHacks.privateStaticMethod(cls, "setActNum", int.class).invoke(new Object[] { act - 2 });
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void resetCoop() {
		SkipCoopBossBlightPatch.remain = 0;
		SkipCoopKeyLockPatch.skip = false;
	}

	@SpirePatch(clz = TheBeyond.class, method = "<ctor>", paramtypez = { AbstractPlayer.class, ArrayList.class })
	public static class PortablePortalUpPatch {
		public static void Postfix(TheBeyond i, AbstractPlayer p, ArrayList<String> theList) {
			AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
			AbstractDungeon.currMapNode.room = new EmptyRoom();
		}
	}

	@SpirePatch(cls = "relicupgradelib.RelicUpgradeLib", method = "receiveStartAct", optional = true)
	public static class PendingBossUpgradePatch {
		public static boolean using = false;
		
		public static SpireReturn<Void> Prefix(Object i) {
			return using && !(using = false) ? SpireReturn.Return() : SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "chronoMods.coop.ProceedButtonPatch$ProceedButtonShouldNotProceed", method = "Prefix",
			optional = true)
	public static class SkipCoopBossBlightPatch {
		public static int remain = 0;
		
		public static SpireReturn<SpireReturn<Void>> Prefix(ProceedButton b, AbstractRoom r) {
			return remain > 0 && remain-- > -1 ? SpireReturn.Return(SpireReturn.Continue()) : SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "chronoMods.coop.ProceedButtonPatch$ProceedButtonShouldNotShow", method = "Insert",
			optional = true)
	public static class SkipCoopBossBlightPatch1 {
		public static SpireReturn<Void> Prefix(AbstractRelic r) {
			return SkipCoopBossBlightPatch.remain > 0 ? SpireReturn.Return() : SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "chronoMods.coop.relics.CoopBossChest$InsertNextChestIntoBossRoom", method = "Insert",
			optional = true)
	public static class SkipCoopBossBlightPatch2 {
		public static SpireReturn<Void> Prefix(BossRelicSelectScreen s) {
			return SkipCoopBossBlightPatch.remain > 0 ? SpireReturn.Return() : SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "chronoMods.coop.CoopDoorUnlockScreen", method = "checkForOpen", optional = true)
	public static class SkipCoopKeyLockPatch {
		public static boolean skip = false;
		public static boolean canProceed = true;
		
		public static SpireReturn<Void> Prefix(Object s) {
			if (skip) {
				try {
					Class<?> c = Class.forName("chronoMods.coop.CoopDoorUnlockScreen");
					ReflectionHacks.setPrivate(s, c, "open", true);
					if (canProceed && !(canProceed = false))
						ReflectionHacks.privateMethod(c, "proceed").invoke(s);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return SpireReturn.Return();
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "chronoMods.coop.CoopDoorUnlockScreen", method = "proceed", optional = true)
	public static class SkipCoopKeyLockPatch1 {
		public static SpireReturn<Void> Prefix(Object s) {
			if (!SkipCoopKeyLockPatch.skip)
				return SpireReturn.Continue();
			try {
				Class<?> c = Class.forName("chronoMods.coop.CoopDoorUnlockScreen");
				ReflectionHacks.setPrivate(s, c, "animateCircle", true);
				ReflectionHacks.privateMethod(c, "unlock").invoke(s);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return SpireReturn.Return();
		}
	}

	@SpirePatch(cls = "chronoMods.coop.drawable.MapCanvasPatches$MapCanvasUpdate", method = "Prefix", optional = true)
	public static class CoopCanvasPatch {
		public static int actNum = 0;
		
		public static void Prefix(DungeonMapScreen s) {
			actNum = AbstractDungeon.actNum;
			AbstractDungeon.actNum = DUNGEON_ID.size();
		}
		
		public static void Postfix(DungeonMapScreen s) {
			AbstractDungeon.actNum = actNum;
		}
	}

	@SpirePatch(cls = "chronoMods.coop.drawable.MapCanvasPatches$MapCanvasRender", method = "Postfix", optional = true)
	public static class CoopCanvasPatch1 {
		public static int actNum = 0;
		
		public static void Prefix(DungeonMap m, SpriteBatch sb, Color c) {
			actNum = AbstractDungeon.actNum;
			AbstractDungeon.actNum = DUNGEON_ID.size();
		}
		
		public static void Postfix(DungeonMap m, SpriteBatch sb, Color c) {
			AbstractDungeon.actNum = actNum;
		}
	}

	@SpirePatch(cls = "chronoMods.ui.hud.MapPlayerPatch$renderPlayerPositionsOnMap", method = "Prefix", optional = true)
	public static class CoopMapPlayerPatch {
		public static int actNum = 0;
		
		public static void Prefix(MapRoomNode node, SpriteBatch sb, float scale, float angle) {
			actNum = AbstractDungeon.actNum;
			AbstractDungeon.actNum = DUNGEON_ID.size();
		}
		
		public static void Postfix(MapRoomNode node, SpriteBatch sb, float scale, float angle) {
			AbstractDungeon.actNum = actNum;
		}
	}

	@SpirePatch(cls = "chronoMods.ui.hud.MapPlayerPatch$renderPlayerPathsOnMap", method = "Prefix", optional = true)
	public static class CoopMapPlayerPatch1 {
		public static int actNum = 0;
		
		public static void Prefix(MapEdge edge, SpriteBatch sb, ArrayList<MapDot> dots) {
			actNum = AbstractDungeon.actNum;
			AbstractDungeon.actNum = DUNGEON_ID.size();
		}
		
		public static void Postfix(MapEdge edge, SpriteBatch sb, ArrayList<MapDot> dots) {
			AbstractDungeon.actNum = actNum;
		}
	}

}