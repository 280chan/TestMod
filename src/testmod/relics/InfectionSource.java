package testmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.powers.InfectionPower;
import testmod.powers.InfectionSourcePower;

public class InfectionSource extends AbstractTestRelic {
	
	public InfectionSource() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL, BAD);
	}

	public void atBattleStart() {
		this.show();
		AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDead && !m.isDying)
				.forEach(m -> m.powers.add(new InfectionPower(m)));
		this.addToTop(apply(p(), new InfectionSourcePower(p())));
    }
	
	public void atTurnStart() {
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
			if (!(m.halfDead || m.isDead || m.isDying || m.escaped || m.isEscaping || InfectionPower.hasThis(m)))
				this.addToBot(apply(p(), new InfectionPower(m)));
    }

}