package relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import powers.InfectionPower;
import powers.InfectionSourcePower;

public class InfectionSource extends AbstractTestRelic {
	
	public InfectionSource() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL, BAD);
	}

	public void atBattleStart() {
		flash();
		this.addToTop(new RelicAboveCreatureAction(p(), this));
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
			if (!m.isDead && !m.isDying)
				m.powers.add(new InfectionPower(m));

		this.addToTop(apply(p(), new InfectionSourcePower(p())));
    }
	
	public void atTurnStart() {
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
			if (!(m.halfDead || m.isDead || m.isDying || m.escaped || m.isEscaping || InfectionPower.hasThis(m)))
				this.addToBot(apply(p(), new InfectionPower(m)));
    }

}