package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import utils.MiscMethods;

public class ObsoleteBoomerang extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "ObsoleteBoomerang";
	
	public ObsoleteBoomerang() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void onUseCard(final AbstractCard targetCard, final UseCardAction useCardAction) {
		counter++;
		AttackEffect effect;
		if (counter < 6) { // 胡乱分级的伤害特效
			effect = AttackEffect.BLUNT_LIGHT;
		} else if (counter < 10) {
			effect = AttackEffect.SMASH;
		} else {
			effect = AttackEffect.BLUNT_HEAVY;
		}
		this.addToBot(new DamageRandomEnemyAction(new DamageInfo(AbstractDungeon.player, counter, DamageType.THORNS),
				effect));
		this.show();
	}

	public void atTurnStart() {
		this.counter = 0;
	}
	
}