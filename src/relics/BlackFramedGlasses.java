package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BlackFramedGlasses extends AbstractRevivalRelicToModifyDamage {
	public static final String ID = "BlackFramedGlasses";
	
	public BlackFramedGlasses() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStart() {
		show();
		AbstractDungeon.player.heal(1);
		this.addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(1, true),
				DamageInfo.DamageType.HP_LOSS, AttackEffect.POISON));
	}
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (2 * damageAmount >= AbstractDungeon.player.maxHealth) {
        	show();
        	return info.owner.isPlayer || info.owner == null ? 0 : 1;
        }
		return damageAmount;
    }
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		return 2 * info.output < p.maxHealth ? originalDamage : (info.owner.isPlayer || info.owner == null ? 0 : 1);
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return 2 * damageAmount >= p.maxHealth;
	}
	
}