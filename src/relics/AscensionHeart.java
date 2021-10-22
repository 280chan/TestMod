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
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import mymod.TestMod;
import powers.DefenceDownPower;
import powers.EventHalfDamagePower;

public class AscensionHeart extends AbstractTestRelic implements OnPlayerDeathRelic {
	public static final String SAVE_NAME = "AHRevived";
	private static boolean revived = false;
	private static boolean looping = false;
	private String desc27 = " Ethereal.";
	
	public static void reset() {
		revived = false;
		save();
	}
	
	public static void load(boolean loadValue) {
		revived = loadValue;
	}
	
	private static void save() {
		TestMod.save(SAVE_NAME, revived);
	}
	
	public AscensionHeart() {
		super(RelicTier.SPECIAL, LandingSound.HEAVY);
		if (DESCRIPTIONS.length == 28)
			desc27 = DESCRIPTIONS[27];
	}
	
	public String getUpdatedDescription() {
		if (!isObtained)
			return DESCRIPTIONS[0];
		String retVal = DESCRIPTIONS[0];
		for (int i = 0; i < this.counter; i++)
			retVal += " NL " + (i + 1) + "." + DESCRIPTIONS[i == 19 && revived ? 26 : i + 1];
		return retVal;
	}
	
	public void updateDescription(AbstractPlayer.PlayerClass c) {
		this.updateDescription();
	}
	
	private void updateDescription() {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, getUpdatedDescription()));
	    initializeTips();
	}
	
	public boolean checkLevel(int level) {
		return this.counter >= level;
	}
	
	private boolean checkDefenceDown(AbstractMonster m, boolean preBattle) {
		if (!preBattle && DefenceDownPower.hasThis(m))
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
				card.rawDescription += desc27;
				card.initializeDescription();
				card.isEthereal = true;
			}
		} else if (card.type != CardType.STATUS && checkLevel(12)) {
			if (card.canUpgrade() && rollUpgrade()) {
				card.upgrade();
			}
		} else if (card.type == CardType.STATUS && checkLevel(24)) {
			if (!card.isEthereal) {
				card.rawDescription += desc27;
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
		if (checkLevel(10))
			AbstractDungeon.player.hand.group.stream().filter(c -> c.type == CardType.CURSE).forEach(c -> {
				c.exhaust = true;
			});
    }
	
	private void setEthereal(AbstractCard c) {
		c.rawDescription += desc27;
		c.initializeDescription();
		c.isEthereal = true;
	}
	
	private boolean checkAddEthereal(AbstractCard c) {
		return (c.type == CardType.CURSE && !c.isEthereal && checkLevel(10))
				|| (c.type == CardType.STATUS && !c.isEthereal && checkLevel(24));
	}
	
	public void onRefreshHand() {
		AbstractDungeon.player.hand.group.stream().filter(this::checkAddEthereal).forEach(this::setEthereal);
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
		this.counter = AbstractDungeon.ascensionLevel;
		if (this.counter == 0)
			this.counter = -1;
		this.updateDescription(AbstractDungeon.player.chosenClass);
    }
	
	private boolean alive(AbstractMonster m) {
		return !(m.isDead || m.isDying || m.halfDead || m.isEscaping || m.escaped);
	}
	
	public void atPreBattle() {
		if (checkLevel(6)) {
			AbstractDungeon.player.heal(1, true);
		}
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && !m.isEscaping && !m.escaped) {
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
		AbstractDungeon.getCurrRoom().monsters.monsters.stream().filter(this::alive)
			.filter(m -> { return checkDefenceDown(m, false); })
			.forEach(m -> { m.powers.add(new DefenceDownPower(m, 10));});
		if (checkLevel(25)) {
			int e = (int) AbstractDungeon.player.relics.stream().filter(r -> {return r.tier == RelicTier.RARE;}).count();
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
			save();
			this.updateDescription();
			AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
			return AbstractDungeon.player.currentHealth < 1;
		}
		return true;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (room instanceof EventRoom) {
			AbstractDungeon.player.powers.add(new EventHalfDamagePower(AbstractDungeon.player, this));
		} else if (EventHalfDamagePower.hasThis()) {
			AbstractDungeon.player.powers.remove(EventHalfDamagePower.getThis());
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