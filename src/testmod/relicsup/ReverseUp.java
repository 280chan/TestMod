package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import testmod.relics.Reverse;

public class ReverseUp extends AbstractUpgradedRelic implements OnReceivePowerRelic, OnPlayerDeathRelic {
	
	public ReverseUp() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public static int a() {
		return (int) MISC.relicStream(Reverse.class).count();
	}
	
	public static int b() {
		return (int) MISC.relicStream(ReverseUp.class).count();
	}

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature source) {
		boolean act = false;
		if (this.isActive && p.type == PowerType.BUFF && (p.canGoNegative || p.amount != -1)) {
			p.amount *= Math.max(1, 1 << b());
			act = true;
		}
		if (this.isActive && p.amount < 0 && (p.canGoNegative || p.amount != -1)) {
			int delta = -p.amount * (a() + b());
			p.stackPower(delta - p.amount);
			if (p.amount < 0) {
				p.amount = delta;
			}
			this.relicStream(Reverse.class).forEach(r -> r.show());
			act = true;
		}
		if (act) {
			p.updateDescription();
			this.relicStream(ReverseUp.class).forEach(r -> r.show());
		}
		return true;
	}
	
	@Override
	public int onReceivePowerStacks(AbstractPower p, AbstractCreature source, int amount) {
		if (this.isActive && p.type == PowerType.BUFF && (p.canGoNegative || amount != -1)) {
			amount *= Math.max(1, 1 << b());
		}
		if (amount < 0 && (p.canGoNegative || amount != -1))
			amount = -amount;
		return (this.isActive && amount < 0 && (p.canGoNegative || amount != -1)) ? amount * (a() + b()) : amount;
	}
	
	public void onEquip() {
		this.counter = -2 * AbstractDungeon.actNum;
		this.beginLongPulse();
	}
	
	public void atPreBattle() {
		if (this.counter >= -2 * AbstractDungeon.actNum)
			this.beginLongPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}

	@Override
	public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
		if (this.counter == -2 * AbstractDungeon.actNum) {
			this.counter--;
			p().heal(p().maxHealth);
			this.stopPulse();
			this.show();
			return true;
		} else if (this.counter < -2 * AbstractDungeon.actNum) {
			return false;
		}
		this.counter = -2 * AbstractDungeon.actNum - 1;
		this.show();
		this.stopPulse();
		return true;
	}
	
}