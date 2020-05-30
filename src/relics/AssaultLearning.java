package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import actions.AssaultLearningAction;
import mymod.TestMod;

public class AssaultLearning extends MyRelic{
	public static final String ID = "AssaultLearning";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "你的回合开始时，将你的抽牌堆顶部第一张可 #y升级 的牌在本场战斗中 #y升级 。";

	public AssaultLearning() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		if (this.isActive)
			AbstractDungeon.actionManager.addToBottom(new AssaultLearningAction(this));
    }
	
}