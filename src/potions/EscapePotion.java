package potions;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import mymod.TestMod;
import utils.MiscMethods;

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
			AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(c, c, this.id));
		}
	}
	
	public String getDesc() {
		return DESCRIPTIONS[0];
	}
	
	private boolean notHalfDead(AbstractMonster m) {
		return !m.halfDead;
	}
	
	private boolean minion(AbstractMonster m) {
		return m.hasPower("Minion") && !m.isDying;
	}

	private void escape(AbstractMonster m) {
		this.addToBot(new EscapeAction(m));
	}
	
	private boolean alive(AbstractMonster m) {
		return !m.isDead && !m.isDying && !m.escaped && !m.isEscaping;
	}
	
	private void checkFlip(AbstractMonster m) {
		if (S.has(p())) {
			p().flipHorizontal = (m.drawX < p().drawX);
			TestMod.info("flipHorizontal:" + p().flipHorizontal);
		}
	}
	
	private void filterEscape() {
		AbstractDungeon.getMonsters().monsters.stream().filter(this::minion).forEach(this::escape);
	}
	
	public void use(AbstractCreature target) {
		AbstractMonster m = (AbstractMonster) target;
		boolean flipPlayer = false, darkling = false;
		if (m.type == EnemyType.BOSS) // TODO
			return;
		if (darkling = m.id.equals("Darkling")) {
			if (AbstractDungeon.getMonsters().monsters.stream().noneMatch(this::notHalfDead)) {
				AbstractDungeon.getCurrRoom().cannotLose = false;
			} else {
				m.halfDead = true;
				m.currentHealth = 0;
			}
		} else if (S.has(p())) {
			flipPlayer = true;
		}
		this.addToBot(new VFXAction(new SmokeBombEffect(m.hb.cX, m.hb.cY)));
		this.addToBot(new EscapeAction(m));
		if (darkling) {
			this.addTmpActionToBot(() -> AbstractDungeon.getMonsters().monsters.remove(m), this::filterEscape);
		} else if (flipPlayer) {
			this.addTmpActionToBot(() -> {
				if (!B.has(m)) {
					AbstractDungeon.getMonsters().monsters.stream().filter(this::alive).peek(this::checkFlip)
							.filter(B::has).forEach(B::remove);
				} else {
					p().flipHorizontal = false;
				}
				S.remove(p());
			});
		} else {
			this.addTmpActionToBot(this::filterEscape);
		}
	}

	public int getPotency(int ascensionLevel) {
		return 0;
	}
}
