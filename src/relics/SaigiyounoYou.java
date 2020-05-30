package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mymod.TestMod;

public class SaigiyounoYou extends MyRelic {
	public static final String ID = "SaigiyounoYou";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当你对敌方造成伤害时，回复 #b1 点生命。";
	
	public SaigiyounoYou() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature target) {
		if (this.isActive && (target == null || !target.isPlayer) && damage > 0)
			AbstractDungeon.player.heal(1);
	}
	
}