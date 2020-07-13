package actions;

import java.util.ArrayList;
import java.util.Iterator;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.BaseMod;

public class TreasureHuntAction extends AbstractGameAction {
	private float startingDuration;
	private boolean toHand;
	private boolean toDeck;
	private boolean retrieveCard = false;
	private CardRewardScreen s;

	public TreasureHuntAction(boolean toHand, boolean toDeck) {
		this.amount = 3;
		this.toHand = toHand;
		this.toDeck = toDeck;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.s = AbstractDungeon.cardRewardScreen;
	}

	private ArrayList<AbstractCard> cards() {
		ArrayList<AbstractCard> derp = new ArrayList<AbstractCard>();
		float deltaX = AbstractCard.IMG_WIDTH + 40.0F * Settings.scale;
		AbstractCard tmp;
		while (derp.size() != 3) {
			boolean dupe = false;
			tmp = AbstractDungeon.getCard(CardRarity.RARE, AbstractDungeon.cardRandomRng).makeCopy();
			Iterator<AbstractCard> var5 = derp.iterator();

			while (var5.hasNext()) {
				AbstractCard c = (AbstractCard) var5.next();
				if (c.cardID.equals(tmp.cardID)) {
					dupe = true;
					break;
				}
			}

			if (!dupe) {
				tmp.target_x = Settings.WIDTH / 2.0F + (derp.size() - 1) * deltaX;
				tmp.target_y = Settings.HEIGHT * 0.45F;
				derp.add(tmp);
				UnlockTracker.markCardAsSeen(tmp.cardID);
			}
		}
		return derp;
	}

	private void checkEggs(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		boolean hasEgg = false;
		if (c.type == CardType.ATTACK && p.hasRelic("Molten Egg 2")) {
			hasEgg = true;
		} else if (c.type == CardType.POWER && p.hasRelic("Frozen Egg 2")) {
			hasEgg = true;
		} else if (c.type == CardType.SKILL && p.hasRelic("Toxic Egg 2")) {
			hasEgg = true;
		}
		if (hasEgg) {
			c.upgrade();
		}
	}
	
	@Override
	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			s.customCombatOpen(cards(), CardRewardScreen.TEXT[1], false);
			tickDuration();
			return;
		}
		if (!this.retrieveCard) {
			if (s.discoveryCard != null) {
				AbstractCard huntedCard = s.discoveryCard.makeStatEquivalentCopy();
				huntedCard.current_x = (-1000.0F * Settings.scale);
				
				if (toHand) {
					checkEggs(huntedCard);
					if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
						AbstractDungeon.effectList.add(
								new ShowCardAndAddToHandEffect(huntedCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					} else {
						AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(huntedCard, Settings.WIDTH / 2.0F,
								Settings.HEIGHT / 2.0F));
					}
				}
				if (toDeck) {
					AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(huntedCard.makeCopy(),
							Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				}
				s.discoveryCard = null;
			}
			this.retrieveCard = true;
		}
		tickDuration();
	}

}
