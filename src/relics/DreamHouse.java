package relics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

public class DreamHouse extends MyRelic implements MiscMethods {
	public static final String ID = "DreamHouse";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "拾取时获得 #y开心小花 和 #y灯笼 。每回合开始获得 [R] 。你只能获得普通卡。";
	private static final AbstractRelic[] RELICS = { new HappyFlower(), new Lantern() };
	private static final ArrayList<DreamHousePurgeCardAction> QUEUE = new ArrayList<DreamHousePurgeCardAction>();
	
	public DreamHouse() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.HEAVY);
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
		MyRelic.setTryEquip(DreamHouse.class, false);
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
		MyRelic.setTryUnequip(DreamHouse.class, false);
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