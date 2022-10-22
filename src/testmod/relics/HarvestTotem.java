package testmod.relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

public class HarvestTotem extends AbstractTestRelic {
	private static final ArrayList<AbstractCreature> DONE = new ArrayList<AbstractCreature>();
	private boolean init = false;
	
	public void onEquip() {
		this.counter = -2;
		this.init = true;
		p().increaseMaxHp(Math.max(1, p().maxHealth), false);
		this.init = false;
	}
	
	private static void increaseMaxHp(AbstractCreature m, int amount) {
		if (!Settings.isEndless) {
			if (m.isPlayer && MISC.p().hasBlight("FullBelly"))
				return;
			m.maxHealth += amount;
			AbstractDungeon.effectsQueue.add(new TextAboveCreatureEffect(m.hb.cX - m.animX, m.hb.cY,
					AbstractCreature.TEXT[2] + Integer.toString(amount), Settings.GREEN_TEXT_COLOR));
			m.heal(amount, true);
			m.healthBarUpdatedEvent();
		}
	}
	
	private void increaseaMonsterMaxHp(AbstractMonster m) {
		increaseMaxHp(m, m.maxHealth / (m.type == EnemyType.BOSS ? 2 : (m.type == EnemyType.ELITE ? 5 : 10)));
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = -2;
		DONE.clear();
		if (!this.hasEnemies())
			return;
		AbstractDungeon.getMonsters().monsters.stream().peek(this::increaseaMonsterMaxHp).forEach(DONE::add);
	}
	
	public void update() {
		super.update();
		if (!this.isActive || this.counter == -2 || AbstractDungeon.currMapNode == null
				|| AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return;
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (!DONE.contains(m)) {
				if (m.halfDead || !m.hasPower("Minion"))
					continue;
				this.increaseaMonsterMaxHp(m);
				DONE.add(m);
			} else if (m.id.equals("AwakenedOne") && m.halfDead) {
				DONE.remove(m);
			}
		}
	}
	
	public int onPlayerHeal(int amount) {
		return this.init ? amount : Math.max(0, Math.min(2 * amount, Integer.MAX_VALUE - p().currentHealth));
	}
	
	public void atTurnStart() {
		this.counter = -2;
	}
	
	public void onPlayerEndTurn() {
		this.counter = -1;
	}
	
	public float preChangeMaxHP(float a) {
		return (a > 0 && !this.init) ? Math.max(0, Math.min(2 * a, Integer.MAX_VALUE - p().maxHealth)) : a;
	}
	
	public boolean canSpawn() {
		return Settings.isEndless && AbstractDungeon.actNum < 2;
	}
	
}