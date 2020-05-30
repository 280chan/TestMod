package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import mymod.TestMod;
import utils.MiscMethods;

public class IncinerationGenerator extends MyRelic implements MiscMethods {
	public static final String ID = "IncinerationGenerator";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每回合开始获得 [R] ， #y消耗 一张牌。";//遗物效果的文本描叙。
	
	public IncinerationGenerator() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.HEAVY);
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
		AbstractDungeon.player.energy.energyMaster += 1;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster -= 1;
    }
	
	public void atTurnStartPostDraw() {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToBottom(new ExhaustAction(p, p, 1, false));
	}
	
}