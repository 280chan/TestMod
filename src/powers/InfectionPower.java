package powers;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class InfectionPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "InfectionPower";
	private float tempDamage;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> {return p instanceof InfectionPower;});
	}
	
	public InfectionPower(AbstractCreature owner) {
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

	public float atDamageFinalReceive(float damage, DamageType type) {
		return this.tempDamage = damage;
	}
    
    public int onAttacked(final DamageInfo info, final int damageAmount) {
    	if (info.type == DamageType.NORMAL) {
    		int temp = MathUtils.floor(tempDamage);
        	if (temp > 0) {
        		AbstractPlayer p = AbstractDungeon.player;
        		this.addToTop(new ApplyPowerAction(owner, p, new PoisonPower(owner, p, temp), temp));
        		if (damageAmount > 0) {
        			return 1;
        		}
        	}
    	}
    	return damageAmount;
    }

    public void onRemove() {
		this.addToTop(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				if (!hasThis(InfectionPower.this.owner))
					InfectionPower.this.owner.powers.add(new InfectionPower(InfectionPower.this.owner));
			}
		});
	}

}
