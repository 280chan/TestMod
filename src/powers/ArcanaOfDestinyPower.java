package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ArcanaOfDestinyPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "ArcanaOfDestinyPower";
	
	public static boolean hasThis(AbstractCreature m) {
		for (AbstractPower p : m.powers)
			if (p instanceof ArcanaOfDestinyPower)
				return true;
		return false;
	}
	
	public ArcanaOfDestinyPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public float playerRate(AbstractPlayer p) {
		return p.currentHealth * 1f / p.maxHealth;
	}
	
	public float atDamageGive(float damage, DamageType type) {
		float tmp, p = playerRate(AbstractDungeon.player);
		if (this.owner.maxHealth == 0) {
			tmp = 1f;
		} else {
			tmp = this.owner.currentHealth * 1f / this.owner.maxHealth;
		}
		if (tmp > p)
			return damage * (1 - tmp + p);
		return damage;
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		float tmp, p = playerRate(AbstractDungeon.player);
		if (this.owner.maxHealth == 0) {
			tmp = 0f;
		} else {
			tmp = this.owner.currentHealth * 1f / this.owner.maxHealth;
		}
		if (tmp < p)
			return (int) (damage * (1 + 2 * (p - tmp)));
		return damage;
	}
	
	public void onRemove() {
		this.addToTop(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				if (!hasThis(ArcanaOfDestinyPower.this.owner))
					ArcanaOfDestinyPower.this.owner.powers.add(new ArcanaOfDestinyPower(ArcanaOfDestinyPower.this.owner));
			}
		});
	}

}
