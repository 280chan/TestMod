package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import relics.IntensifyImprint;

public class IntensifyImprintPower extends AbstractPower implements InvisiblePower {
	public static final String POWER_ID = "IntensifyImprintPower";
	public static final String NAME = "激化刻印";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "如果你看见这句话，请反馈bug。";
	private IntensifyImprint r;
	
	public IntensifyImprintPower(AbstractCreature owner, IntensifyImprint r) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
		this.r = r;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
    public int onAttacked(final DamageInfo info, int damage) {
    	if ((info.owner == null || info.owner == AbstractDungeon.player) && (damage > 0)) {
			if (this.r.counter > 0)
				this.r.show();
			damage += this.r.counter;
			this.r.incrementCounter();
		}
		return damage;
    }

}
