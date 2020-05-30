
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LimitFlipper extends CustomCard {
    public static final String ID = "LimitFlipper";
    public static final String NAME = "燃烧极限";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "如果你打出的上一张牌也是燃烧极限，将你的 力量 翻倍。否则获得 !M! 点 力量 。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 1;//卡牌费用
    private static final int BASE_MGC = 1;
    private static boolean active = false;

    public LimitFlipper() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	if (active) {
    	    AbstractDungeon.actionManager.addToBottom(new LimitBreakAction());
		} else {
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
		}
    }

	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		if (active) {
			this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
		}
	}
	
    public void triggerOnCardPlayed(AbstractCard c) {
		active = c.cardID.equals(ID);
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(1);
        }
    }
}