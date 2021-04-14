package relics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import mymod.TestMod;
import utils.MiscMethods;

public class RandomTest extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "RandomTest";
	private static Color color = null;
	
	public RandomTest() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.color == CardColor.COLORLESS) {
			this.addToBot(new AbstractGameAction(){
				@Override
				public void update() {
					this.isDone = true;
					ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
					list.addAll(AbstractDungeon.player.hand.group);
					if (list.isEmpty())
						return;
					ArrayList<AbstractCard> toRemove = new ArrayList<AbstractCard>();
					for (AbstractCard c : list)
						if (c.cost == -2 || c.costForTurn == 0 || c.freeToPlayOnce)
							toRemove.add(c);
					if (toRemove.size() < list.size())
						for (AbstractCard c : toRemove)
							list.remove(c);
					if (list.size() == 1) {
						reduceCost(list.get(0));
					} else {
						reduceCost(randomFrom(list));
					}
				}});
		}
	}
	
	private static void reduceCost(AbstractCard c) {
		c.setCostForTurn(Math.max(c.costForTurn - 1, 0));
	}
	
	private static AbstractCard randomFrom(ArrayList<AbstractCard> list) {
		return list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
	}
	
	public void atPreBattle() {
		AbstractCard c = randomFrom(TestMod.CARDS).makeCopy();
		UnlockTracker.markCardAsSeen(c.cardID);
	    this.addToBot(new MakeTempCardInHandAction(c));
    }
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.canUpdateHandGlow())
			return;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c.color == CardColor.COLORLESS && c.hasEnoughEnergy()
					&& c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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