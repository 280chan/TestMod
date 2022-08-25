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
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.id = DUNGEON_ID.get(0);
			DUNGEON_ID.remove(0);
			//AbstractDungeon.dungeonMapScreen.open(true);
			
			this.addLowLevelEffect(() -> {
				this.show();
				MapRoomNode node = new MapRoomNode(-1, 15);
		        node.room = new MonsterRoomBoss();
		        AbstractDungeon.nextRoom = node;
		        CardCrawlGame.music.fadeOutTempBGM();
		        AbstractDungeon.pathX.add(Integer.valueOf(1));
		        AbstractDungeon.pathY.add(Integer.valueOf(15));
		        AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
		        AbstractDungeon.nextRoomTransitionStart();
			});
		}
	}

}