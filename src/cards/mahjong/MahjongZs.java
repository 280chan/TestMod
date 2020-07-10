
package cards.mahjong;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ArtifactPower;

import mymod.TestMod;
import powers.MahjongZsPower;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;

public class MahjongZs extends AbstractMahjongCard {
    private static final String STRING_ID = "MahjongZs";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(STRING_ID));
    private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
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
        return EXTENDED_DESCRIPTION[num - 1];
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        if (this.mode == 5) {
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    this.isDone = true;
                    this.addToBot(new RemoveDebuffsAction(p));
                    this.addToBot(new ApplyPowerAction(p, p, new ArtifactPower(p, MahjongZs.this.magicNumber), MahjongZs.this.magicNumber));
                }
            });
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