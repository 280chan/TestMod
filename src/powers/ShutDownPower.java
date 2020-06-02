package powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class ShutDownPower extends AbstractPower {
	public static final String POWER_ID = "ShutDownPower";
	public static final String NAME = "关机";//能力的名称。
	public static final String[] DESCRIPTIONS = { "每回合将 #b", " 张 #y重启", " 放入手牌。" };
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	private boolean upgraded;
	
	public ShutDownPower(AbstractCreature owner, boolean upgraded, int amount) {
		this.name = NAME;
		if (upgraded)
			this.name += "+";
		this.ID = POWER_ID + upgraded;
		this.owner = owner;
		this.amount = 1;
		this.upgraded = upgraded;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		 if (this.upgraded)
			 this.description += "+";
		 this.description += DESCRIPTIONS[2];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
    public void atStartOfTurn() {
    	AbstractCard c = new Reboot();
    	if (upgraded)
    		c.upgrade();
		for (int i = 0; i < this.amount; i++) {
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
		}
    }
    
}
