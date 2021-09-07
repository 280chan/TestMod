package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import utils.MiscMethods;

public class ArcanaOfDestinyPower extends AbstractTestPower implements InvisiblePower, MiscMethods {
	public static final String POWER_ID = "ArcanaOfDestinyPower";
	
	public static boolean hasThis(AbstractCreature m) {
		return m.powers.stream().anyMatch(p -> {return p instanceof ArcanaOfDestinyPower;});
	}
	
	public static void addThis(AbstractCreature m) {
		m.powers.add(new ArcanaOfDestinyPower(m));
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
		float tmp = this.owner.maxHealth == 0 ? 1f : this.owner.currentHealth * 1f / this.owner.maxHealth,
				p = playerRate(AbstractDungeon.player);
		return tmp > p ? damage * (1 - tmp + p) : damage;
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		float tmp = this.owner.maxHealth == 0 ? 0f : this.owner.currentHealth * 1f / this.owner.maxHealth,
				p = playerRate(AbstractDungeon.player);
		return tmp < p ? (int) (damage * (1 + 2 * (p - tmp))) : damage;
	}
	
	public void onRemove() {
		this.addTmpActionToTop(() -> {
			if (!hasThis(this.owner))
				addThis(this.owner);
		});
	}

}
