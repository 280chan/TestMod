package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class Charity extends MyRelic{
	
	public static final String ID = "Charity";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每次获得 #y金币 数额减少 #b1 ，但增加 #b1 点最大生命。";//遗物效果的文本描叙。
	
	public Charity() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}//文本更新方法，当你修改了DESCRIPTION时，调用该方法。
	
	public void onGainGold() {
		if (!this.isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		p.gold--;
		p.increaseMaxHp(1, true);
		this.show();
    }
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
	
}