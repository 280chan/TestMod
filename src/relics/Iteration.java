package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class Iteration extends MyRelic{
	
	public static final String ID = "Recursion";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "第一回合额外抽 #b5 张牌，之后的每一回合比前一回合少抽 #r1 张牌。每 #b10 回合重置。";//遗物效果的文本描叙。
	public static int baseHandSize;
	
	public Iteration() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		baseHandSize = p.gameHandSize;
		this.counter = 0;
		p.gameHandSize += 5;
    }//触发时机：每一场战斗（具体作用时机未知）
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		counter++;
		if (counter == 10) {
			counter = 0;
		}
		p.gameHandSize = baseHandSize + 5 - counter;
    }//触发时机：在玩家回合结束时。
	
}