
package testmod.cards.colorless;

import java.util.function.UnaryOperator;

import testmod.actions.ArrangementUpgradingAction;
import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.badlogic.gdx.math.MathUtils;

public class Arrangement extends AbstractTestCard {
	private static final int BASE_BLK = 0;
	private static final int BASE_DMG = 0;
	private static final int BASE_MGC = 1;

	public Arrangement() {
		super(-1, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF_AND_ENEMY);
		this.baseBlock = BASE_BLK;
		this.baseDamage = BASE_DMG;
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpXCostActionToBot(this, a -> {
			this.att(new ArrangementUpgradingAction(p, a));
			this.att(new DrawCardAction(p, a));
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
		this.att(new DamageAction(m, new DamageInfo(p, this.damage, this.damageType), AttackEffect.BLUNT_LIGHT));
	}
	
	private void block(AbstractPlayer p) {
		this.att(new GainBlockAction(p, p, this.block, true));
	}
	
	private UnaryOperator<Float> blo(AbstractPower p) {
		return p::modifyBlock;
	}
    
	private UnaryOperator<Float> adm(AbstractRelic r) {
		return f -> r.atDamageModify(f, this);
	}
	
	private UnaryOperator<Float> adg(AbstractPower r) {
		return f -> r.atDamageGive(f, this.damageTypeForTurn);
	}
	
	private UnaryOperator<Float> adr(AbstractPower r) {
		return f -> r.atDamageReceive(f, this.damageTypeForTurn);
	}
	
	private UnaryOperator<Float> adfg(AbstractPower r) {
		return f -> r.atDamageFinalGive(f, this.damageTypeForTurn);
	}
	
	private UnaryOperator<Float> adfr(AbstractPower r) {
		return f -> r.atDamageFinalReceive(f, this.damageTypeForTurn);
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		boolean monsterPower = m != null && m.powers != null;
		this.isBlockModified = false;
		float blc = this.baseBlock;

		this.isDamageModified = false;
		float tmp = this.baseDamage;

		if (!this.upgraded) {
			int x = this.energyOnUse == -1 ? EnergyPanel.totalCount : this.energyOnUse;
			if (p().hasRelic("Chemical X"))
				x += 2;
			blc = tmp = x;
		}

		// 防御
		blc = chain(p().powers.stream().map(this::blo)).apply(blc);
		if (this.baseBlock != MathUtils.floor(blc)) {
			this.isBlockModified = true;
		}
		if (blc < 0.0F) {
			blc = 0.0F;
		}
		this.block = MathUtils.floor(blc);

		// 攻击
		tmp = chain(p().relics.stream().map(this::adm)).apply(tmp);
		tmp = chain(p().powers.stream().map(this::adg)).apply(tmp);
		tmp = p().stance.atDamageGive(tmp, this.damageTypeForTurn, this);
		if (monsterPower) {
			tmp = chain(m.powers.stream().map(this::adr)).apply(tmp);
		}
		tmp = chain(p().powers.stream().map(this::adfg)).apply(tmp);
		if (monsterPower) {
			tmp = chain(m.powers.stream().map(this::adfr)).apply(tmp);
		}
		if (this.baseDamage != (int) tmp) {
			this.isDamageModified = true;
		}
		if (tmp < 0.0F) {
			tmp = 0.0F;
		}
		this.damage = MathUtils.floor(tmp);
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upDesc();
            this.upgradeBlock(1);
            this.upgradeDamage(1);
        }
    }
}