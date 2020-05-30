
package cards.mahjong;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ArtifactPower;

import powers.MahjongZsPower;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;

public class MahjongZs extends AbstractMahjongCard {
	public static final String DESCRIPTIONS[] = { "每打出1张麻将牌，获得1层 再生 ， !M! 层多层护甲。 虚无 。", "每打出1张麻将牌，获得 !M! 层 真言 。 虚无 。",
			"每打出1张非攻击牌，你造成的攻击伤害提高 !M! %。加成之间相乘。 虚无 。", "免疫 !M! 次致命伤害，以1%最大生命复活。每打出1张牌将复活生命比例提升1%，生效后重置。 虚无 。",
			"去除自身所有负面 状态 ，获得 !M! 层 人工制品 。 虚无 。", "每当你打出麻将牌，每个敌人使你获得 !M! 金币 。 虚无 。", "每回合开始额外触发 !M! 次麻将遗物的效果。 虚无 。" };
	private static final int COLOR = 3;
	private static final int BASE_MGC = 1;
    private int mode;

    public MahjongZs() {
        this(1);
    }
    
    public MahjongZs(int num) {
        super(COLOR, num, setString(num), CardType.POWER, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.isEthereal = true;
        this.mode = num;
    }
    
    private static String setString(int num) {
    	return DESCRIPTIONS[num - 1];
    }
    
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (this.mode == 5) {
			this.addToBot(new AbstractGameAction(){
				@Override
				public void update() {
					this.isDone = true;
					this.addToBot(new RemoveDebuffsAction(p));
					this.addToBot(new ApplyPowerAction(p, p, new ArtifactPower(p, MahjongZs.this.magicNumber), MahjongZs.this.magicNumber));
				}});
		} else {
			this.addToBot(new ApplyPowerAction(p, p, new MahjongZsPower(p, this, this.magicNumber), this.magicNumber));
		}
	}
    
    public AbstractCard makeCopy() {
        return new MahjongZs(this.num());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}