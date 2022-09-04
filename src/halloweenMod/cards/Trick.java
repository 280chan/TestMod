
package halloweenMod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.DaggerSprayEffect;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import halloweenMod.mymod.HalloweenMod;
import testmod.utils.Festival;
import halloweenMod.actions.TrickAction;

public class Trick extends AbstractCard implements Festival {
    public static final String ID = HalloweenMod.MOD_PREFIX + "Trick";
    public static final String IMG = "green/attack/daggerSpray";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = cardStrings.NAME;
    private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_DMG = 10;
    private static final int BASE_MGC = 1;
    private static final int BASE_DRAW = 1;
    private static final int BASE_BUFF = 1;
    private int buffAmount;

    public Trick() {
        super(ID, NAME, IMG, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ALL_ENEMY);
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.isMultiDamage = true;
        this.baseDraw = BASE_DRAW;
        this.draw = this.baseDraw;
        this.buffAmount = BASE_BUFF;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new VFXAction(new DaggerSprayEffect(AbstractDungeon.getMonsters().shouldFlipVfx()), 0.0F));
        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        AbstractDungeon.actionManager.addToBottom(new TrickAction(p, this.buffAmount, this.draw));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new LoseStrengthPower(p, this.magicNumber), this.magicNumber));
    }
    
    public AbstractCard makeCopy() {
        return new Trick();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
            this.upgradeMagicNumber(1);
        }
    }
}