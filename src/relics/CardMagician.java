package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import mymod.TestMod;
import utils.MiscMethods;

public class CardMagician extends MyRelic implements MiscMethods {
	public static final String ID = "CardMagician";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "在每回合开始获得 [R] ，并将你的弃牌堆洗入抽牌堆。";//遗物效果的文本描叙。
	
	public CardMagician() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player != null) {
			return setDescription(AbstractDungeon.player.chosenClass);
		}
		return setDescription(null);
	}

	private String setDescription(PlayerClass c) {
		return this.setDescription(c, this.DESCRIPTIONS[0], this.DESCRIPTIONS[1]);
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, setDescription(c)));
	    initializeTips();
	}

	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
	    if (!AbstractDungeon.player.discardPile.isEmpty())
	    	AbstractDungeon.actionManager.addToBottom(new EmptyDeckShuffleAction());
	    AbstractDungeon.actionManager.addToBottom(new ShuffleAction(AbstractDungeon.player.drawPile));
    }
	
}