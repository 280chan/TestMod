package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BlackFramedGlassesUp extends AbstractUpgradedRevivalRelic {
	
	public BlackFramedGlassesUp() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void atTurnStart() {
		this.addTmpActionToBot(() -> {
			int amount = AbstractDungeon.getMonsters().monsters.size();
			this.att(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(amount, true),
					DamageInfo.DamageType.HP_LOSS, AttackEffect.POISON));
			this.addTmpActionToTop(() -> p().heal(amount));
		});
		show();
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (damage >= p().maxHealth / 3.0) {
        	show();
        	if (info.owner != null && !info.owner.isPlayer) {
        		this.att(new LoseHPAction(info.owner, null, damage));
        	}
        	return 0;
        }
		return damage;
    }
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		if (info.output >= p.maxHealth / 3.0) {
        	if (info.owner != null && !info.owner.isPlayer) {
        		this.att(new LoseHPAction(info.owner, null, originalDamage));
        	}
        	return 0;
		}
		return originalDamage;
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damage) {
		return damage >= p.maxHealth / 3.0;
	}
	
}