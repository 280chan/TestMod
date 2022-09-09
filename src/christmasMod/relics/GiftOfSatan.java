package christmasMod.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.potions.PoisonPotion;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import christmasMod.mymod.ChristmasMod;
import christmasMod.utils.ChristmasMiscMethods;
import testmod.relics.AbstractTestRelic;

public class GiftOfSatan extends AbstractTestRelic implements ChristmasMiscMethods, ClickableRelic {
	private boolean victory = false;
	
	private static int checkNum(int input) {
		if (input % 10 == 6)
			return 1 + checkNum(input / 10);
		else
			return 0;
	}
	
	private void addRandomDisaster(int num) {
		for (int i = 0; i < num; i++)
			this.atb(new MakeTempCardInHandAction(ChristmasMod.randomDisaster(this.counter >= 6)));
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
		if (this.counter > 0)
			this.toggleState(true);
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		if (this.victory) {
			this.toggleState(false);
		}
    }

	public int getRewardCardNum() {
		int numCards = 3;
		for (AbstractRelic r : AbstractDungeon.player.relics) {
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
		AbstractPlayer player = AbstractDungeon.player;
		float cardUpgradedChance = 0.25f * (AbstractDungeon.actNum - 1);
		if (AbstractDungeon.ascensionLevel >= 12)
			cardUpgradedChance /= 2f;
		if (cardUpgradedChance > 1)
			cardUpgradedChance = 1;
	    int numCards = this.getRewardCardNum();
	    ArrayList<AbstractCard> retVal2 = new ArrayList<AbstractCard>();
	    while (retVal2.size() < numCards) {
	    	if (retVal.isEmpty())
	    		retVal.addAll(ChristmasMod.DISASTERS);
	    	retVal2.add(retVal.remove((int) (this.cardRng() * retVal.size())).makeCopy());
	    }
	    Random r = AbstractDungeon.cardRng.copy();
		for (AbstractCard c : retVal2) {
			if ((c.type == AbstractCard.CardType.ATTACK) && (player.hasRelic("Molten Egg 2"))) {
				c.upgrade();
			} else if ((c.type == AbstractCard.CardType.SKILL) && (player.hasRelic("Toxic Egg 2"))) {
				c.upgrade();
			} else if ((c.type == AbstractCard.CardType.POWER) && (player.hasRelic("Frozen Egg 2"))) {
				c.upgrade();
			} else if (r.randomBoolean(cardUpgradedChance)) {
				c.upgrade();
			}
		}
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
			if (this.cardRng() < 0.25f) {
				this.addReward();
				this.flash();
			}
		}
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 6)
			this.beginLongPulse();
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
		if (isDisaster(c) && this.counter >= 6) {
			action.exhaustCard = true;
		}
	}
	
}