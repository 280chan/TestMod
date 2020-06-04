package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

/**
 * @deprecated
 */
public class 示例遗物 extends MyRelic {
	public static final String ID = "";
	public static final String DESCRIPTION = "";
	
	public 示例遗物() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {//参数：c-使用的卡牌，m-目标敌人。
    }//触发时机：当一张卡被打出且卡牌效果生效前。
	//c.可调用卡牌信息,比如稀有度、费用。
	
	public void onUseCard(final AbstractCard targetCard, final UseCardAction useCardAction) {
	}//触发时机：当一张卡被打出且卡牌效果生效后。(参考死灵之书)
	//targetCard.可调用卡牌信息,比如稀有度、费用。
	
	public void onExhaust(final AbstractCard card) {
    }//触发时机：当你消耗一张 卡牌时。(参考卡戍之灰)
	//card.可调用卡牌信息,比如稀有度、费用。
	
	public void onCardDraw(final AbstractCard drawnCard) {
    }//触发时机：当你抽一张牌时。
	//drawnCard.可调用卡牌信息,比如稀有度、费用。
	
	//卡牌信息:c.cardID-卡牌Id,c.cost-卡牌费用,c.costForTurn-卡牌一回合内消耗的费用(如旋风斩的费用),c.color-卡牌颜色,c.damage-含力量、钢笔尖等加成的伤害,c.block-含敏捷等加成的格挡,c.baseDamage-不含力量、钢笔尖等加成的伤害,c.baseBlock-不含敏捷等加成的格挡
	//       c.magicNumber-卡牌特殊值,c.type-卡牌种类,c.rarity-卡牌稀有度.
	
	public void onGainGold() {
    }//触发时机：当玩家获得金币时。(参考金神像)
	
	public void onLoseGold() {
    }//触发时机：当玩家失去金币时。(参考鲜血神像)
	
	public void onEquip() {
    }//触发时机：当玩家获得该遗物时。(参考灵体外质、诅咒钥匙、天鹅绒项圈等)
	
	public void onUnequip() {
    }//触发时机：当玩家失去该遗物时。(参考灵体外质、诅咒钥匙、天鹅绒项圈等)
	
	public void atPreBattle() {
    }//触发时机：每一场战斗（具体作用时机未知）
	
	public void atBattleStart() {
    }//触发时机：当玩家战斗开始时，在第一轮抽牌之后。(参考金刚杵、缩放仪)
	
	public void atBattleStartPreDraw() {
    }//触发时机：当玩家战斗开始时，在第一轮抽牌的时候，每抽一次牌触发一次。（猜测效果，具体触发时机请实测）
	
	public void atTurnStart() {
    }//触发时机：在玩家回合开始时。
	
	public void onPlayerEndTurn() {
    }//触发时机：在玩家回合结束时。
	
	public void onManualDiscard() {
    }//触发时机：当你手动弃牌时。
	
	public void onVictory() {
    }//触发时机：当玩家战斗胜利时。(参考精致折扇)
	
	public void onMonsterDeath(final AbstractMonster m) {//参数：m-死亡的敌人。
    }//触发时机：当一名敌人死亡时。
	//m.可调用敌人的信息。
	
	public int onPlayerGainedBlock(float blockAmount) {
		return super.onPlayerGainedBlock(blockAmount);
	}
	
	public int onPlayerHeal(final int healAmount) {
        return healAmount;
    }//触发时机：当玩家回复生命值时，返回生命值回复数量，可用来修改生命值回复数量。
	
	public void onEnterRestRoom() {
    }//触发时机：当玩家进入篝火时。
	
	public void onRest() {
    }//触发时机：当玩家在篝火内休息时。
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {//参数： info-伤害信息，damageAmount-伤害数值，target-伤害目标
    }//触发时机：当玩家攻击时。info.可调用伤害信息。
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {//参数：info-伤害信息，damageAmount-伤害数值
        return damageAmount;//该damaeAmount为未被格挡的伤害（参考鸟居）。
    }//触发时机：当玩家被攻击时，返回伤害数值，可用来修改伤害数值。info.可调用伤害信息。
	
	//伤害信息：info.owner (该次伤害的攻击者) info.type(该次伤害的种类，可利用info.type.调用伤害种类)
    //伤害种类:DamageInfo.DamageType.HP_LOSS (失去生命，无法被格挡，无法触发原版的【受到伤害时】的条件)  DamageInfo.DamageType.NORMAL (一般伤害，可以被格挡，能触发原版的【受到伤害时】的条件)  DamageInfo.DamageType.THORNS (荆棘伤害，可以被格挡，无法触发原版的【受到伤害时】的条件)
	
	public void onEnterRoom(final AbstractRoom room) {//参数：room-进入的房间。
    }//触发时机：当玩家进入房间时。(参考永恒羽毛)
	//room.可调用房间信息。
	
	public void onChestOpen(final boolean bossChest) {//参数：bossChest-是否为boss宝箱，true是boss宝箱，false不是boss宝箱。
    }//触发时机：当玩家打开一个箱子时。(详参遗物黑星、套娃、诅咒钥匙)
	
	public void onDrawOrDiscard() {
    }//触发时机：当玩家抽卡或者弃卡时。
	
	public void onLoseHp(int damageAmount) {
	}//当玩家失去生命时

}