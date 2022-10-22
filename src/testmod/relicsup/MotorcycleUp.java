package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

public class MotorcycleUp extends AbstractUpgradedRelic implements ClickableRelic {
	
	public void atPreBattle() {
		if (!checkBoss())
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
	
	private int square(int x) {
		return x < 14655 ? x * x : 214740000;
	}
	
	@Override
	public void onRightClick() {
		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			if (checkBoss() || p().isEscaping || !AbstractDungeon.overlayMenu.endTurnButton.enabled)
				return;
			this.stopPulse();
			AbstractDungeon.getCurrRoom().smoked = true;
			this.atb(new VFXAction(new SmokeBombEffect(p().hb.cX, p().hb.cY)));
			p().hideHealthBar();
			p().isEscaping = true;
			AbstractDungeon.overlayMenu.endTurnButton.disable();
			p().escapeTimer = 2.5F;
			if (!AbstractDungeon.getMonsters().monsters.isEmpty())
				p().gainGold(square(AbstractDungeon.getMonsters().monsters.size()) * 10);
		}
	}

}