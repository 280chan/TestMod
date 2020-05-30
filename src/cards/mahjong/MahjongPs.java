
package cards.mahjong;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;
import com.megacrit.cardcrawl.actions.common.*;

public class MahjongPs extends AbstractMahjongCard {
    public static final String DESCRIPTIONS[] = {"获得 !B! 点格挡。 消耗 。 虚无 。", "获得 !B! 点格挡。下回合开始获得 !B! 点格挡。 消耗 。 虚无 。"};
    private static final int COLOR = 1;
    private int mode;

    public MahjongPs() {
        this(1);
    }
    
    public MahjongPs(int num) {
        super(COLOR, num, setString(num), CardType.SKILL, CardTarget.SELF);
        this.baseBlock = this.setBaseBlock();
        this.exhaust = true;
        this.isEthereal = true;
    }
    
    private int setBaseBlock() {
    	int tmp = this.num();
        this.mode = tmp % 2;
        if (tmp == 0) {
        	tmp = 5;
        	return 2 * tmp;
        }
        return tmp;
    }
    
    private static String setString(int num) {
    	return DESCRIPTIONS[num % 2];
    }
    
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (this.mode == 0) {
			this.addToBot(new GainBlockAction(p, p, this.block));
		} else {
			this.addToBot(new GainBlockAction(p, p, this.block));
			this.addToBot(new ApplyPowerAction(p, p, new NextTurnBlockPower(p, this.block), this.block));
		}
	}
    
    public AbstractCard makeCopy() {
        return new MahjongPs(this.num());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(1);
        }
    }

}