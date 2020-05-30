package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import actions.MagicalMalletAction;
import mymod.TestMod;

public class MagicalMallet extends MyRelic{
	public static final String ID = "MagicalMallet";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每回合开始，记录手牌中的所有牌的耗能的最小值和最大值。将你手牌中所有耗能为这两个值的牌的耗能在本场战斗中变成另一个值。";//遗物效果的文本描叙。
	
	public MagicalMallet() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.SHOP, LandingSound.MAGICAL);
		//参数：ID-遗物Id，new Texture(Gdx.files.internal(IMG))-遗物图片，new Texture(Gdx.files.internal(OUTLINE))-遗物轮廓，RelicTier.BOSS-遗物种类，LandingSound.FLAT-遗物音效。
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		if (this.isActive)
			this.addToBot(new MagicalMalletAction(this, AbstractDungeon.player.hand.group));
    }
	
}