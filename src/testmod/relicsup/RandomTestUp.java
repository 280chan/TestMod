package testmod.relicsup;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.mymod.TestMod;
import testmod.relics.RandomTest;

public class RandomTestUp extends AbstractUpgradedRelic {
	
	public RandomTestUp() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.color == CardColor.COLORLESS) {
			this.addTmpActionToBot(() -> {
				if (!p().hand.group.isEmpty()) {
					ArrayList<AbstractCard> list = p().hand.group.stream()
							.filter(c -> c.cost > -1 && c.costForTurn != 0 && !c.freeToPlayOnce).collect(toArrayList());
					reduceRandom(list.isEmpty() ? p().hand.group : list);
				}
			});
		}
	}
	
	private void reduceRandom(ArrayList<AbstractCard> list) {
		superFlash(list.size() == 1 ? list.get(0) : randomFrom(list)).freeToPlayOnce = true;
	}
	
	private AbstractCard randomFrom(ArrayList<AbstractCard> list) {
		return list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
	}
	
	private AbstractCard superFlash(AbstractCard c) {
		c.superFlash();
	    this.show();
		return c;
	}
	
	public void atPreBattle() {
		AbstractCard c = randomFrom(TestMod.CARDS).makeCopy();
		c.upgrade();
	    this.atb(new MakeTempCardInHandAction(c));
	    this.show();
    }
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			if (!p().hand.group.isEmpty()) {
				ArrayList<AbstractCard> list = p().hand.group.stream().filter(c -> c.color != CardColor.COLORLESS)
						.collect(toArrayList());
				if (!list.isEmpty()) {
					superFlash(randomFrom(list)).color = CardColor.COLORLESS;
				}
			}
		});
	}
	
	public void onRefreshHand() {
		if (RandomTest.color == null)
			RandomTest.color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat())
			return;
		for (AbstractCard c : p().hand.group) {
			if (c.color == CardColor.COLORLESS && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster())) {
				this.addToGlowChangerList(c, RandomTest.color);
				active = true;
			} else
				this.removeFromGlowList(c, RandomTest.color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}

}