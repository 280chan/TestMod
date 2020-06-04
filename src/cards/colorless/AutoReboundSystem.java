
package cards.colorless;

import cards.AbstractTestCard;
import powers.AutoReboundPower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.BerserkPower;
import com.megacrit.cardcrawl.powers.DrawPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class AutoReboundSystem extends AbstractTestCard {
	public static final String ID = "AutoReboundSystem";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String[] E = { " [R]", " [G]", " [B]", " [W]" };
	private static final int COST = 3;// 卡牌费用
	private static final int BASEMAGIC = 1;

	public AutoReboundSystem() {
		super(ID, NAME, COST, getDescription(), CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.baseMagicNumber = BASEMAGIC;
		this.magicNumber = this.baseMagicNumber;
	}

	public static String getDescription() {
		return getDescription(BASEMAGIC);
	}
	
	public static String getDescription(int magic) {
		String temp = EXTENDED_DESCRIPTION[0];
		String e = E[0];
		if (AbstractDungeon.player != null) {
			switch (AbstractDungeon.player.chosenClass) {
			case WATCHER:
				e = E[3];
				break;
			case DEFECT:
				e = E[2];
				break;
			case THE_SILENT:
				e = E[1];
			default:
			}
		}
		if (magic < 4 && magic > 0) {
			for (int i = 0; i < magic; i++)
				temp += e;
		} else
			temp += magic + e;
		temp += EXTENDED_DESCRIPTION[1];
		return temp;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new AutoReboundPower(p, this.magicNumber), this.magicNumber));
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BerserkPower(p, this.magicNumber), this.magicNumber));
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DrawPower(p, this.magicNumber), this.magicNumber));
	}

	public AbstractCard makeCopy() {
		return new AutoReboundSystem();
	}// 复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();// 改名，其实就是多个+
			this.upgradeMagicNumber(1);
			this.rawDescription = getDescription(magicNumber);
			this.initializeDescription();
		}
	}// 升级后额外增加（括号内的）值，以及升级后的各种改变
}