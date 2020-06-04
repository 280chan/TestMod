package relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import mymod.TestMod;
import powers.DefenceDownPower;
import powers.EventHalfDamagePower;

public class AscensionHeart extends MyRelic implements OnPlayerDeathRelic {
	public static final String ID = "AscensionHeart";
	public static final String DESCRIPTION = "依据 #y进阶等级 获得增益。";
	public static final String[] TEXT = new String[25];
	public static final String[] NEGATIVE_TEXT = new String[20];
	public static boolean updated = false;
	private static boolean initialized = false;
	private static boolean revived = false;
	
	private static boolean looping = false;
	
	public static void initialize() {
		if (initialized)
			return;
		initialized = true;
		int i = 0;
		TEXT[i++] = "精英敌人奖励变得更好";
		TEXT[i++] = "普通敌人受到伤害增加";
		TEXT[i++] = "精英敌人受到伤害增加";
		TEXT[i++] = "Boss敌人受到伤害增加";
		TEXT[i++] = "Boss战后增加生命上限";
		TEXT[i++] = "战斗开始时恢复生命";
		TEXT[i++] = "普通敌人初始受到伤害";
		TEXT[i++] = "精英敌人初始受到伤害";
		TEXT[i++] = "Boss敌人初始受到伤害";
		TEXT[i++] = "为所有诅咒增加虚无";
		TEXT[i++] = "使用药水后恢复生命";
		TEXT[i++] = "获得牌时概率将其升级";
		TEXT[i++] = "Boss变富";
		TEXT[i++] = "击败精英增加生命上限";
		TEXT[i++] = "事件中失去的生命减少";
		TEXT[i++] = "购买商品返还部分金币";
		TEXT[i++] = "普通敌人-1力量";
		TEXT[i++] = "精英敌人-1力量";
		TEXT[i++] = "Boss敌人-1力量";
		TEXT[i++] = "死亡时有1次复活机会";
		TEXT[i++] = "获得卡牌时获得金币";
		TEXT[i++] = "失去生命时获得金币";
		TEXT[i++] = "治疗效果一半概率翻倍";
		TEXT[i++] = "为所有状态增加虚无";
		TEXT[i++] = "稀有遗物提供额外能量";
		i = 0;
		NEGATIVE_TEXT[i++] = "精英敌人奖励变得更好";
		NEGATIVE_TEXT[i++] = "普通敌人受到伤害增加";
		NEGATIVE_TEXT[i++] = "精英敌人受到伤害增加";
		NEGATIVE_TEXT[i++] = "Boss敌人受到伤害增加";
		NEGATIVE_TEXT[i++] = "Boss战后增加生命上限";
		NEGATIVE_TEXT[i++] = "战斗开始时恢复生命";
		NEGATIVE_TEXT[i++] = "普通敌人初始受到伤害";
		NEGATIVE_TEXT[i++] = "精英敌人初始受到伤害";
		NEGATIVE_TEXT[i++] = "Boss敌人初始受到伤害";
		NEGATIVE_TEXT[i++] = "为所有诅咒增加虚无";
		NEGATIVE_TEXT[i++] = "使用药水后恢复生命";
		NEGATIVE_TEXT[i++] = "获得牌时概率将其升级";
		NEGATIVE_TEXT[i++] = "Boss变富";
		NEGATIVE_TEXT[i++] = "击败精英增加生命上限";
		NEGATIVE_TEXT[i++] = "事件中失去的生命减少";
		NEGATIVE_TEXT[i++] = "购买商品返还部分金币";
		NEGATIVE_TEXT[i++] = "普通敌人-1力量";
		NEGATIVE_TEXT[i++] = "精英敌人-1力量";
		NEGATIVE_TEXT[i++] = "Boss敌人-1力量";
		NEGATIVE_TEXT[i++] = "死亡时有1次复活机会";
		
	}

	
	public AscensionHeart() {
		super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		if (!isObtained)
			return DESCRIPTIONS[0];
		String retVal = DESCRIPTION;
		for (int i = 0; i < this.counter; i++)
			retVal += " NL " + (i + 1) + "." + TEXT[i];
		return retVal;
	}
	
	public void updateDescription(AbstractPlayer.PlayerClass c) {
		this.updateDescription();
	}
	
	private void updateDescription() {
		if (revived) {
			TEXT[19] = "(这个效果已用尽)";
			initialized = false;
		}
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, getUpdatedDescription()));
	    initializeTips();
	}
	
	public AbstractRelic makeCopy() {
		initialize();
		return new AscensionHeart();
	}
	
	public boolean checkLevel(int level) {
		return this.counter >= level;
	}
	
	private boolean checkDefenceDown(AbstractMonster m, boolean preBattle) {
		if (!preBattle && m.hasPower("DefenceDownPower"))
			return false;
		if (m.type == EnemyType.NORMAL && checkLevel(2))
			return true;
		if (m.type == EnemyType.ELITE && checkLevel(3))
			return true;
		if (m.type == EnemyType.BOSS && checkLevel(4))
			return true;
		return false;
	}
	
	private boolean checkReceiveDamage(AbstractMonster m) {
		if (m.type == EnemyType.NORMAL && checkLevel(7))
			return true;
		if (m.type == EnemyType.ELITE && checkLevel(8))
			return true;
		if (m.type == EnemyType.BOSS && checkLevel(9))
			return true;
		return false;
	}
	
	private boolean checkLoseStrength(AbstractMonster m) {
		if (m.type == EnemyType.NORMAL && checkLevel(17))
			return true;
		if (m.type == EnemyType.ELITE && checkLevel(18))
			return true;
		if (m.type == EnemyType.BOSS && checkLevel(19))
			return true;
		return false;
	}
	
	public void onObtainCard(AbstractCard card) {
		if (card.type == CardType.CURSE && checkLevel(10)) {
			if (!card.isEthereal) {
				card.rawDescription += " 虚无 。";
				card.initializeDescription();
				card.isEthereal = true;
			}
		} else if (card.type != CardType.STATUS && checkLevel(12)) {
			if (card.canUpgrade() && rollUpgrade()) {
				card.upgrade();
			}
		} else if (card.type == CardType.STATUS && checkLevel(24)) {
			if (!card.isEthereal) {
				card.rawDescription += " 虚无 。";
				card.initializeDescription();
				card.isEthereal = true;
			}
		}
		if (checkLevel(21)) {
			AbstractDungeon.player.gainGold(10);
		}
	}
	
	private static boolean rollUpgrade() {
		return AbstractDungeon.cardRng.randomBoolean(0.25f);
	}
	
	public void onPlayerEndTurn() {
		if (checkLevel(10)) {
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (c.type == CardType.CURSE) {
					c.exhaust = true;
				}
			}
		}
    }
	
	public void onRefreshHand() {
		if (checkLevel(10)) {
			for (AbstractCard card : AbstractDungeon.player.hand.group) {
				if (card.type == CardType.CURSE && !card.isEthereal) {
					card.rawDescription += " 虚无 。";
					card.initializeDescription();
					card.isEthereal = true;
				} else if (card.type == CardType.STATUS && !card.isEthereal && checkLevel(24)) {
					card.rawDescription += " 虚无 。";
					card.initializeDescription();
					card.isEthereal = true;
				}
			}
		}
	}
	
	public void onSpendGold() {
		if (checkLevel(16) && !looping) {
			looping = true;
			AbstractDungeon.player.gainGold(10);
		}
		looping = false;
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		initialize();
		this.counter = AbstractDungeon.ascensionLevel;
		if (this.counter == 0)
			this.counter = -1;
		this.updateDescription(AbstractDungeon.player.chosenClass);
    }
	
	public void atPreBattle() {
		if (checkLevel(6)) {
			AbstractDungeon.player.heal(1, true);
		}
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead) {
				if (checkDefenceDown(m, true))
					m.powers.add(new DefenceDownPower(m, 10));
				if (checkReceiveDamage(m)) {
					m.currentHealth -= m.maxHealth / 10;
					m.healthBarUpdatedEvent();
				}
				if (checkLoseStrength(m)) {
					if (m.hasPower("Strength")) {
						m.getPower("Strength").amount--;
						if (m.getPower("Strength").amount == 0) {
							m.powers.remove(m.getPower("Strength"));
						}
					} else {
						AbstractPower temp = new StrengthPower(m, -1);
						temp.updateDescription();
						m.powers.add(temp);
					}
				}
			}
		}
    }
	
	public void atTurnStart() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead) {
				if (checkDefenceDown(m, false))
					m.powers.add(new DefenceDownPower(m, 10));
			}
		}
		if (checkLevel(25)) {
			int e = 0;
			for (AbstractRelic r : AbstractDungeon.player.relics)
				if (r.tier == RelicTier.RARE)
					e++;
			if (e > 2)
				this.addToBot(new GainEnergyAction(e / 3));
		}
    }
	
	public void onVictory() {
		AbstractRoom room = AbstractDungeon.getCurrRoom();
		if (room instanceof MonsterRoomElite) {
			if (checkLevel(14))
				AbstractDungeon.player.increaseMaxHp(1, true);
			if (checkLevel(1))
				startEliteSwarm();
		}
		if (room instanceof MonsterRoomBoss) {
			if (checkLevel(13))
				room.addGoldToRewards(50);
			if (checkLevel(5))
				AbstractDungeon.player.increaseMaxHp(5, true);
		}
    }
	
	private static boolean eliteSwarm = false;
	
	private static void updateChangeCard() {
		if (eliteSwarm && checkNumCards() && AbstractDungeon.screen == CurrentScreen.COMBAT_REWARD) {
			for (RewardItem r : AbstractDungeon.combatRewardScreen.rewards) {
				if (r.type == RewardType.CARD) {
					for (int i = 0; i < r.cards.size(); i++) {
						AbstractCard c = r.cards.get(i);
						if (c.rarity == CardRarity.COMMON || c.rarity == CardRarity.UNCOMMON) {
							ArrayList<AbstractCard> pool = AbstractDungeon.srcRareCardPool.group;
							r.cards.set(i, pool.get(AbstractDungeon.cardRandomRng.random(pool.size() - 1)).makeCopy());
						}
					}
				}
			}
		}
	}
	
	public void update() {
		super.update();
		updateChangeCard();
	}
	
	private static boolean checkNumCards() {
		AbstractPlayer p = AbstractDungeon.player;
		return !p.hasRelic("Busted Crown") || !ModHelper.isModEnabled("Binary") || p.hasRelic("Question Card");
	}
	
	private static void startEliteSwarm() {
		eliteSwarm = true;
	}
	
	private static void stopEliteSwarm() {
		eliteSwarm = false;
	}
	
	@Override
	public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
		if (checkLevel(20) && !revived && !AbstractDungeon.player.hasRelic("Mark of the Bloom")) {
			revived = true;
			this.updateDescription();
			AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
			return false;
		}
		return true;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (room instanceof EventRoom) {
			AbstractDungeon.player.powers.add(new EventHalfDamagePower(AbstractDungeon.player, this));
		} else if (AbstractDungeon.player.hasPower("EventHalfDamagePower")) {
			AbstractDungeon.player.powers.remove(AbstractDungeon.player.getPower("EventHalfDamagePower"));
		}
		if (eliteSwarm) {
			stopEliteSwarm();
		}
    }
	
	public void onUsePotion() {
		if (checkLevel(11))
			AbstractDungeon.player.heal(3, true);
	}
	
	public void onLoseHp(int damageAmount) {
		if (checkLevel(22)) {
			AbstractDungeon.player.gainGold(10);
		}
	}
	
	public int onPlayerHeal(int healAmount) {
		if (checkLevel(23) && AbstractDungeon.miscRng.randomBoolean()) {
			this.show();
			return 2 * healAmount;
		}
		return healAmount;
	}
	
}