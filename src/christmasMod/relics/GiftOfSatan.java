package christmasMod.relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.PoisonPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import christmasMod.mymod.ChristmasMod;
import christmasMod.utils.ChristmasMiscMethods;

public class GiftOfSatan extends AbstractClickRelic implements ChristmasMiscMethods {
	public static final String ID = "GiftOfSatan";
	public static final String DESCRIPTION = "每回合开始将 #b1 张随机的灾厄加入手牌。每有一个单位的当前生命以若干个 #b6 结尾，额外加入 #b6 的个数张。当有单位受到单次伤害以若干个 #b6 结尾，也将其个数张随机的灾厄加入手牌。第 #b6 回合开始，你将获得升级后的灾厄，打出灾厄时将其 #y消耗 。战斗结束时右击此遗物，有 #b25% 几率有机会获得一次额外的灾厄选牌。";
	
	private boolean victory = false;
	
	public GiftOfSatan() {
		super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private static int checkNum(int input) {
		if (input % 10 == 6)
			return 1 + checkNum(input / 10);
		else
			return 0;
	}
	
	private void addRandomDisaster(int num) {
		for (int i = 0; i < num; i++)
			this.addToBot(new MakeTempCardInHandAction(ChristmasMod.randomDisaster(this.counter >= 6)));
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
		for (AbstractCard c : retVal2) {
			if ((RNGTools.cardRNG.randomBoolean(cardUpgradedChance))) {
				c.upgrade();
			} else if ((c.type == AbstractCard.CardType.ATTACK) && (player.hasRelic("Molten Egg 2"))) {
				c.upgrade();
			} else if ((c.type == AbstractCard.CardType.SKILL) && (player.hasRelic("Toxic Egg 2"))) {
				c.upgrade();
			} else if ((c.type == AbstractCard.CardType.POWER) && (player.hasRelic("Frozen Egg 2"))) {
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
	protected void onRightClick() {
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
		int extra = checkNum(AbstractDungeon.player.currentHealth);
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			extra += checkNum(m.currentHealth);
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
		for (AbstractCard d : ChristmasMod.DISASTERS)
			if (c.cardID.equals(d.cardID))
				return true;
		return false;
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (isDisaster(c) && this.counter >= 6) {
			action.exhaustCard = true;
		}
	}
	
}