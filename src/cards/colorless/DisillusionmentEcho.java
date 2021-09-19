//感谢天地一dalao，承泽dalao以及群里的各位dalao教导，忙了几个小时总算做出来了
//我认真地测试过正确性了
//出错了我会认真改的
//上面两条是假的

//tips：这只是个模板，出任何错误或者运行不了或者某个语句错了与本人无关
// 哈哈哈哈——BY  U2

package cards.colorless;

import cards.AbstractTestCard;
import powers.DisillusionmentEchoPower;
import utils.MiscMethods;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

public class DisillusionmentEcho extends AbstractTestCard implements MiscMethods {
	public static final String ID = "DisillusionmentEcho";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = -1;
	private static final int BASE_MGC = 3;

	public DisillusionmentEcho() {
		super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpXCostActionToBot(this, a -> {
			int amount = a / (this.magicNumber == 0 ? 3 - this.timesUpgraded : this.magicNumber);
			if (amount > 0)
				this.addToTop(new ApplyPowerAction(p, p, new DisillusionmentEchoPower(p, amount), amount));
		});
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(-1);
		}
	}
}