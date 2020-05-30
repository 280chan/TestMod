package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.EventRoom;

import relics.AscensionHeart;

import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;

public class EventHalfDamagePower extends AbstractPower{
	
	public static final String POWER_ID = "EventHalfDamagePower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "能力名称";//能力的名称。
	public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "能力描叙";//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。
	public static final String[] DESCRIPTIONS = {"","",""};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	private static AscensionHeart ah = null;
	
	public EventHalfDamagePower(AbstractCreature owner, AscensionHeart relic) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		ah = relic;
		//以上五句不可缺少，照抄即可。记得修改this.img的图片路径。
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}//可通过添加if判定this.amount来限制层数上限。
	
    public int onLoseHp(int damage) {
    	if (AbstractDungeon.currMapNode != null) {
        	if (AbstractDungeon.getCurrRoom() instanceof EventRoom) {
        		if (AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT) {
            		if (ah.checkLevel(15)) {
            			damage /= 2;
            		}
        		}
        	}
        }
        return damage;
    }//触发时机：当失去生命值时，返回伤害数值，可用来修改伤害数值。

}
