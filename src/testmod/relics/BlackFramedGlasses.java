package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class BlackFramedGlasses extends AbstractRevivalRelicToModifyDamage {
	public static final String ID = "BlackFramedGlasses";
	
	public BlackFramedGlasses() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void atTurnStart() {
		show();
		p().heal(1);
		this.addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(1, true),
				DamageInfo.DamageType.HP_LOSS, AttackEffect.POISON));
	}
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (2 * damageAmount >= p().maxHealth) {
        	show();
        	return info.owner == null || info.owner.isPlayer ? 0 : 1;
        }
		return damageAmount;
    }
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		return 2 * info.output < p.maxHealth ? originalDamage : (info.owner == null || info.owner.isPlayer ? 0 : 1);
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return 2 * damageAmount >= p().maxHealth;
	}
	
}