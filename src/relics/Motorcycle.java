package relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

public class Motorcycle extends AbstractTestRelic implements ClickableRelic {
	public static final String ID = "Motorcycle";
	private static int loadedFloor = 0;
	
	public Motorcycle() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public static void loadGame() {
		loadedFloor = AbstractDungeon.floorNum;
	}
	
	public void atPreBattle() {
		if (loadedFloor < AbstractDungeon.floorNum && !checkBoss())
			this.beginLongPulse();
    }
	
	public void onPlayerEndTurn() {
		this.stopPulse();
    }
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private static boolean checkBoss() {
		return AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> m.type == EnemyType.BOSS);
	}
	
	@Override
	public void onRightClick() {
		if (loadedFloor < AbstractDungeon.floorNum && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			AbstractPlayer p = AbstractDungeon.player;
			if (GameActionManager.turn > 1 || checkBoss() || p.isEscaping
					|| !AbstractDungeon.overlayMenu.endTurnButton.enabled)
				return;
			this.stopPulse();
			AbstractDungeon.getCurrRoom().smoked = true;
			this.addToBot(new VFXAction(new SmokeBombEffect(p.hb.cX, p.hb.cY)));
			p.hideHealthBar();
			p.isEscaping = true;
			AbstractDungeon.overlayMenu.endTurnButton.disable();
			p.escapeTimer = 2.5F;
		}
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}

}