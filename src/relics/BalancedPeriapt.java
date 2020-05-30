package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class BalancedPeriapt extends MyRelic {
	public static final String ID = "BalancedPeriapt";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "你的最大生命值不会再变化。当你尝试增加和减少最大生命值时，分别改为改变其数值 #b3 倍和 #b1 倍的生命。";//遗物效果的文本描叙。
	
	public BalancedPeriapt() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.UNCOMMON, LandingSound.FLAT);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public float preChangeMaxHP(float amount) {
		AbstractPlayer p = AbstractDungeon.player;
		if (p.isDead || p.isDying)
			return 0;
		float tmp = amount * 3;
		if (amount < 0) {
			p.damage(new DamageInfo(p, (int)(-amount), DamageType.HP_LOSS));
		} else if (amount > 0) {
			p.heal((int)tmp);
		}
    	return 0;
    }
	
}