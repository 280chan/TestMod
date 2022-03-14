package testmod.relics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import testmod.mymod.TestMod;
import testmod.powers.DefenceDownPower;
import testmod.powers.EventHalfDamagePower;

public class AscensionHeart extends AbstractTestRelic implements OnPlayerDeathRelic {
	public static final String SAVE_SIZE = "AHRevivedpeak";
	private static final String SAVE_NAME = "AHRevived";
	private boolean revived = false;
	private static boolean looping = false;
	private static String desc27 = " Ethereal.";
	private static int offset = 0;
	private static int peak = 0;
	
	public static void reset() {
		offset = 0;
		INSTANCE.relicStream(AscensionHeart.class).forEach(AscensionHeart::reset);
		INSTANCE.getIdenticalList((AscensionHeart) null, peak - offset).forEach(AscensionHeart::reset);
	}
	
	private static void reset(AscensionHeart r) {
		TestMod.save(SAVE_NAME + offset++, r == null ? false : (r.revived = false));
	}
	
	public static void load(int size) {
		peak = size;
		offset = 0;
		INSTANCE.relicStream(AscensionHeart.class).forEach(AscensionHeart::load);
	}
	
	private static void load(AscensionHeart r) {
		r.revived = TestMod.getBool(SAVE_NAME + offset++);
		r.updateDescription();
	}
	
	private static void save() {
		offset = 0;
		INSTANCE.relicStream(AscensionHeart.class).forEach(AscensionHeart::save);
		TestMod.save(SAVE_SIZE, peak = Math.max(peak, offset));
	}
	
	private static void save(AscensionHeart r) {
		TestMod.save(SAVE_NAME + offset++, r.revived);
	}
	
	public AscensionHeart() {
		super(RelicTier.SPECIAL, LandingSound.HEAVY);
		if (DESCRIPTIONS.length == 28)
			desc27 = DESCRIPTIONS[27];
	}
	
	public String getUpdatedDescription() {
		if (!isObtained)
			return DESCRIPTIONS[0];
		return getNaturalNumberList(Math.min(counter, 25)).stream()
				.map(i -> " NL " + (i + 1) + "." + DESCRIPTIONS[i == 19 && revived ? 26 : i + 1])
				.reduce(DESCRIPTIONS[0], String::concat);
	}
	
	public boolean checkLevel(int level) {
		return this.counter >= level;
	}
	
	private boolean checkDefenceDown(AbstractMonster m, boolean preBattle) {
		if (!preBattle && DefenceDownPower.hasThis(m))
			return false;
		return m.type.ordinal() < 3 ? checkLevel(m.type.ordinal() + 2) : false;
	}
	
	private boolean checkReceiveDamage(AbstractMonster m) {
		return m.type.ordinal() < 3 ? checkLevel(m.type.ordinal() + 7) : false;
	}
	
	private boolean checkLoseStrength(AbstractMonster m) {
		return m.type.ordinal() < 3 ? checkLevel(m.type.ordinal() + 17) : false;
	}
	
	public void onObtainCard(AbstractCard card) {
		if (!this.isActive)
			return;
		if (card.type == CardType.CURSE && checkLevel(10)) {
			if (!card.isEthereal) {
				card.rawDescription += desc27;
				card.initializeDescription();
				card.isEthereal = true;
			}
		} else if (card.type != CardType.STATUS && checkLevel(12)) {
			int tmp = (int) relicStream(AscensionHeart.class).count();
			while (card.canUpgrade() && tmp > 0) {
				tmp--;
				if (rollUpgrade())
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
			p().gainGold((int) (10 * relicStream(AscensionHeart.class).count()));
		}
	}
	
	private static boolean rollUpgrade() {
		return AbstractDungeon.cardRng.randomBoolean(0.25f);
	}
	
	public void onPlayerEndTurn() {
		if (this.isActive && checkLevel(10))
			p().hand.group.stream().filter(c -> c.type == CardType.CURSE).forEach(c -> c.exhaust = true);
    }
	
	private void setEthereal(AbstractCard c) {
		c.rawDescription += desc27;
		c.initializeDescription();
		c.isEthereal = true;
	}
	
	private boolean checkAddEthereal(AbstractCard c) {
		return !c.isEthereal && ((c.type == CardType.CURSE && checkLevel(10))
				|| (c.type == CardType.STATUS && checkLevel(24)));
	}
	
	public void onRefreshHand() {
		if (this.isActive)
			p().hand.group.stream().filter(this::checkAddEthereal).forEach(this::setEthereal);
	}
	
	public void onSpendGold() {
		if (this.isActive && checkLevel(16) && !looping) {
			looping = true;
			p().gainGold((int) (10 * relicStream(AscensionHeart.class).count()));
		}
		looping = false;
	}
	
	public void onEquip() {
		this.initCounter();
		this.updateDescription(p().chosenClass);
    }
	
	public void initCounter() {
		this.counter = AbstractDungeon.ascensionLevel;
		if (this.counter <= 0)
			this.counter = -1;
	}
	
	private boolean alive(AbstractMonster m) {
		return !(m.isDead || m.isDying || m.halfDead || m.isEscaping || m.escaped);
	}
	
	public void atPreBattle() {
		if (checkLevel(6)) {
			p().heal(1, true);
		}
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && !m.isEscaping && !m.escaped) {
				if (this.isActive && checkDefenceDown(m, true))
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
						} else {
							m.getPower("Strength").updateDescription();
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
		if (!this.isActive)
			return;
		AbstractDungeon.getMonsters().monsters.stream().filter(this::alive).filter(m -> checkDefenceDown(m, false))
				.forEach(m -> m.powers.add(new DefenceDownPower(m, 10)));
		if (checkLevel(25)) {
			int e = (int) (p().relics.stream().filter(r -> r.tier == RelicTier.RARE).count()
					* relicStream(AscensionHeart.class).count());
			if (e > 2)
				this.addToBot(new GainEnergyAction(e / 3));
		}
    }
	
	public void onVictory() {
		AbstractRoom room = AbstractDungeon.getCurrRoom();
		if (room instanceof MonsterRoomElite) {
			if (checkLevel(14))
				p().increaseMaxHp(1, true);
			if (checkLevel(1))
				startEliteSwarm();
		}
		if (room instanceof MonsterRoomBoss) {
			if (checkLevel(13))
				room.addGoldToRewards(50);
			if (checkLevel(5))
				p().increaseMaxHp(5, true);
		}
    }
	
	private static boolean eliteSwarm = false;
	
	private void updateChangeCard() {
		if (!(this.isActive && eliteSwarm && checkNumCards() && AbstractDungeon.screen == CurrentScreen.COMBAT_REWARD))
			return;
		AbstractDungeon.combatRewardScreen.rewards.stream().filter(r -> r.type == RewardType.CARD).forEach(r -> {
			int size = r.cards.size();
			r.cards = r.cards.stream().filter(c -> c.rarity == CardRarity.RARE).collect(toArrayList());
			ArrayList<AbstractCard> pool = AbstractDungeon.srcRareCardPool.group.stream()
					.filter(b -> r.cards.stream().noneMatch(a -> b.cardID.equals(a.cardID))).collect(toArrayList());
			Collections.shuffle(pool, new Random(AbstractDungeon.cardRng.randomLong()));
			pool.stream().limit(size - r.cards.size()).forEach(r.cards::add);
		});
	}
	
	public void update() {
		super.update();
		updateChangeCard();
	}
	
	private boolean checkNumCards() {
		return p().relics.stream().map(r -> get(r::changeNumberOfCardsInReward)).reduce(a -> a, this::chain)
				.apply(3) > (ModHelper.isModEnabled("Binary") ? 1 : 0);
	}
	
	private static void startEliteSwarm() {
		eliteSwarm = true;
	}
	
	private static void stopEliteSwarm() {
		eliteSwarm = false;
	}
	
	@Override
	public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
		if (checkLevel(20) && !this.revived && !p().hasRelic("Mark of the Bloom")) {
			this.revived = true;
			save();
			this.updateDescription();
			p().heal(p().maxHealth, true);
			return p().currentHealth < 1;
		}
		return true;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!this.isActive)
			return;
		if (room instanceof EventRoom) {
			p().powers.add(new EventHalfDamagePower(p(), this));
		} else if (EventHalfDamagePower.hasThis()) {
			p().powers.remove(EventHalfDamagePower.getThis());
		}
		if (eliteSwarm) {
			stopEliteSwarm();
		}
    }
	
	public void onUsePotion() {
		if (checkLevel(11))
			p().heal(3, true);
	}
	
	public void onLoseHp(int damageAmount) {
		if (checkLevel(22)) {
			p().gainGold(10);
		}
	}
	
	public int onPlayerHeal(int healAmount) {
		if (healAmount > 0 && checkLevel(23) && AbstractDungeon.miscRng.randomBoolean()) {
			this.show();
			return 2 * healAmount;
		}
		return healAmount;
	}
	
}