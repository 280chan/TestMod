package testmod.potions;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public class EscapePotion extends AbstractTestPotion implements MiscMethods {
	public static final String POTION_ID = TestMod.makeID("EscapePotion");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final PowerManager S = new PowerManager("Surrounded"), B = new PowerManager("BackAttack");

	public EscapePotion() {
		super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.SPHERE, PotionColor.WHITE);
		this.isThrown = true;
		this.targetRequired = true;
	}
	
	private static class PowerManager {
		String id;
		PowerManager(String id) {
			this.id = id;
		}
		boolean has(AbstractCreature c) {
			return c.hasPower(this.id);
		}
		void remove(AbstractCreature c) {
			MISC.atb(new RemoveSpecificPowerAction(c, c, this.id));
		}
	}
	
	public String getDesc() {
		return DESCRIPTIONS[0];
	}
	
	private static boolean minion(AbstractMonster m) {
		return m.hasPower("Minion") && !m.isDying;
	}

	private static void escape(AbstractMonster m) {
		MISC.atb(new EscapeAction(m));
	}
	
	private static boolean alive(AbstractMonster m) {
		return !m.isDead && !m.isDying && !m.escaped && !m.isEscaping;
	}
	
	private static void checkFlip(AbstractMonster m) {
		if (S.has(MISC.p())) {
			MISC.p().flipHorizontal = (m.drawX < MISC.p().drawX);
			TestMod.info("flipHorizontal:" + MISC.p().flipHorizontal);
		}
	}
	
	private static void filterEscape() {
		AbstractDungeon.getMonsters().monsters.stream().filter(m -> minion(m)).forEach(m -> escape(m));
	}
	
	public static void escape(AbstractMonster m, boolean includeBoss) {
		boolean flipPlayer = false, darkling = false;
		if (!includeBoss && m.type == EnemyType.BOSS) // TODO
			return;
		if (darkling = m.id.equals("Darkling")) {
			if (AbstractDungeon.getMonsters().monsters.stream().noneMatch(a -> !a.halfDead)) {
				AbstractDungeon.getCurrRoom().cannotLose = false;
			} else {
				m.halfDead = true;
				m.currentHealth = 0;
			}
		} else if (S.has(MISC.p())) {
			flipPlayer = true;
		}
		MISC.atb(new VFXAction(new SmokeBombEffect(m.hb.cX, m.hb.cY)));
		MISC.atb(new EscapeAction(m));
		if (darkling) {
			MISC.addTmpActionToBot(() -> AbstractDungeon.getMonsters().monsters.remove(m), () -> filterEscape());
		} else if (flipPlayer) {
			MISC.addTmpActionToBot(() -> {
				if (!B.has(m)) {
					AbstractDungeon.getMonsters().monsters.stream().filter(a -> alive(a)).peek(a -> checkFlip(a))
							.filter(B::has).forEach(B::remove);
				} else {
					MISC.p().flipHorizontal = false;
				}
				S.remove(MISC.p());
			});
		} else {
			MISC.addTmpActionToBot(() -> filterEscape());
		}
	}
	
	public void use(AbstractCreature target) {
		escape((AbstractMonster) target, false);
	}

	public int getPotency(int ascensionLevel) {
		return 0;
	}
}
