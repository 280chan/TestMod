package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import mymod.TestMod;

public class Antiphasic extends MyRelic {
	public static final String ID = "Antiphasic";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当受到单次伤害数值不低于 #b25% 最大生命时，先增加 #b5 点生命上限再进行伤害结算。最大生命和受到伤害均为 #b1 时此遗物不生效。";//遗物效果的文本描叙。
	
	public Antiphasic() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {//参数：info-伤害信息，damageAmount-伤害数值
        if (damage >= AbstractDungeon.player.maxHealth / 4.0) {
        	if (damage == AbstractDungeon.player.maxHealth && damage == 1) {
        		return damage;
        	}
        	AbstractDungeon.player.increaseMaxHp(5, true);
        }
		return damage;
    }

}