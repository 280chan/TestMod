package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

import mymod.TestMod;
import utils.MiscMethods;

public class HarvestTotem extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "HarvestTotem";
	
	private static final ArrayList<AbstractCreature> DONE = new ArrayList<AbstractCreature>();
	private static boolean init = false;
	
	public HarvestTotem() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
		this.counter = -2;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		init = true;
		AbstractDungeon.player.increaseMaxHp(AbstractDungeon.player.maxHealth, false);
		init = false;
    }
	
	private static void increaseMaxHp(AbstractCreature m, int amount) {
		if (!Settings.isEndless) {
			if (m.isPlayer && AbstractDungeon.player.hasBlight("FullBelly"))
				return;
			m.maxHealth += amount;
			AbstractDungeon.effectsQueue.add(new TextAboveCreatureEffect(m.hb.cX - m.animX, m.hb.cY,
					AbstractCreature.TEXT[2] + Integer.toString(amount), Settings.GREEN_TEXT_COLOR));
			m.heal(amount, true);
			m.healthBarUpdatedEvent();
		}
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = -2;
		DONE.clear();
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (m.type == EnemyType.BOSS)
				increaseMaxHp(m, m.maxHealth / 2);
			else if (m.type == EnemyType.ELITE)
				increaseMaxHp(m, m.maxHealth / 5);
			else if (m.type == EnemyType.NORMAL)
				increaseMaxHp(m, m.maxHealth / 10);
			DONE.add(m);
		}
    }
	
	public void update() {
		super.update();
		if (!this.isActive || this.counter == -2 || AbstractDungeon.currMapNode == null
				|| AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return;
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!DONE.contains(m)) {
				if (m.halfDead || !m.hasPower("Minion"))
					continue;
				if (m.type == EnemyType.BOSS)
					increaseMaxHp(m, m.maxHealth / 2);
				else if (m.type == EnemyType.ELITE)
					increaseMaxHp(m, m.maxHealth / 5);
				else if (m.type == EnemyType.NORMAL)
					increaseMaxHp(m, m.maxHealth / 10);
				DONE.add(m);
			} else if (m.id.equals("AwakenedOne") && m.halfDead) {
				DONE.remove(m);
			}
		}
	}
	
	public int onPlayerHeal(int healAmount) {
		if (!this.isActive || init)
			return healAmount;
        return 2 * healAmount;
    }
	
	public void atTurnStart() {
		this.counter = -2;
    }
	
	public void onPlayerEndTurn() {
		this.counter = -1;
    }
	
	public float preChangeMaxHP(float amount) {
		if (amount > 0 && this.isActive && !init)
			return 2 * amount;
		return amount;
	}
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}