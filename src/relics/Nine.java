package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class Nine extends AbstractRevivalRelicToModifyDamage {
	public static final String ID = "Nine";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每次受到致命伤害时无条件将生命上限数值降低 #b9 点，同时将此次伤害降为 #b1 。该遗物造成的生命上限降低不会被影响。";//遗物效果的文本描叙。
	
	public Nine() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		AbstractPlayer p = AbstractDungeon.player;
		if (damage >= p.currentHealth) {
			p.maxHealth -= 9;
			if (p.maxHealth < 1)
				p.maxHealth = 1;
			if (p.currentHealth > p.maxHealth)
				p.currentHealth = p.maxHealth;
			p.healthBarUpdatedEvent();
			return 1;
		}
		return damage;
    }

	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		return 1;
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		if (damageAmount >= p.currentHealth) {
			return true;
		}
		return false;
	}
	
}