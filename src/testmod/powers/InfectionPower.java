package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.PoisonPower;
import testmod.relics.InfectionSource;

public class InfectionPower extends AbstractTestPower implements InvisiblePower {
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof InfectionPower);
	}
	
	public InfectionPower(AbstractCreature owner) {
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.addMap(p -> new InfectionPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public int onAttacked(final DamageInfo info, final int dmg) {
		if (info.type == DamageType.NORMAL && dmg > 0) {
			this.relicStream(InfectionSource.class).peek(r -> r.show())
					.forEach(r -> this.att(this.apply(p(), new PoisonPower(owner, p(), dmg))));
			return 1;
		}
		return dmg;
	}

}
