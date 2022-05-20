package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;

public class AscensionHeartUp extends AbstractUpgradedRelic implements OnPlayerDeathRelic {
	public static final String SAVE_SIZE = "AHUpRevivedpeak";
	private static final String SAVE_NAME = "AHUpRevived";
	private boolean revived = false;
	private static boolean looping = false;
	private static String desc27 = " Ethereal.";
	private static int offset = 0;
	private static int peak = 0;
	
	public static void reset() {
		offset = 0;
		MISC.relicStream(AscensionHeartUp.class).forEach(AscensionHeartUp::reset);
		MISC.getIdenticalList((AscensionHeartUp) null, peak - offset).forEach(AscensionHeartUp::reset);
	}
	
	private static void reset(AscensionHeartUp r) {
		TestMod.save(SAVE_NAME + offset++, r == null ? false : (r.revived = false));
	}
	
	public static void load(int size) {
		peak = size;
		offset = 0;
		MISC.relicStream(AscensionHeartUp.class).forEach(AscensionHeartUp::load);
	}
	
	private static void load(AscensionHeartUp r) {
		r.revived = TestMod.getBool(SAVE_NAME + offset++);
		r.updateDescription();
	}
	
	private static void save() {
		offset = 0;
		MISC.relicStream(AscensionHeartUp.class).forEach(AscensionHeartUp::save);
		TestMod.save(SAVE_SIZE, peak = Math.max(peak, offset));
	}
	
	private static void save(AscensionHeartUp r) {
		TestMod.save(SAVE_NAME + offset++, r.revived);
	}
	
	public AscensionHeartUp() {
		super(RelicTier.SPECIAL, LandingSound.HEAVY);
		if (DESCRIPTIONS.length >= 28)
			desc27 = DESCRIPTIONS[27];
	}
	
	public String getUpdatedDescription() {
		if (!isObtained)
			return DESCRIPTIONS[0];
		String tmp = getNaturalNumberList(Math.min(counter, 25)).stream()
				.map(i -> " NL " + (i + 1) + "." + DESCRIPTIONS[i == 19 && revived ? 26 : i + 1])
				.reduce(DESCRIPTIONS[0], String::concat);
		return checkLevel(26) ? tmp.concat(" NL INF." + DESCRIPTIONS[28]) : tmp;
	}
	
	public boolean checkLevel(int level) {
		return this.counter >= level;
	}
	
	private boolean checkDefenceDown(AbstractMonster m, boolean preBattle) {
		if (!preBattle && this.hasDefenceDownPower(m))
			return false;
		return m.type.ordinal() < 3 && checkLevel(m.type.ordinal() + 2);
	}
	
	private boolean checkReceiveDamage(AbstractMonster m) {
		return m.type.ordinal() < 3 && checkLevel(m.type.ordinal() + 7);
	}
	
	private boolean checkLoseStrength(AbstractMonster m) {
		return m.type.ordinal() < 3 && checkLevel(m.type.ordinal() + 17);
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
			int tmp = (int) relicStream(AscensionHeartUp.class).count();
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
			p().gainGold((int) (10 * relicStream(AscensionHeartUp.class).count()));
		}
	}
	
	private static boolean rollUpgrade() {
		return AbstractDungeon.cardRng.randomBoolean(0.25f);
	}
	
	public void onPlayerEndTurn() {
		if (this.isActive && checkLevel(24))
			p().hand.group.stream().filter(c -> c.type == CardType.STATUS).forEach(c -> c.exhaust = true);
		else if (this.isActive && checkLevel(10))
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
			p().gainGold((int) (10 * relicStream(AscensionHeartUp.class).count()));
		}
		looping = false;
	}
	
	public void onEquip() {
		this.initCounter();
		this.updateDescription();
    }
	
	public void initCounter() {
		this.counter = Math.max(AbstractDungeon.ascensionLevel, 0) + 25;
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
					m.powers.add(new DefenceDownUpPower(m, 10));
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
				.forEach(m -> m.powers.add(new DefenceDownUpPower(m, 10)));
		if (checkLevel(25)) {
			int e = (int) (p().relics.stream().filter(r -> r.tier == RelicTier.RARE).count()
					* relicStream(AscensionHeartUp.class).count());
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
				room.rewards.add(new RewardItem(50));
			if (checkLevel(5))
				p().increaseMaxHp(5, true);
		}
		if (checkLevel(26)) {
			room.rewards.add(new RewardItem(this.counter));
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
			p().powers.add(new EventHalfDamageUpPower(p()));
		} else if (hasEventPower()) {
			p().powers.remove(getEventPower());
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
	

	public boolean hasEventPower() {
		return p().powers.stream().anyMatch(p -> p instanceof EventHalfDamageUpPower);
	}
	
	public AbstractPower getEventPower() {
		return p().powers.stream().filter(p -> p instanceof EventHalfDamageUpPower).findAny().orElse(null);
	}
	
	private class EventHalfDamageUpPower extends AbstractTestPower implements InvisiblePower {
		
		public EventHalfDamageUpPower(AbstractCreature owner) {
			this.owner = owner;
			this.amount = -1;
			updateDescription();
			this.type = PowerType.BUFF;
		}
		
		public void updateDescription() {
			 this.description = "";
		}
		
		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
		}
		
		private float changeDmg(float damage) {
			return damage / 2f;
		}
		
		public int onLoseHp(int damage) {
			return AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof EventRoom
					&& AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT && checkLevel(15)
							? chain(relicStream(AscensionHeartUp.class).map(r -> get(this::changeDmg)))
									.apply(damage * 1f).intValue()
							: damage;
		}

	}
	
	public boolean hasDefenceDownPower(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof DefenceDownUpPower);
	}
	
	private class DefenceDownUpPower extends AbstractTestPower {
		public DefenceDownUpPower(AbstractCreature owner, int amount) {
			this.img = ImageMaster.loadImage(TestMod.powerIMGPath("DefenceDownPower"));
			this.owner = owner;
			this.amount = amount;
			updateDescription();
			this.type = PowerType.DEBUFF;
		}
		
		public void updateDescription() {
			 this.description = desc(0) + (single() ? amount : (dmgRate(100f) - 100)) + desc(1);
		}
		
		private float dmg(float input) {
			return input * (100 + this.amount) / 100;
		}
		
		private float dmgRate(float input) {
			return chain(relicStream(AscensionHeartUp.class).map(r -> get(this::dmg))).apply(input);
		}
		
	    public float atDamageReceive(float damage, DamageType damageType) {
	        return dmgRate(damage);
	    }
	    
	    private boolean single() {
			return relicStream(AscensionHeartUp.class).count() == 1;
		}
	}
	
}