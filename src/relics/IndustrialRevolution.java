package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import powers.IndustrialRevolutionPower;
import powers.InorganicPower;
import utils.MiscMethods;

public class IndustrialRevolution extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "IndustrialRevolution";
	
	public static final ArrayList<AbstractMonster> LIST = new ArrayList<AbstractMonster>();
	
	public IndustrialRevolution() {
		super(ID, RelicTier.COMMON, LandingSound.FLAT);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		LIST.clear();
		AbstractPlayer p = AbstractDungeon.player;
		p.powers.add(new IndustrialRevolutionPower(p));
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && m.hasPower("Artifact")) {
				m.powers.add(new InorganicPower(m));
				LIST.add(m);
			}
		}
    }
	

	private void tryAdd() {
		AbstractPlayer p = AbstractDungeon.player;
		if (!IndustrialRevolutionPower.hasThis(p))
			p.powers.add(new IndustrialRevolutionPower(p));
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && !InorganicPower.hasThis(m)) {
				if (m.hasPower("Artifact")) {
					m.powers.add(new InorganicPower(m));
					LIST.add(m);
				} else if (LIST.contains(m)) {
					m.powers.add(new InorganicPower(m));
				}
			}
		}
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		tryAdd();
    }
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		tryAdd();
    }
	
}