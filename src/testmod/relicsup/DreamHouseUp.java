package testmod.relicsup;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import testmod.actions.DreamHousePurgeCardAction;

public class DreamHouseUp extends AbstractUpgradedRelic {
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	
	
	public DreamHouseUp() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
		this.counter = 0;
	}
	
	public void onObtainCard(AbstractCard card) {
		if (card.type == CardType.CURSE || card.rarity == CardRarity.CURSE || card.rarity == CardRarity.RARE) {
			if (this.isActive)
				QUEUE.add(new DreamHousePurgeCardAction(card));
			this.counter++;
			this.addRandomKey();
		}
	}
	
	public void atTurnStart() {
		if (this.counter > 0) {
			this.counter--;
			this.atb(new DrawCardAction(1));
			this.atb(new GainEnergyAction(1));
			this.show();
		}
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void update() {
		super.update();
		if (this.isActive && !QUEUE.isEmpty()) {
			DreamHousePurgeCardAction action = QUEUE.get(0);
			if (!action.isDone) {
				action.update();
			} else {
				QUEUE.remove(0);
			}
		}
	}
	
}