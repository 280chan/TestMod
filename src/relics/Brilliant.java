package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Brilliant extends AbstractTestRelic {
	public static final String ID = "Brilliant";
	
	public Brilliant() {
		super(ID, RelicTier.RARE, LandingSound.SOLID);
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
	
	private void applyDamage() {
		int[] dmg = DamageInfo.createDamageMatrix(damageFunction(AbstractDungeon.player.gold), true);
		this.addToBot(new DamageAllEnemiesAction(null, dmg, DamageType.THORNS, AttackEffect.BLUNT_LIGHT));
	}
	
	public void atBattleStart() {
		applyDamage();
		this.show();
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead() || AbstractDungeon.getCurrRoom().monsters.areMonstersDead())
			return;
		applyDamage();
		this.show();
    }

}