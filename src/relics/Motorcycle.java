package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import mymod.TestMod;

public class Motorcycle extends AbstractClickRelic {
	public static final String ID = "Motorcycle";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "非Boss战斗中第一回合可以右击该遗物逃跑。 NL ( #ySL 后本场战斗失效)";
	private static int loadedFloor = 0;
	
	public Motorcycle() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.UNCOMMON, LandingSound.MAGICAL);
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
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (m.type == EnemyType.BOSS)
				return true;
		return false;
	}
	
	@Override
	protected void onRightClick() {
		if (loadedFloor < AbstractDungeon.floorNum && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			AbstractPlayer p = AbstractDungeon.player;
			if (GameActionManager.turn > 1 || checkBoss() || p.isEscaping || !AbstractDungeon.overlayMenu.endTurnButton.enabled)
				return;
			this.stopPulse();
			AbstractDungeon.getCurrRoom().smoked = true;
			AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmokeBombEffect(p.hb.cX, p.hb.cY)));
			p.hideHealthBar();
			p.isEscaping = true;
			AbstractDungeon.overlayMenu.endTurnButton.disable();
			p.escapeTimer = 2.5F;
		}
	}
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}

}