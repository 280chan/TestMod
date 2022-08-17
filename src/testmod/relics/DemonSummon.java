package testmod.relics;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DemonFormPower;
import testmod.powers.AbstractTestPower;

public class DemonSummon extends AbstractTestRelic {
	
	public void onEquip() {
		if (this.inCombat())
			this.counter = GameActionManager.turn;
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
	public void atTurnStartPostDraw() {
		this.counter++;
		this.att(this.apply(this.p(), demon(p(), this.counter, this.counter)));
		this.show();
    }
	
	public static AbstractPower demon(AbstractCreature owner, int amount, int version) {
		return version <= 1 ? new DemonFormPower(owner, amount) : new DemonSummonPower(owner, amount, version);
	}

	public static class DemonSummonPower extends AbstractTestPower {
		private static final String DEMON = CardCrawlGame.languagePack.getPowerStrings(DemonFormPower.POWER_ID).NAME;
		public int version;
		
		public DemonSummonPower(AbstractCreature owner, int amount, int version) {
			this.ID += version;
			this.name += version;
			this.setRegion("demonForm");
			this.owner = owner;
			this.version = version;
			this.amount = amount;
			updateDescription();
			this.type = PowerType.BUFF;
		}
		
		public void updateDescription() {
			description = desc(0) + this.amount + desc(1) + (version <= 2 ? DEMON : name() + (version - 1)) + desc(2);
		}
		
		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
	        this.amount += stackAmount;
		}
		
		public void atStartOfTurnPostDraw() {
			this.flashWithoutSound();
			this.atb(this.apply(this.owner, demon(this.owner, this.amount, this.version - 1)));
		}

	}
	
}