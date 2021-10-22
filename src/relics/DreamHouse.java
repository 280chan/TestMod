package relics;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.HappyFlower;
import com.megacrit.cardcrawl.relics.Lantern;
import actions.DreamHousePurgeCardAction;
import mymod.TestMod;

public class DreamHouse extends AbstractTestRelic {
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	
	public DreamHouse() {
		super(RelicTier.BOSS, LandingSound.HEAVY, BAD);
	}
	
	public void onObtainCard(AbstractCard card) {
		if (card.rarity != CardRarity.COMMON) {
			QUEUE.add(new DreamHousePurgeCardAction(card));
		}
	}
	
	private static void obtainRelic(AbstractRelic r) {
		TestMod.obtain(AbstractDungeon.player, r, true);
	}
	
	public static void equipAction() {
		AbstractTestRelic.setTryEquip(DreamHouse.class, false);
		Stream.of(new HappyFlower(), new Lantern()).forEach(DreamHouse::obtainRelic);
	}
	
	public void onEquip() {
		this.addEnergy();
		this.setTryEquip(true);
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void update() {
		super.update();
		if (!QUEUE.isEmpty()) {
			DreamHousePurgeCardAction action = QUEUE.get(0);
			if (!action.isDone) {
				action.update();
			} else {
				QUEUE.remove(0);
			}
		}
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}