package testmod.relics;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CombustPower;
import com.megacrit.cardcrawl.powers.EchoPower;
import com.megacrit.cardcrawl.powers.PanachePower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.TimeMazePower;

import basemod.ReflectionHacks;
import testmod.mymod.TestMod;
import testmod.powers.RecapPower;
import testmod.relicsup.RegisterUp;

public class Register extends AbstractTestRelic {
	public static final ArrayList<AbstractPower> POWERS = new ArrayList<AbstractPower>();
	private static final ArrayList<AbstractPower> REDUCED = new ArrayList<AbstractPower>();
	private static boolean reduced = false;
	
	public void clear() {
		POWERS.clear();
		reduced = false;
		REDUCED.clear();
	}
	
	private void updateInfo() {
		clear();
		POWERS.addAll(p().powers);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive || this.relicStream(RegisterUp.class).count() > 0)
			return;
		clear();
    }
	
	private static boolean isUp() {
		return MISC.hasStack(RegisterUp.class.getCanonicalName(), "prepare");
	}
	
	public static AbstractPower prepare(AbstractPower pow) {
		AbstractPower p = pow;
		if (p instanceof CombustPower)
			p = new CombustPower(MISC.p(), 1, isUp() ? Math.max(5, p.amount / 2) : 5);
		else if (p instanceof PanachePower)
			p = new PanachePower(MISC.p(),
					isUp() ? (int) ReflectionHacks.getPrivate(p, PanachePower.class, "damage") / 2 : 10);
		else if (p instanceof ThornsPower)
			p.stackPower(0);
		else if (p instanceof TimeMazePower)
			p.amount = 15;
		else if (p instanceof RecapPower)
			((RecapPower) p).list.clear();
		else if (p instanceof EchoPower)
			p.atStartOfTurn();
		p.updateDescription();
		if (!isUp())
			REDUCED.add(p);
		return p;
	}
	
	private boolean filterAndChangeAmount(AbstractPower p) {
		if ((p.amount < 2 && p.amount > -2) || p instanceof InvisiblePower)
			return false;
		p.amount = p.amount < 0 ? -1 : 1;
		this.ensure(p);
		return true;
	}
	
	private void ensure(AbstractPower p) {
		if (p.owner != p())
			p.owner = p();
	}
	
	public void atBattleStart() {
		if (!isActive || this.relicStream(RegisterUp.class).count() > 0)
			return;
		if (reduced) {
			REDUCED.stream().peek(this::ensure).forEach(p -> this.att(apply(p(), p)));
		} else {
			POWERS.stream().filter(this::filterAndChangeAmount).forEach(p -> this.att(apply(p(), prepare(p))));
			reduced = true;
		}
		if (!REDUCED.isEmpty())
			this.show();
    }
	
	public void onVictory() {
		if (!isActive || this.relicStream(RegisterUp.class).count() > 0)
			return;
		updateInfo();
	}
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
}