package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import testmod.powers.AbstractTestPower;

public class InfectionSourceUp extends AbstractUpgradedRelic {

	public void atPreBattle() {
		if (this.isActive && this.hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDead && !m.isDying)
					.forEach(m -> m.powers.add(new InfectionPowerUp(m)));
	}
	
	public void atTurnStart() {
		if (this.isActive)
			for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
				if (!(m.halfDead || m.isDead || m.isDying || m.escaped || m.isEscaping || InfectionPowerUp.hasThis(m)))
					m.powers.add(new InfectionPowerUp(m));
	}
	
	public static class InfectionPowerUp extends AbstractTestPower implements InvisiblePower {
		
		public static boolean hasThis(AbstractCreature owner) {
			return owner.powers.stream().anyMatch(p -> p instanceof InfectionPowerUp);
		}
		
		public InfectionPowerUp(AbstractCreature owner) {
			this.owner = owner;
			this.amount = -1;
			updateDescription();
			this.type = PowerType.DEBUFF;
			this.addMap(p -> new InfectionPowerUp(p.owner));
		}
		
		public void updateDescription() {
			 this.description = "";
		}
		
		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
		}
	    
	    public int onAttacked(final DamageInfo info, final int dmg) {
			if (info.type != DamageType.HP_LOSS && dmg > 0) {
				this.relicStream(InfectionSourceUp.class).peek(r -> r.show())
						.forEach(r -> att(apply(p(), new PoisonPower(owner, p(), dmg))));
			}
	    	return dmg;
	    }

	}

}