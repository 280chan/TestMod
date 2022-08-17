package testmod.relics;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.HappyFlower;
import com.megacrit.cardcrawl.relics.Lantern;

import testmod.actions.DreamHousePurgeCardAction;
import testmod.mymod.TestMod;
import testmod.relicsup.DreamHouseUp;

public class DreamHouse extends AbstractTestRelic {
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	
	public void onObtainCard(AbstractCard card) {
		if (this.isActive && card.rarity != CardRarity.COMMON && this.relicStream(DreamHouseUp.class).count() == 0) {
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
		if (this.isActive && !QUEUE.isEmpty()) {
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