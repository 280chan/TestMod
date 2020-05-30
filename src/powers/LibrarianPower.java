
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import mymod.TestMod;

public class LibrarianPower extends AbstractPower {
	public static final String POWER_ID = "LibrarianPower";
	public static final String NAME = "图书管理员";
	public static final String[] DESCRIPTIONS = {"若在接下来的 #b"," 次回合开始之前胜利，将 #y", " 加入牌组。"};
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	private AbstractCard c;
	
	public LibrarianPower(AbstractCreature owner, AbstractCard c, int amount) {
		this.name = NAME + "[" + c.name + "]";
		this.ID = POWER_ID + c.cardID;
		this.owner = owner;
		this.amount = amount;
		this.c = c;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.c.name + DESCRIPTIONS[2];
	}
	
	public void stackPower(final int stackAmount) {
		this.amount += stackAmount;
	}
	
    public void atStartOfTurn() {
    	this.amount--;
    	this.flashWithoutSound();
    	if (this.amount == 0)
    		this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    	else
        	this.updateDescription();
    }
    
    public void onVictory() {
    	this.flash();
    	AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.c.makeCopy(),
				Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }
    
}
