
package cards.mahjong;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import com.megacrit.cardcrawl.actions.unique.ExhumeAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;

public class MahjongWs extends AbstractMahjongCard {
	public static final String DESCRIPTIONS[] = { "抽 !M! 张牌。 消耗 。 虚无 。", " 预见 !M! 张牌。 消耗 。 虚无 。",
			"升级1张手牌 !M! 次。 消耗 。 虚无 。", "从抽牌堆选择 !M! 张牌放入手牌。 消耗 。 虚无 。", "将 !M! 张随机麻将牌加入手牌。 消耗 。 虚无 。",
			"从弃牌堆选择 !M! 张牌放入手牌。 消耗 。 虚无 。", " 发掘 !M! 张牌。 消耗 。 虚无 。", " 消耗 !M! 张手牌。 消耗 。 虚无 。",
			"丢弃 !M! 张手牌。 消耗 。 虚无 。" };
	public static final String UPGRADE_DESCRIPTION = "升级手牌中的所有牌。 消耗 。 虚无 。";
	private static final int COLOR = 0;
	private static final int BASE_MGC = 1;
    private int mode;

    public MahjongWs() {
        this(1);
    }
    
    public MahjongWs(int num) {
        super(COLOR, num, setString(num), CardType.SKILL, CardTarget.NONE);
        if (num == 0)
        	this.baseMagicNumber = 2 * BASE_MGC;
        else
        	this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.isEthereal = true;
        this.mode = num;
        if (this.mode == 0)
        	this.mode = 5;
    }
    
    private static String setString(int num) {
    	if (num == 0)
    		num = 5;
    	return DESCRIPTIONS[num - 1];
    }
    
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		switch (this.mode) {
		case 1:
			this.addToBot(new DrawCardAction(this.magicNumber));
			break;
		case 2:
			this.addToBot(new ScryAction(this.magicNumber));
			break;
		case 3:
			if (this.upgraded)
				this.addToBot(new ArmamentsAction(this.upgraded));
			else {
				for (int i = 0; i < this.magicNumber; i++)
					this.addToBot(new ArmamentsAction(this.upgraded));
			}
			break;
		case 4:
			this.addToBot(new BetterDrawPileToHandAction(this.magicNumber));
			break;
		case 5:
			for (int i = 0; i < this.magicNumber; i++)
				this.addToBot(new MakeTempCardInHandAction(this.pureRandomMahjong()));
			break;
		case 6:
			this.addToBot(new BetterDiscardPileToHandAction(this.magicNumber));
			break;
		case 7:
			for (int i = 0; i < this.magicNumber; i++)
				this.addToBot(new ExhumeAction(false));
			break;
		case 8:
			this.addToBot(new ExhaustAction(this.magicNumber, false, false, false));
			break;
		default:
			this.addToBot(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, this.magicNumber, false));
		}
	}
    
    public AbstractCard makeCopy() {
        return new MahjongWs(this.num());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            if (this.mode != 3)
            	this.upgradeMagicNumber(this.magicNumber);
            else {
            	this.rawDescription = UPGRADE_DESCRIPTION;
            	this.initializeDescription();
            }
        }
    }
}