package relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

/**
 * @deprecated
 */
public class 示例遗物 extends AbstractTestRelic {
	
	public 示例遗物() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		// 如不需要可删除
		return DESCRIPTIONS[0];
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
    }
	
	public void onUseCard(final AbstractCard targetCard, final UseCardAction useCardAction) {
	}
	
	public void onExhaust(final AbstractCard card) {
    }
	
	public void onCardDraw(final AbstractCard drawnCard) {
    }
	
	public void onGainGold() {
    }
	
	public void onLoseGold() {
    }
	
	public void onEquip() {
    }
	
	public void onUnequip() {
    }
	
	public void atPreBattle() {
    }
	
	public void atBattleStart() {
    }
	
	public void atBattleStartPreDraw() {
    }
	
	public void atTurnStart() {
    }
	
	public void onPlayerEndTurn() {
    }
	
	public void onManualDiscard() {
    }
	
	public void onVictory() {
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
    }
	
	public int onPlayerGainedBlock(float blockAmount) {
		return super.onPlayerGainedBlock(blockAmount);
	}
	
	public int onPlayerHeal(int healAmount) {
        return healAmount;
    }
	
	public void onEnterRestRoom() {
    }
	
	public void onRest() {
    }
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
        return damageAmount;
    }
	
	public void onEnterRoom(final AbstractRoom room) {
    }
	
	public void onChestOpen(final boolean bossChest) {
    }
	
	public void onDrawOrDiscard() {
    }
	
	public void onLoseHp(int damageAmount) {
	}

}