package relics;

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
	
	public boolean checkLevel(int level) {
		return this.counter >= level;
	}
	
	private boolean checkDefenceDown(AbstractMonster m, boolean preBattle) {
		if (!preBattle && DefenceDownPower.hasThis(m))
			return false;
		switch (m.type) {
		case BOSS:
			return checkLevel(4);
		case ELITE:
			return checkLevel(3);
		case NORMAL:
			return checkLevel(2);
		}
		return false;
	}
	
	private boolean checkReceiveDamage(AbstractMonster m) {
		switch (m.type) {
		case BOSS:
			return checkLevel(9);
		case ELITE:
			return checkLevel(8);
		case NORMAL:
			return checkLevel(7);
		}
		return false;
	}
	
	private boolean checkLoseStrength(AbstractMonster m) {
		switch (m.type) {
		case BOSS:
			return checkLevel(19);
		case ELITE:
			return checkLevel(18);
		case NORMAL:
			return checkLevel(17);
		}
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
			p().gainGold(10);
		}
	}
	
	private static boolean rollUpgrade() {
		return AbstractDungeon.cardRng.randomBoolean(0.25f);
	}
	
	public void onPlayerEndTurn() {
		if (checkLevel(10))
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
		p().hand.group.stream().filter(this::checkAddEthereal).forEach(this::setEthereal);
	}
	
	public void onSpendGold() {
		if (checkLevel(16) && !looping) {
			looping = true;
			p().gainGold(10);
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
		this.updateDescription(p().chosenClass);
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
		AbstractDungeon.getMonsters().monsters.stream().filter(this::alive).filter(m -> checkDefenceDown(m, false))
				.forEach(m -> m.powers.add(new DefenceDownPower(m, 10)));
		if (checkLevel(25)) {
			int e = (int) p().relics.stream().filter(r -> r.tier == RelicTier.RARE).count();
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
	
	private static void updateChangeCard() {
		if (!(eliteSwarm && checkNumCards() && AbstractDungeon.screen == CurrentScreen.COMBAT_REWARD))
			return;
		AbstractDungeon.combatRewardScreen.rewards.stream().filter(r -> r.type == RewardType.CARD).forEach(r -> {
			int size = r.cards.size();
			r.cards = r.cards.stream().filter(c -> c.rarity == CardRarity.RARE).collect(INSTANCE.toArrayList());
			ArrayList<AbstractCard> pool = AbstractDungeon.srcRareCardPool.group.stream()
					.filter(b -> r.cards.stream().noneMatch(a -> b.cardID.equals(a.cardID)))
					.collect(INSTANCE.toArrayList());
			Collections.shuffle(pool, new Random(AbstractDungeon.cardRng.randomLong()));
			pool.stream().limit(size - r.cards.size()).forEach(r.cards::add);
		});
	}
	
	public void update() {
		super.update();
		updateChangeCard();
	}
	
	private static boolean checkNumCards() {
		AbstractPlayer p = INSTANCE.p();
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
		if (checkLevel(20) && !revived && !p().hasRelic("Mark of the Bloom")) {
			revived = true;
			save();
			this.updateDescription();
			p().heal(p().maxHealth, true);
			return p().currentHealth < 1;
		}
		return true;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
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
		if (checkLevel(23) && AbstractDungeon.miscRng.randomBoolean()) {
			this.show();
			return 2 * healAmount;
		}
		return healAmount;
	}
	
}