package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class OneHitWonderDebuffPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "OneHitWonderDebuffPower";
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> {return p instanceof OneHitWonderDebuffPower;});
	}
	
	public OneHitWonderDebuffPower(AbstractCreature owner) {
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
	
	private static boolean checkPlayerHealth() {
		return AbstractDungeon.player.currentHealth == 1;
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (checkPlayerHealth() && info.type != DamageType.NORMAL)
			return (int)(1.5f * damage);
		return damage;
	}
	
	public float atDamageFinalGive(float damage, DamageType type) {
		if (checkPlayerHealth())
			return 0.5f * damage;
		return damage;
	}
    
	public float atDamageFinalReceive(float damage, DamageType type) {
		if (checkPlayerHealth() && type == DamageType.NORMAL)
			return 1.5f * damage;
		return damage;
	}

	public void onRemove() {
		this.addToTop(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				if (!hasThis(OneHitWonderDebuffPower.this.owner))
					OneHitWonderDebuffPower.this.owner.powers
							.add(new OneHitWonderDebuffPower(OneHitWonderDebuffPower.this.owner));
			}
		});
	}
    
}
