package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import utils.MiscMethods;

public class TaurusBlackCatEnemyPower extends AbstractTestPower implements InvisiblePower, MiscMethods {
	public static final String POWER_ID = "TaurusBlackCatEnemyPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final int PRIORITY = 100000;

	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof TaurusBlackCatEnemyPower);
	}
	
	public static AbstractPower getThis(AbstractCreature owner) {
		return owner.powers.stream().filter(p -> p instanceof TaurusBlackCatEnemyPower).findAny().orElse(null);
	}
	
	public TaurusBlackCatEnemyPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
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
		return (int) AbstractDungeon.getMonsters().monsters.stream()
				.filter(m -> !(m.isDead || m.isDying || m.escaped || m.halfDead)).count();
	}

	public int onAttacked(final DamageInfo info, final int damage) {
		if (info.type != DamageType.HP_LOSS && damage / 100f * this.amount > 1) {
			this.addTmpActionToBot(() -> {
				int d = (int) (damage / 100f / this.monsterAmount() * this.amount);
				if (d > 0)
					this.addToTop(new DamageAllEnemiesAction(AbstractDungeon.player,
							DamageInfo.createDamageMatrix(d, true), DamageType.HP_LOSS, AttackEffect.POISON, true));
			});
		}
		return damage;
	}
    
}
