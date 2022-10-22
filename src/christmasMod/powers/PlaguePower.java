package christmasMod.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;

import christmasMod.actions.PlaguePoisonActAction;
import testmod.powers.AbstractTestPower;

public class PlaguePower extends AbstractTestPower {
	public static final String POWER_ID = "christmas-PlaguePower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public PlaguePower(AbstractCreature owner, int amount) {
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
	
	private void effect() {
		boolean acted = false;
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (m.hasPower(PoisonPower.POWER_ID)) {
				for (int i = 0; i < this.amount; i++)
					this.att(new PlaguePoisonActAction(this.owner, m.getPower(PoisonPower.POWER_ID)));
				acted = true;
			}
		}
		if (acted)
			this.flash();
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (info.type == DamageType.NORMAL && damage > 0)
			effect();
		return damage;
	}

}
