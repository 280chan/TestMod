package testmod.relics;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.mymod.TestMod;

public class RandomTest extends AbstractTestRelic {
	public static Color color = null;
	
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.color == CardColor.COLORLESS) {
			this.addTmpActionToBot(() -> {
				if (!p().hand.group.isEmpty()) {
					ArrayList<AbstractCard> list = p().hand.group.stream()
							.filter(c -> c.cost > -1 && c.costForTurn != 0 && !c.freeToPlay()).collect(toArrayList());
					reduceRandom(list.isEmpty() ? p().hand.group : list);
					this.show();
				}
			});
		}
	}
	
	private static void reduceRandom(ArrayList<AbstractCard> list) {
		reduceCost(list.size() == 1 ? list.get(0) : randomFrom(list));
	}
	
	private static void reduceCost(AbstractCard c) {
		c.setCostForTurn(Math.max(c.costForTurn - 1, 0));
	}
	
	private static AbstractCard randomFrom(ArrayList<AbstractCard> list) {
		return list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
	}
	
	public void atPreBattle() {
		AbstractCard c = randomFrom(TestMod.CARDS).makeCopy();
		this.atb(new MakeTempCardInHandAction(c));
		this.show();
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat())
			return;
		for (AbstractCard c : p().hand.group) {
			if (c.color == CardColor.COLORLESS && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster())) {
				this.addToGlowChangerList(c, color);
				active = true;
			} else
				this.removeFromGlowList(c, color);
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