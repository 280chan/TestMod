package relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GremlinBalance extends AbstractTestRelic {
	public static final String ID = "GremlinBalance";
	
	public GremlinBalance() {
		super(ID, RelicTier.BOSS, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atTurnStart() {
		int e = 0, c = 0, hp = AbstractDungeon.player.currentHealth;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (m.isDead || m.halfDead || m.escaped)
				continue;
			if (m.currentHealth <= hp)
				e++;
			if (m.currentHealth >= hp)
				c++;
		}
		if (e > 0)
			this.addToBot(new GainEnergyAction(e));
		if (c > 0)
			this.addToBot(new DrawCardAction(c));
    }

}