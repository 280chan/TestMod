package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

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
			DUNGEON_ID.remove(0);
			CardCrawlGame.nextDungeon = DUNGEON_ID.get(0);
			AbstractDungeon.isDungeonBeaten = true;
			CardCrawlGame.music.fadeOutBGM();
			CardCrawlGame.music.fadeOutTempBGM();
			AbstractDungeon.fadeOut();
			AbstractDungeon.topLevelEffects.clear();
			AbstractDungeon.actionManager.actions.clear();
			AbstractDungeon.effectList.clear();
			AbstractDungeon.effectsQueue.clear();
			completeRoom();
			AbstractDungeon.id = DUNGEON_ID.remove(0);

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
		}
	}

}