package christmasMod.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.defect.DamageAllButOneEnemyAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import powers.AbstractTestPower;

public class WarPower extends AbstractTestPower {
	public static final String POWER_ID = "WarPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private boolean canApply = true;
	
	public WarPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
    
	private static AbstractGameAction currentAction() {
		AbstractGameAction a = AbstractDungeon.actionManager.currentAction;
		if (a instanceof DamageAllEnemiesAction) {
			return a;
		} else if (a instanceof DamageAllButOneEnemyAction) {
			return a;
		} else if (a instanceof VampireDamageAllEnemiesAction) {
			return a;
		} else {
			TestMod.info("未知的aoe action");
			return null;
		}
	}
	
	private static void apply(int[] damage) {
		AbstractGameAction a = currentAction();
		if (a != null) {
			AbstractCreature source = a.source;
			DamageType damageType = a.damageType;
			AttackEffect attackEffect = a.attackEffect;
			for (AbstractPower p : AbstractDungeon.player.powers) {
				p.onDamageAllEnemies(damage);
			}
			int temp = AbstractDungeon.getCurrRoom().monsters.monsters.size();
			for (int i = 0; i < temp; i++) {
				if (!((AbstractMonster) AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).isDeadOrEscaped()) {
					if (attackEffect == AbstractGameAction.AttackEffect.POISON) {
						((AbstractMonster) AbstractDungeon.getCurrRoom().monsters.monsters
								.get(i)).tint.color = Color.CHARTREUSE.cpy();
						((AbstractMonster) AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).tint
								.changeColor(Color.WHITE.cpy());
					} else if (attackEffect == AbstractGameAction.AttackEffect.FIRE) {
						((AbstractMonster) AbstractDungeon.getCurrRoom().monsters.monsters
								.get(i)).tint.color = Color.RED.cpy();
						((AbstractMonster) AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).tint
								.changeColor(Color.WHITE.cpy());
					}
					((AbstractMonster) AbstractDungeon.getCurrRoom().monsters.monsters.get(i))
							.damage(new DamageInfo(source, damage[i], damageType));
				}
			}
			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
			}
		}
	}
	
	public void onDamageAllEnemies(int[] damage) {
		if (this.canApply) {
			this.canApply = false;
			for (int i = 0; i < this.amount; i++)
				apply(damage);
			this.canApply = true;
		}
	}
    
}
