
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import actions.AdversityCounterattackAction;

import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class AdversityCounterattack extends CustomCard {
    public static final String ID = "AdversityCounterattack";
    public static final String NAME = "逆境反击";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "给予敌人 !M! 层 人工制品 ，获得 !M! 层 易伤 和1层 无实体 。随机造成该敌人增益状态层数点伤害，自身负面状态层数次。";
    private static final int COST = 1;
    private static final int BASE_MGC = 1;
    private static final int INT_AMOUNT = 1;

    public AdversityCounterattack() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ENEMY);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ApplyPowerAction(m, p, new ArtifactPower(m, this.magicNumber), this.magicNumber));
    	this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, this.magicNumber, false), this.magicNumber));
    	this.addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, INT_AMOUNT), INT_AMOUNT));
    	this.addToBot(new AdversityCounterattackAction(p, m, AttackEffect.SLASH_HORIZONTAL));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}