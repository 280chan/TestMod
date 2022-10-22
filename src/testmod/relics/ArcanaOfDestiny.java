package testmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.powers.ArcanaOfDestinyPower;

public class ArcanaOfDestiny extends AbstractTestRelic {
	
	private void tryApplyDebuff() {
		if (hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(ArcanaOfDestinyPower::hasThis))
					.forEach(ArcanaOfDestinyPower::addThis);
	}
	
	public void atPreBattle() {
		tryApplyDebuff();
	}

	public void update() {
		super.update();
		if (this.isActive && this.inCombat())
			tryApplyDebuff();
	}
	
	private void updateHp(int input) {
		if (hasEnemies() && input > 0) {
			this.addTmpActionToTop(() -> {
				tryApplyDebuff();
				AbstractDungeon.getMonsters().monsters.forEach(m -> m.applyPowers());
			});
		}
	}
	
	public void wasHPLost(int damage) {
		this.updateHp(damage);
	}
	
	public int onPlayerHeal(int amount) {
		this.updateHp(amount);
		return amount;
	}

}