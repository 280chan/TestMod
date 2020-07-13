package actions;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import basemod.BaseMod;

public class TestPotionAction extends AbstractGameAction {
	private float startingDuration;
	private ArrayList<AbstractCard> list;
	private boolean retrieveCard = false;
	private CardRewardScreen s;

	public TestPotionAction(ArrayList<AbstractCard> list) {
		this.list = list;
		this.actionType = ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.s = AbstractDungeon.cardRewardScreen;
	}
	
	private ArrayList<AbstractCard> cards(ArrayList<AbstractCard> group, Random rng) {
		ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
		list.addAll(group);
		ArrayList<AbstractCard> derp = new ArrayList<AbstractCard>();
		while (derp.size() < 3) {
			derp.add(list.remove(rng.random(list.size() - 1)));
		}
		return derp;
	}
	
	@Override
	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			s.customCombatOpen(cards(this.list, AbstractDungeon.cardRandomRng), CardRewardScreen.TEXT[1], true);
			tickDuration();
			return;
		}
		if (!this.retrieveCard) {
			if (s.discoveryCard != null) {
				AbstractCard card = s.discoveryCard.makeCopy();
				card.setCostForTurn(0);
				card.current_x = (-1000.0F * Settings.scale);
				if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
					AbstractDungeon.effectList
							.add(new ShowCardAndAddToHandEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				} else {
					AbstractDungeon.effectList.add(
							new ShowCardAndAddToDiscardEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				}
				s.discoveryCard = null;
			}
			this.retrieveCard = true;
		}
		tickDuration();
	}

}
