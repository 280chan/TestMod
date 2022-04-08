package testmod.relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DemonFormPower;
import testmod.powers.AbstractTestPower;

public class DemonSummon extends AbstractTestRelic {
	
	public DemonSummon() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
	public void atTurnStartPostDraw() {
		this.counter++;
		this.atb(this.apply(this.p(), demon(p(), this.counter, this.counter)));
		this.show();
    }
	
	private static AbstractPower demon(AbstractCreature owner, int amount, int version) {
		return version <= 1 ? new DemonFormPower(owner, amount) : new DemonSummonPower(owner, amount, version);
	}

	public static class DemonSummonPower extends AbstractTestPower {
		public static final String POWER_ID = "DemonSummonPower";
		private static final PowerStrings PS = Strings(POWER_ID);
		private static final String NAME = PS.NAME;
		private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
		private static final String DEMON = CardCrawlGame.languagePack.getPowerStrings(DemonFormPower.POWER_ID).NAME;
		public int version;
		
		public DemonSummonPower(AbstractCreature owner, int amount, int version) {
			super(POWER_ID);
			this.ID += version;
			this.name = NAME + version;
			this.owner = owner;
			this.version = version;
			this.amount = amount;
			updateDescription();
			this.type = PowerType.BUFF;
		}
		
		public void updateDescription() {
			this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] 
					+ (this.version <= 2 ? DEMON : NAME + (this.version - 1)) + DESCRIPTIONS[2];
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