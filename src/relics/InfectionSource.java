package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import powers.InfectionPower;
import powers.InfectionSourcePower;

public class InfectionSource extends AbstractTestRelic {
	public static final String ID = "InfectionSource";
	
	public InfectionSource() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void atBattleStart() {
		flash();
		AbstractPlayer p = AbstractDungeon.player;
		this.addToTop(new RelicAboveCreatureAction(p, this));
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!m.isDead && !m.isDying)
				m.powers.add(new InfectionPower(m));
		this.addToTop(new ApplyPowerAction(p, p, new InfectionSourcePower(p)));
    }
	
	public void atTurnStart() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!(m.halfDead || m.isDead || m.isDying || m.escaped || m.isEscaping || InfectionPower.hasThis(m)))
				this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new InfectionPower(m)));
    }

}