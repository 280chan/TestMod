package testmod.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import testmod.powers.InorganicPower;

public class IndustrialRevolution extends AbstractTestRelic implements OnReceivePowerRelic {
	public static final ArrayList<AbstractMonster> LIST = new ArrayList<AbstractMonster>();
	
	public void atPreBattle() {
		if (!isActive)
			return;
		LIST.clear();
		if (!this.hasEnemies())
			return;
		AbstractDungeon.getMonsters().monsters.stream()
				.filter(not(m -> m.isDead || m.isDying || m.halfDead || !m.hasPower("Artifact"))).peek(LIST::add)
				.forEach(m -> m.powers.add(new InorganicPower(m)));
	}
	

	private void tryAdd() {
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

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature s) {
		return !(LIST.contains(s) && p.type == PowerType.DEBUFF);
	}
	
}