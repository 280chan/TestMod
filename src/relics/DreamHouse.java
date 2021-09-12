package relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.HappyFlower;
import com.megacrit.cardcrawl.relics.Lantern;

import actions.DreamHousePurgeCardAction;
import mymod.TestMod;

public class DreamHouse extends AbstractTestRelic {
	public static final String ID = "DreamHouse";
	private static final AbstractRelic[] RELICS = { new HappyFlower(), new Lantern() };
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	
	public DreamHouse() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return this.DESCRIPTIONS[0];
	}
	  
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
		initializeTips();
	}
	
	public AbstractRelic makeCopy() {
		return new DreamHouse();
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
		Stream.of(RELICS).forEach(DreamHouse::obtainRelic);
	}
	
	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster++;
		this.setTryEquip(true);
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
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