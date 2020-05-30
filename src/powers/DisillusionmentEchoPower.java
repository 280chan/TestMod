
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import utils.MiscMethods;

public class DisillusionmentEchoPower extends AbstractPower implements MiscMethods {
	
	public static final String POWER_ID = "DisillusionmentEchoPower";
	public static final String NAME = "幻灭回响";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "你在每回合打出的第一张牌，将会额外打出 #b1 次。";
	public static final String[] DESCRIPTIONS = {"你在每回合打出的第一张牌，将会额外打出 #b"," 次。",""};
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	
	public DisillusionmentEchoPower(AbstractCreature owner, int amount) {
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
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.purgeOnUse && AbstractDungeon.player.cardsPlayedThisTurn < 2) {
			flash();
			for (int i = 0; i < this.amount; i++) {
				playAgain(card, action);
			}
		}
	}

	private void playAgain(AbstractCard card, UseCardAction action) {
		AbstractMonster m = null;
		if (action.target != null) {
			m = (AbstractMonster) action.target;
		}
		this.playAgain(card, m);
	}
    
}
