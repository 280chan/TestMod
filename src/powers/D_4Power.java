
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import relics.D_4;
import relics.D_4.Situation;

public class D_4Power extends AbstractPower{
	private static final String POWER_ID = "D_4Power";
	private static final String NAME = "符文四面体";
	private static final String DESCRIPTION = "打出下一张牌时，";
	private static final String IMG = "resources/images/powers/" + POWER_ID;
	private static final int AMOUNT = -1;
	public Situation situation;
	public static boolean endTurn = false;
	
	public D_4Power(AbstractCreature owner, Situation s) {
		this.name = NAME;
		this.ID = POWER_ID + s.ordinal();
		this.owner = owner;
		this.amount = AMOUNT;
		this.img = ImageMaster.loadImage(IMG + s.ordinal() + ".png");

		this.situation = s;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION + situation.toString() + "。";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount = -1;
	}
	
    public void onRemove() {
    	AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(owner, owner, next()));
    }
    
    private D_4Power next() {
    	return new D_4Power(owner, D_4.nextSituation);
    }
    
    
}
