package relics;

import java.util.ArrayList;

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
import utils.MiscMethods;

public class DreamHouse extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "DreamHouse";
	private static final AbstractRelic[] RELICS = { new HappyFlower(), new Lantern() };
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	
	public DreamHouse() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player == null)
			return setDescription(null);
		return setDescription(AbstractDungeon.player.chosenClass);
	}
	
	private String setDescription(PlayerClass c) {
		return this.setDescription(c, this.DESCRIPTIONS[0], this.DESCRIPTIONS[1]);
	}
	  
	public void updateDescription(PlayerClass c) {
		this.description = setDescription(c);
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.description));
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
	
	public static void equipAction() {
		AbstractTestRelic.setTryEquip(DreamHouse.class, false);
		for (AbstractRelic r : RELICS) {
			TestMod.obtain(AbstractDungeon.player, r, false);
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		AbstractDungeon.player.energy.energyMaster++;
		this.setTryEquip(true);
    }

	public static void unequipAction() {
		AbstractTestRelic.setTryUnequip(DreamHouse.class, false);
		for (AbstractRelic r : RELICS) {
			AbstractDungeon.player.loseRelic(r.relicId);
			System.out.println("梦幻馆: 移除" + r.name);
		}
	}
	
	public void onUnequip() {
		if (!isActive)
			return;
		AbstractDungeon.player.energy.energyMaster--;
		this.setTryUnequip(true);
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
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}