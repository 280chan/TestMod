package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;
import powers.JusticePower;

public class Justice extends MyRelic {
	public static final String ID = "Justice";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当你获得负面状态时，获得 #b1  #y力量 。";//遗物效果的文本描叙。
	
	public Justice() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new JusticePower(p)));
    }
	
	public void atTurnStart() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		if (!p.hasPower(JusticePower.POWER_ID)) {
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new JusticePower(p)));
		}
    }
}