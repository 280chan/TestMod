package testmod.powers;

import java.util.stream.Stream;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class TaurusBlackCatEnemyPower extends AbstractTestPower implements InvisiblePower {
	private static final int PRIORITY = 100000;
	public static boolean lock = false;
	private static boolean mute = false;

	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof TaurusBlackCatEnemyPower);
	}
	
	public static AbstractPower getThis(AbstractCreature owner) {
		return owner.powers.stream().filter(p -> p instanceof TaurusBlackCatEnemyPower).findAny().orElse(null);
	}
	
	public TaurusBlackCatEnemyPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = PRIORITY;
	}
	
	public void updateDescription() {
		 this.description = "";
	}

	private int monsterAmount() {
		return (int) monsters().count();
	}

	private Stream<AbstractMonster> monsters() {
		return AbstractDungeon.getMonsters().monsters.stream()
				.filter(m -> !(m.isDead || m.isDying || m.escaped || m.halfDead) && m.currentHealth > 0);
	}

	public int onAttacked(final DamageInfo info, final int damage) {
		if (!lock && damage / 100f * this.amount > 1) {
			this.addTmpActionToBot(() -> {
				int d = (int) (damage / 100f / this.monsterAmount() * this.amount);
				if (d < 1)
					return;
				this.addTmpActionToTop(() -> {
					lock = true;
					int[] dmg = DamageInfo.createDamageMatrix(d, true);
					p().powers.forEach(p -> p.onDamageAllEnemies(dmg));
					monsters().forEach(m -> {
						AbstractDungeon.effectList
								.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.POISON, mute));
						mute = true;
						m.tint.color.set(Color.CHARTREUSE);
						m.tint.changeColor(Color.WHITE.cpy());
						m.damage(new DamageInfo(p(), d, DamageType.HP_LOSS));
					});

					if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
						AbstractDungeon.actionManager.clearPostCombatActions();
					}
					lock = mute = false;
				});
			});
		}
		return damage;
	}
	
	public void onRemove() {
		this.addTmpActionToTop(() -> p().powers.stream().filter(p -> p instanceof TaurusBlackCatPower).limit(1)
				.forEach(p -> p.stackPower(0)));
	}
    
}
