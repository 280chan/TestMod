package deprecated.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import actions.TriggerFirstTurnPostDrawPowersAction;
import deprecated.powers.LackOfCardPower;
import mymod.TestMod;
import relics.MyRelic;
import utils.MiscMethods;

/**
 * @deprecated
 */
public class LackOfCard extends MyRelic implements MiscMethods {
	public static final String ID = "LackOfCard";
	public static final String IMG = TestMod.cardIMGPath(ID);
	public static final String DESCRIPTION = "回合开始时，如果手牌数不超过当前能量，额外抽 #b2 张牌，否则下一回合开始获得 [R] 。";//遗物效果的文本描叙。
	private static final int DRAW = 2;
	
	private static boolean firstTurn = false;
	
	public LackOfCard() {
		super(ID, RelicTier.BOSS, LandingSound.FLAT);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player != null) {
			return setDescription(AbstractDungeon.player.chosenClass);
		}
		return setDescription(null);
	}
	
	private String setDescription(PlayerClass c) {
		return setDescription(c, DESCRIPTIONS);
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, setDescription(c)));
	    initializeTips();
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new LackOfCardPower(p, DRAW), DRAW));
		firstTurn = true;
	}

	public void atTurnStartPostDraw() {
		if (!this.isActive || !firstTurn)
			return;
		firstTurn = false;
		AbstractDungeon.actionManager.addToBottom(new TriggerFirstTurnPostDrawPowersAction());
	}
	
}