package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import testmod.relicsup.BlackFramedGlassesUp;

public class BlackFramedGlasses extends AbstractRevivalRelicToModifyDamage {
	
	public void atTurnStart() {
		show();
		p().heal(1);
		this.addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(1, true),
				DamageInfo.DamageType.HP_LOSS, AttackEffect.POISON));
	}
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (this.relicStream(BlackFramedGlassesUp.class).count() == 0 && damageAmount >= p().maxHealth / 2.0) {
			show();
			return info.owner == null || info.owner.isPlayer ? 0 : 1;
		}
		return damageAmount;
	}
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		return this.relicStream(BlackFramedGlassesUp.class).count() > 0 || info.output < p.maxHealth / 2.0
				? originalDamage : (info.owner == null || info.owner.isPlayer ? 0 : 1);
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return this.relicStream(BlackFramedGlassesUp.class).count() == 0 && damageAmount >= p.maxHealth / 2.0;
	}
	
}