
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import actions.ArrangementAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.badlogic.gdx.math.MathUtils;

public class Arrangement extends AbstractTestCard {
	public static final String ID = "Arrangement";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = -1;
	private static final int BASE_BLK = 0;
	private static final int BASE_DMG = 0;
	private static final int BASE_MGC = 1;

	public Arrangement() {
		super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF_AND_ENEMY);
		this.baseBlock = BASE_BLK;
		this.baseDamage = BASE_DMG;
		this.baseMagicNumber = BASE_MGC;
		this.magicNumber = this.baseMagicNumber;

		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new ArrangementAction(p, m, this.damageTypeForTurn, this.freeToPlayOnce, this.upgraded,
				this.energyOnUse, this.damage, this.block));
	}
    
	public void calculateCardDamage(AbstractMonster m) {
		AbstractPlayer player = AbstractDungeon.player;

		this.isBlockModified = false;
		float blc = this.baseBlock;

		this.isDamageModified = false;
		float tmp = this.baseDamage;

		if (!this.upgraded) {
			int x = EnergyPanel.totalCount;
			if (this.energyOnUse != -1)
				x = this.energyOnUse;
			if (player.hasRelic("Chemical X"))
				x += 2;
			blc = x;
			tmp = x;
		}

		// 防御
		for (AbstractPower p : player.powers) {
			blc = p.modifyBlock(blc);
			if (this.baseBlock != MathUtils.floor(blc)) {
				this.isBlockModified = true;
			}
		}
		if (blc < 0.0F) {
			blc = 0.0F;
		}
		this.block = MathUtils.floor(blc);

		// 攻击
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
		for (AbstractPower p : m.powers) {
			tmp = p.atDamageReceive(tmp, this.damageTypeForTurn);
		}
		for (AbstractPower p : player.powers) {
			tmp = p.atDamageFinalGive(tmp, this.damageTypeForTurn);
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
		}
		for (AbstractPower p : m.powers) {
			tmp = p.atDamageFinalReceive(tmp, this.damageTypeForTurn);
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
		}
		if (tmp < 0.0F) {
			tmp = 0.0F;
		}
		this.damage = MathUtils.floor(tmp);
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
            this.upgradeBlock(1);
            this.upgradeDamage(1);
        }
    }
}