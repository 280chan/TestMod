package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.potions.PoisonPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import christmasMod.mymod.ChristmasMod;
import christmasMod.relics.GiftOfSatan;

public class GiftOfSatanUp extends AbstractUpgradedRelic implements ClickableRelic {
	
	private boolean victory = false;
	
	public GiftOfSatanUp() {
		super(GiftOfSatanUp.class, GiftOfSatan.class);
	}
	
	private static int checkNum(int input) {
		if (input < 1)
			return 0;
		return (input % 10 == 6 ? 1 : 0) + checkNum(input / 10);
	}
	
	private void addRandomDisaster(int num) {
		for (int i = 0; i < num; i++)
			this.atb(new MakeTempCardInHandAction(ChristmasMod.randomDisaster(true)));
	}
	
	private void toggleState(boolean victory) {
		this.victory = victory;
		if (victory) {
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}
	
	public void onVictory() {
		this.toggleState(true);
		this.counter = -1;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (this.victory) {
			this.toggleState(false);
		}
	}

	public int getRewardCardNum() {
		int numCards = 3;
		for (AbstractRelic r : p().relics) {
			numCards = r.changeNumberOfCardsInReward(numCards);
		}
		if (ModHelper.isModEnabled("Binary")) {
			numCards--;
		}
		return Math.max(numCards, 0);
	}
	
	private ArrayList<AbstractCard> randomReward() {
		ArrayList<AbstractCard> retVal = new ArrayList<AbstractCard>();
		retVal.addAll(ChristmasMod.DISASTERS);
		int numCards = this.getRewardCardNum();
		ArrayList<AbstractCard> retVal2 = new ArrayList<AbstractCard>();
		Random rng = new Random(AbstractDungeon.cardRng.copy().randomLong());
		Collections.shuffle(retVal, rng);
		if (retVal.size() > numCards) {
			retVal.stream().limit(numCards).forEach(retVal2::add);
			retVal.clear();
			retVal.addAll(retVal2);
			retVal2.clear();
		}
		while (retVal.size() < numCards) {
			retVal2.addAll(ChristmasMod.DISASTERS);
			Collections.shuffle(retVal2, rng);
			retVal2.stream().limit(numCards - retVal.size()).forEach(retVal::add);
			retVal2.clear();
		}
		retVal.stream().map(c -> c.makeCopy()).peek(retVal2::add).filter(c -> c.canUpgrade()).forEach(c -> c.upgrade());
		retVal.clear();
		return retVal2;
	}
	
	private void addReward() {
		RewardItem item = new RewardItem(new PoisonPotion());
		item.type = RewardType.CARD;
		item.potion = null;
		item.text = RewardItem.TEXT[2];
		item.cards = randomReward();
		AbstractDungeon.combatRewardScreen.rewards.add(item);
		AbstractDungeon.combatRewardScreen.positionRewards();
	}
	
	@Override
	public void onRightClick() {
		if (this.victory) {
			this.toggleState(false);
			this.addReward();
			this.flash();
		}
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void atTurnStart() {
		int extra = AbstractDungeon.getMonsters().monsters.stream().mapToInt(m -> checkNum(m.currentHealth)).sum()
				+ checkNum(p().currentHealth);
		addRandomDisaster(extra + 1);
	}
	
	public int onAttackedMonster(DamageInfo info, int damage) {
		addRandomDisaster(checkNum(damage));
		return damage;
	}
	
	public void onLoseHp(int hpLoss) {
		addRandomDisaster(checkNum(hpLoss));
	}
	
	private static boolean isDisaster(AbstractCard c) {
		return ChristmasMod.DISASTERS.stream().anyMatch(a -> a.cardID.equals(c.cardID));
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (isDisaster(c)) {
			this.counter++;
			action.exhaustCard = true;
			if (this.counter == 6)
				this.beginLongPulse();
			if (this.counter > 5) {
				if (c.freeToPlay() || c.isInAutoplay || c.cost < -1 || (c.cost == -1 && EnergyPanel.totalCount < 1)
						|| c.costForTurn == 0)
					return;
				this.atb(new GainEnergyAction(c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn));
			}
		}
	}
	
}