package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;
import powers.InjuryResistancePower;

public class InjuryResistance extends MyRelic {
	public static final String ID = "InjuryResistance";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当受到伤害后，接下来受到的伤害的最终数值都将降低 #b1 。当通过此遗物完全格挡一次伤害后，数值重置。";//遗物效果的文本描叙。
	
	public InjuryResistance() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	private void addPower(AbstractPlayer p) {
		if (!p.hasPower("InjuryResistancePower")) {
			p.powers.add(new InjuryResistancePower(p, this));
		}
	}
	
	public void justEnteredRoom(AbstractRoom r)	{
		this.addPower(AbstractDungeon.player);
	}
	
	public void atPreBattle() {
		this.addPower(AbstractDungeon.player);
    }

}