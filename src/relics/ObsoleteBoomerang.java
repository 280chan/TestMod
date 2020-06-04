package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import actions.ObsoleteBoomerangDamageAction;

public class ObsoleteBoomerang extends MyRelic{
	
	public static final String ID = "ObsoleteBoomerang";
	public static AttackEffect effect;
	
	public ObsoleteBoomerang() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onUseCard(final AbstractCard targetCard, final UseCardAction useCardAction) {
		if (!isActive)
			return;
		if (targetCard.type == AbstractCard.CardType.STATUS && targetCard.color != AbstractCard.CardColor.COLORLESS) {
			return;
		}
		counter++;
	    if (counter < 6) {			// 胡乱分级的伤害特效
	        effect = AttackEffect.BLUNT_LIGHT;
	    } else if (counter < 21) {
	        effect = AttackEffect.SMASH;
	    } else {
	        effect = AttackEffect.BLUNT_HEAVY;
	    }
	    this.addToBot(new ObsoleteBoomerangDamageAction(new DamageInfo(AbstractDungeon.player, counter, DamageType.THORNS), effect));
	    this.show();
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		this.counter = 0;
    }
	
}