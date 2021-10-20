
package cards.colorless;

import cards.AbstractTestCard;
import actions.ArrangementUpgradingAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.dungeons.*;
import com.badlogic.gdx.math.MathUtils;

public class Arrangement extends AbstractTestCard {
	private static final int BASE_BLK = 0;
	private static final int BASE_DMG = 0;
	private static final int BASE_MGC = 1;

	public Arrangement() {
		super(Arrangement.class, -1, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF_AND_ENEMY);
		this.baseBlock = BASE_BLK;
		this.baseDamage = BASE_DMG;
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpXCostActionToBot(this, a -> {
			this.addToTop(new ArrangementUpgradingAction(p, a));
			this.addToTop(new DrawCardAction(p, a));
			if (this.upgraded) {
				for (int i = 0; i < a; i++) {
					this.damage(p, m);
				}
				for (int i = 0; i < a; i++) {
					this.block(p);
				}
			} else {
				this.damage(p, m);
				this.block(p);
			}
		});
	}
	
	private void damage(AbstractPlayer p, AbstractMonster m) {
		this.addToTop(new DamageAction(m, new DamageInfo(p, this.damage, this.damageType), AttackEffect.BLUNT_LIGHT));
	}
	
	private void block(AbstractPlayer p) {
		this.addToTop(new GainBlockAction(p, p, this.block, true));
	}
    
	public void calculateCardDamage(AbstractMonster m) {
		AbstractPlayer player = AbstractDungeon.player;

		this.isBlockModified = false;
		float blc = this.baseBlock;

		this.isDamageModified = false;
		float tmp = this.baseDamage;

		if (!this.upgraded) {
			int x = this.energyOnUse == -1 ? EnergyPanel.totalCount : this.energyOnUse;
			if (player.hasRelic("Chemical X"))
				x += 2;
			blc = tmp = x;
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
		if (AbstractDungeon.player.hasRelic("WristBlade") && (this.costForTurn == 0 || this.freeToPlayOnce)) {
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
            this.rawDescription = this.upgradedDesc();
            this.initializeDescription();
            this.upgradeBlock(1);
            this.upgradeDamage(1);
        }
    }
}