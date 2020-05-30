package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import actions.ObsoleteBoomerangDamageAction;
import mymod.TestMod;

public class ObsoleteBoomerang extends MyRelic{
	
	public static final String ID = "ObsoleteBoomerang";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "你在你的回合中每打出一张牌，对随机敌人造成你在该回合已打出的牌数的伤害。";
	public static AttackEffect effect;
	
	public ObsoleteBoomerang() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
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
		// 在角色上方显示遗物触发特效
	    if (counter < 6) {			// 胡乱分级的伤害特效
	        effect = AttackEffect.BLUNT_LIGHT;
	    } else if (counter < 21) {
	        effect = AttackEffect.SMASH;
	    } else {
	        effect = AttackEffect.BLUNT_HEAVY;
	    }
	    AbstractDungeon.actionManager.addToBottom(new ObsoleteBoomerangDamageAction(new DamageInfo(AbstractDungeon.player, counter, DamageInfo.DamageType.THORNS), effect));
	    this.show();
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		this.counter = 0;
    }//触发时机：在玩家回合开始时。
	
}