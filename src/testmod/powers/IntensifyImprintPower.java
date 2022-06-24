package testmod.powers;

import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.relics.AbstractTestRelic;
import testmod.relics.IntensifyImprint;
import testmod.relicsup.IntensifyImprintUp;

public class IntensifyImprintPower extends AbstractTestPower implements InvisiblePower {
	private static final int PRIORITY = 999999;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof IntensifyImprintPower);
	}
	
	public IntensifyImprintPower(AbstractCreature owner) {
		this.owner = owner;
		updateDescription();
		this.type = PowerType.BUFF;
		this.priority = PRIORITY;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	private Stream<AbstractTestRelic> stream() {
		return Stream.concat(this.relicStream(IntensifyImprint.class), this.relicStream(IntensifyImprintUp.class));
	}
	
	public void incrementCounter(AbstractTestRelic r) {
		r.counter++;
		r.updateDescription();
		r.show();
	}
	
	private boolean up() {
		return this.relicStream(IntensifyImprintUp.class).count() > 0;
	}
	
    public int onAttacked(final DamageInfo info, int damage) {
    	if (up() || ((info.owner == null || info.owner.isPlayer) && (damage > 0))) {
			damage += stream().mapToInt(r -> r.counter).sum();
			stream().forEach(this::incrementCounter);
		}
		return damage;
    }

}
