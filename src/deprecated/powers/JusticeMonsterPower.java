
package deprecated.powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import relics.Justice;

/**
 * @deprecated
 */
public class JusticeMonsterPower extends AbstractPower{
	
	public static final String POWER_ID = "JusticeMonsterPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "邪恶";//能力的名称。
	public static final String IMG = "resources/images/JusticeMonsterPower.png";
	public static final String DESCRIPTION = "当给予玩家负面状态时，给玩家增加 #b1 力量。";//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。
	public static final String[] DESCRIPTIONS = {"","",""};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	
	public JusticeMonsterPower(AbstractCreature owner) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		//以上五句不可缺少，照抄即可。记得修改this.img的图片路径。
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;//不需要调用变量的文本更新方式。
		 
		 //this.description = (DESCRIPTIONS[0] + 变量1 + DESCRIPTIONS[1] + 变量2 + DESCRIPTIONS[3] + ······);需要调用变量的文本更新方式。
		 //例： public static final String[] DESCRIPTIONS = {"在你回合开始时获得","点力量."};
		 //   this.description = (DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]);
		 //   通过该方式更新后的文本:在你回合开始时获得amount层力量.
		 //   另外一提，除变量this.amount（能力层数对应的变量）外，其他变量被赋值后需要人为调用updateDescription函数进行文本更新。
	}
	
    public void onApplyPower(final AbstractPower power, final AbstractCreature t, final AbstractCreature source) {
    	if (t.isPlayer && power.type == PowerType.DEBUFF) {
    		((Justice)AbstractDungeon.player.getRelic("Justice")).show();
    		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(t, t, new StrengthPower(t, 1), 1));
    	}
    	//参数：power-能力Id，target-被给予者，source-给予者
    }//触发时机：当给予能力时。

}
