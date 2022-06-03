package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import testmod.actions.DreamHousePurgeCardAction;

public class DreamHouseUp extends AbstractUpgradedRelic {
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	private static final ArrayList<ObtainKeyEffect.KeyColor> COLOR = new ArrayList<ObtainKeyEffect.KeyColor>();
	
	static {
		Stream.of(ObtainKeyEffect.KeyColor.values()).forEach(COLOR::add);
	}
	
	public DreamHouseUp() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
		this.counter = 0;
	}
	
	public void onObtainCard(AbstractCard card) {
		if (card.type == CardType.CURSE || card.rarity == CardRarity.CURSE || card.rarity == CardRarity.RARE) {
			if (this.isActive)
				QUEUE.add(new DreamHousePurgeCardAction(card));
			this.counter++;
			Collections.shuffle(COLOR, new Random(AbstractDungeon.miscRng.randomLong()));
			AbstractDungeon.topLevelEffectsQueue.add(new ObtainKeyEffect(COLOR.get(0)));
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