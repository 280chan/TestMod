package powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MalleablePower;

import mymod.TestMod;

public class ConditionedReflexPower extends AbstractPower {
	public static final String POWER_ID = "ConditionedReflexPower";
	public static final String NAME = "条件反射";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每失去一次生命，获得 #b"," 层 #y柔韧 ，并在下回合开始时使其生效 #b1 次。", " NL 下回合开始生效 #b", " 次"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	
	private int activeAmount;
	
	public ConditionedReflexPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		 if (this.activeAmount > 0)
			 this.description += DESCRIPTIONS[2] + this.activeAmount + DESCRIPTIONS[3];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	private void activate(AbstractPower p) {
		if (p.owner.isPlayer) {
			AbstractDungeon.actionManager.addToTop(new GainBlockAction(p.owner, p.owner, p.amount));
		} else {
			AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p.owner, p.owner, p.amount));
		}
		p.amount++;
		p.updateDescription();
	}
	
	public void atStartOfTurn() {
		if (this.activeAmount > 0) {
			AbstractPower mp = this.owner.getPower(MalleablePower.POWER_ID);
			if (mp == null)
				return;
			mp.flash();
			for (int i = 0; i < this.activeAmount; i++)
				this.activate(mp);
			this.activeAmount = 0;
    		this.updateDescription();
		}
	}
	
    public int onLoseHp(final int damage) {
    	if (damage > 0) {
    		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(owner, owner, new MalleablePower(owner, this.amount), this.amount));
    		this.activeAmount++;
    		this.updateDescription();
    	}
    	return damage;
    }
    
}
