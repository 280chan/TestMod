package testmod.relicsup;

import java.util.ArrayList;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import testmod.powers.AbstractTestPower;
import testmod.powers.InorganicPower;

public class IndustrialRevolutionUp extends AbstractUpgradedRelic implements ClickableRelic, OnReceivePowerRelic {
	public static final ArrayList<AbstractMonster> LIST = new ArrayList<AbstractMonster>();
	
	public void onVictory() {
		if (!isActive)
			return;
		LIST.clear();
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		LIST.clear();
		if (!this.hasEnemies())
			return;
		AbstractDungeon.getMonsters().monsters.stream()
				.filter(not(m -> m.isDead || m.isDying || m.halfDead || !m.hasPower("Artifact"))).peek(LIST::add)
				.forEach(m -> m.powers.add(new InorganicPowerUp(m)));
	}

	private void tryAdd() {
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && !InorganicPowerUp.hasThis(m)) {
				if (m.hasPower("Artifact")) {
					m.powers.add(new InorganicPowerUp(m));
					LIST.add(m);
				} else if (LIST.contains(m)) {
					m.powers.add(new InorganicPowerUp(m));
				}
			}
		}
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		tryAdd();
		this.addTmpActionToBot(() -> {
			if (AbstractDungeon.getMonsters().monsters.stream().allMatch(m -> m.hasPower("Artifact")))
				this.att(new GainEnergyAction((int) this.relicStream(IndustrialRevolutionUp.class).count()));
		});
	}
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		tryAdd();
	}

	@Override
	public void onRightClick() {
		if (this.inCombat()) {
			AbstractDungeon.getMonsters().monsters.forEach(m -> this.atb(apply(p(), new ArtifactPower(m, 1))));
			this.addTmpActionToBot(this::tryAdd);
		}
	}

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature s) {
		return !(LIST.contains(s) && p.type == PowerType.DEBUFF);
	}
	
	private static class InorganicPowerUp extends AbstractTestPower implements OnReceivePowerPower {
		
		private static boolean check(AbstractCreature m) {
			return LIST.contains(m);
		}

		public static boolean hasThis(AbstractCreature owner) {
			return owner.powers.stream().anyMatch(p -> p instanceof InorganicPowerUp);
		}
		
		public InorganicPowerUp(AbstractCreature owner) {
			this.owner = owner;
			this.amount = -1;
			updateDescription();
			this.type = PowerType.DEBUFF;
			this.addMap(p -> new InorganicPowerUp(p.owner));
		}
		
		public void updateDescription() {
			 this.description = desc(0);
		}
		
		public void stackPower(int stackAmount) {
			this.fontScale = 8.0f;
		}
		
		@Override
		public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature s) {
			return !(check(s) && t.equals(this.owner) && p.type == PowerType.BUFF) || InorganicPower.isException(p);
		}
	}
}