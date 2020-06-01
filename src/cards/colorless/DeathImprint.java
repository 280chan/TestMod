
package cards.colorless;

import powers.DeathImprintPower;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;

import actions.DeathImprintAction;
import basemod.abstracts.CustomCard;
import mymod.TestMod;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class DeathImprint extends CustomCard {
    public static final String ID = "DeathImprint";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = 2;//卡牌费用
    private static final int BASE_DMG = 8;//基础伤害值
    private static final int BASE_MGC = 80;//基础伤害值
    public boolean same = false;

    public DeathImprint() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new DeathImprintAction(p, m, this.damage, this.damageTypeForTurn));//造成伤害
    }

	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if ((!m.isDeadOrEscaped()) && (m.hasPower(DeathImprintPower.POWER_ID))) {
				this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
				break;
			}
		}
	}
    
	public void calculateCardDamage(AbstractMonster m) {
		AbstractPlayer player = AbstractDungeon.player;
		this.isDamageModified = false;
		float tmp = this.baseDamage;
		if (m != null) {
			if (m.hasPower(DeathImprintPower.POWER_ID)) {
				tmp += m.getPower(DeathImprintPower.POWER_ID).amount * this.magicNumber / 100f;
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
		}
		if ((!this.isMultiDamage) && (m != null)) {
			if ((AbstractDungeon.player.hasRelic("WristBlade")) && ((this.costForTurn == 0) || (this.freeToPlayOnce))) {
				tmp += 3.0F;
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
			for (AbstractPower p : player.powers) {
				tmp = p.atDamageGive(tmp, this.damageTypeForTurn);
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
			if (m != null) {
				for (AbstractPower p : m.powers) {
					tmp = p.atDamageReceive(tmp, this.damageTypeForTurn);
				}
			}
			for (AbstractPower p : player.powers) {
				tmp = p.atDamageFinalGive(tmp, this.damageTypeForTurn);
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
			if (m != null) {
				for (AbstractPower p : m.powers) {
					tmp = p.atDamageFinalReceive(tmp, this.damageTypeForTurn);
					if (this.baseDamage != (int) tmp) {
						this.isDamageModified = true;
					}
				}
			}
			if (tmp < 0.0F) {
				tmp = 0.0F;
			}
			this.damage = MathUtils.floor(tmp);
		}
	}
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(20);
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变

}