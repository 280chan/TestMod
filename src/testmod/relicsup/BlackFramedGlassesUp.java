package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BlackFramedGlassesUp extends AbstractUpgradedRevivalRelic {
	
	public void atTurnStart() {
		this.addTmpActionToBot(() -> {
			int amount = AbstractDungeon.getMonsters().monsters.size();
			this.att(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(amount, true),
					DamageInfo.DamageType.HP_LOSS, AttackEffect.POISON));
			this.addTmpActionToTop(() -> p().heal(amount));
		});
		show();
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
	private BlackFramedGlassesUp min() {
		return relicStream(BlackFramedGlassesUp.class).reduce((a, b) -> a.counter <= b.counter ? a : b).orElse(null);
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (this.isActive && damage >= p().maxHealth / 3.0) {
			show();
			if (info.owner != null && !info.owner.isPlayer) {
				BlackFramedGlassesUp min = min();
				min.counter++;
				relicStream(BlackFramedGlassesUp.class).forEach(r -> att(new LoseHPAction(info.owner, null, damage)));
				if (min.counter > p().maxHealth) {
					return 1;
				}
			}
			return 0;
		}
		return damage;
	}
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int damage) {
		if (this.isActive && info.output >= p.maxHealth / 3.0) {
			if (info.owner != null && !info.owner.isPlayer) {
				BlackFramedGlassesUp min = min();
				min.counter++;
				relicStream(BlackFramedGlassesUp.class).forEach(r -> att(new LoseHPAction(info.owner, null, damage)));
				if (min.counter > p().maxHealth) {
					return 1;
				}
			}
			return 0;
		}
		return damage;
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damage) {
		return this.isActive && damage >= p.maxHealth / 3.0;
	}
	
}