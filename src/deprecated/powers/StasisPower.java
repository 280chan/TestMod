package deprecated.powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * @deprecated
 */
public class StasisPower extends AbstractPower{
	public static final String POWER_ID = "StasisPower";
	public static final String NAME = "凝滞";//能力的名称。
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Stasis");
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

	private static int idOffset;
	private AbstractCard card;
	
	public StasisPower(AbstractCreature owner, AbstractCard card) {
		this.name = NAME;
		this.ID = POWER_ID + idOffset++;
		this.owner = owner;
		this.amount = -1;
		this.card = card;
		updateDescription();
		loadRegion("stasis");
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = (DESCRIPTIONS[0] + " #y" + this.card.name + " " + DESCRIPTIONS[1]);
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public void onDeath() {
		if (AbstractDungeon.player.hand.size() != 10) {
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(this.card, 1, false));
		} else {
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(this.card, 1));
		}
	}
}
