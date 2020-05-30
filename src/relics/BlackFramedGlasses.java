package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class BlackFramedGlasses extends AbstractRevivalRelicToModifyDamage {
	public static final String ID = "BlackFramedGlasses";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每回合使所有敌人失去 #b1 点生命，并给自己恢复 #b1 点生命。当受到大于等于生命上限的一半的伤害时，将其降为 #b1 点；如果该伤害来自自己，将其降为 #b0 点。";//遗物效果的文本描叙。
	
	public BlackFramedGlasses() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
		show();
		AbstractDungeon.player.heal(1);
		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(1, true), DamageInfo.DamageType.HP_LOSS, AttackEffect.POISON));
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (!this.isActive)
			return damageAmount;
		if (2 * damageAmount >= AbstractDungeon.player.maxHealth) {
        	show();
        	if (info.owner.isPlayer || info.owner == null) {
        		return 0;
        	} else {
        		return 1;
        	}
        }
		return damageAmount;
    }
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		if (2 * info.output >= p.maxHealth) {
			if (info.owner.isPlayer || info.owner == null)
				return 0;
			return 1;
		}
		return originalDamage;
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return 2 * damageAmount >= p.maxHealth;
	}
	
}