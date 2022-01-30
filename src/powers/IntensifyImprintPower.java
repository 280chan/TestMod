package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relics.IntensifyImprint;

public class IntensifyImprintPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "IntensifyImprintPower";
	private static final int PRIORITY = 999999;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof IntensifyImprintPower);
	}
	
	public IntensifyImprintPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
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
	
    public int onAttacked(final DamageInfo info, int damage) {
    	if ((info.owner == null || info.owner == AbstractDungeon.player) && (damage > 0)) {
			damage += this.relicStream(IntensifyImprint.class).mapToInt(r -> r.counter).sum();
			this.relicStream(IntensifyImprint.class).forEach(r -> r.incrementCounter());
		}
		return damage;
    }

}
