package relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import powers.IndustrialRevolutionPower;
import powers.InorganicPower;

public class IndustrialRevolution extends AbstractTestRelic {
	
	public static final ArrayList<AbstractMonster> LIST = new ArrayList<AbstractMonster>();
	
	public IndustrialRevolution() {
		super(RelicTier.COMMON, LandingSound.FLAT, BAD);
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		LIST.clear();
		p().powers.add(new IndustrialRevolutionPower(p()));
		if (!this.hasEnemies())
			return;
		AbstractDungeon.getMonsters().monsters.stream()
				.filter(not(m -> m.isDead || m.isDying || m.halfDead || !m.hasPower("Artifact"))).peek(LIST::add)
				.forEach(m -> m.powers.add(new InorganicPower(m)));
    }
	

	private void tryAdd() {
		if (!IndustrialRevolutionPower.hasThis(p()))
			this.addPower(new IndustrialRevolutionPower(p()));
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