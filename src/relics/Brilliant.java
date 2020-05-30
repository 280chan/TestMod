package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import mymod.TestMod;

public class Brilliant extends MyRelic {
	public static final String ID = "Brilliant";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每场战斗开始时和每当敌人死亡时，对所有敌人造成当前 #y金币 数量的平方的三次根的一半的伤害。";//遗物效果的文本描叙。
	
	public Brilliant() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private static int damageFunction(int gold) {
		return (int) (Math.cbrt(gold * gold) / 2);
	}
	
	private static void applyDamage() {
		int[] dmg = DamageInfo.createDamageMatrix(damageFunction(AbstractDungeon.player.gold), true);
		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(null, dmg, DamageType.THORNS, AttackEffect.BLUNT_LIGHT));
	}
	
	public void atBattleStart() {
		if (!this.isActive)
			return;
		applyDamage();
		this.show();
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (!this.isActive)
			return;
		applyDamage();
		this.show();
    }

}