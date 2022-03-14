package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.SuperconductorNoEnergyPower;
import testmod.powers.SuperconductorPower;

import com.megacrit.cardcrawl.actions.unique.ApplyBulletTimeAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class Superconductor extends AbstractTestCard {

    public Superconductor() {
        super(-1, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.atb(new ApplyBulletTimeAction());
		this.addTmpXCostActionToBot(this, e -> this.att(this.apply(p, new SuperconductorPower(p, e))));
		this.atb(this.apply(p, new SuperconductorNoEnergyPower(p, EnergyPanel.totalCount)));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.upDesc();
        }
    }
}