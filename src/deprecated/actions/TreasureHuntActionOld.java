package deprecated.actions;

import java.util.ArrayList;
import java.util.Iterator;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.BaseMod;
import deprecated.cards.colorless.TreasureHunterOld;

/**
 * @deprecated
 */
public class TreasureHuntActionOld extends AbstractGameAction {

	private float startingDuration;
	private boolean toDeck;
	private boolean retrieveCard = false;
	private CardRewardScreen s;
	private TreasureHunterOld usedCard;

	public TreasureHuntActionOld(boolean toDeck, TreasureHunterOld usedCard) {
		this.amount = 3;
		this.toDeck = toDeck;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.s = AbstractDungeon.cardRewardScreen;
		this.usedCard = usedCard;
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

	private void increaseCost() {
		TreasureHunterOld source = usedCard.source;
		
		ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
		for (int i = 0; i < deck.size(); i++) {
			AbstractCard c = deck.get(i);
			if (c == source) {
				BaseMod.logger.info("找到相同");
				AbstractCard tmp = new TreasureHunterOld(c.cost + usedCard.magicNumber);
				if (c.upgraded) {
					tmp.upgrade();
				}
				deck.set(i, tmp);
				break;
			}
		}
	}
	
	@Override
	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			s.discoveryOpen();
			s.rewardGroup = cards();

			increaseCost();
			
			tickDuration();
			return;
		}
		if (!this.retrieveCard) {
			if (s.discoveryCard != null) {
				AbstractCard huntedCard = s.discoveryCard.makeStatEquivalentCopy();
				huntedCard.current_x = (-1000.0F * Settings.scale);
				
				if (AbstractDungeon.player.hand.size() < 10) {
					AbstractDungeon.effectList.add(
							new ShowCardAndAddToHandEffect(huntedCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				} else {
					AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(huntedCard, Settings.WIDTH / 2.0F,
							Settings.HEIGHT / 2.0F));
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
