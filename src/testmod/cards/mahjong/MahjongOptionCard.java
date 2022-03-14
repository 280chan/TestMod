
package testmod.cards.mahjong;

import basemod.abstracts.*;
import testmod.mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class MahjongOptionCard extends CustomCard {
    public static final String ID = "MahjongOptionCard";
    public static final String NAME = "选择 - ";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final String[] NAMES = {"立直", "自摸", "杠", "返回"};
    private static final String[] DESCS = {"", "", "", ""};
    private static final int COST = -2;
    private int mode;

    public MahjongOptionCard(int mode) {
        super(ID, getName(mode), IMG, COST, getDesc(mode), CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        this.mode = mode;
    }

    private static String getName(int mode) {
    	return NAME + NAMES[mode];
    }
    
    private static String getDesc(int mode) {
    	return DESCS[mode];
    }
    
    public int mode() {
    	return this.mode;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    }
    
    public boolean canUpgrade() {
    	return false;
    }
    
    public void upgrade() {
    }
}