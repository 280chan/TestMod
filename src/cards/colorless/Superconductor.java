package cards.colorless;

import cards.AbstractTestCard;
import powers.SuperconductorNoEnergyPower;
import powers.SuperconductorPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.ApplyBulletTimeAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class Superconductor extends AbstractTestCard {

    public Superconductor() {
        super(Superconductor.class, -1, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new ApplyBulletTimeAction());
		this.addTmpXCostActionToBot(this,
				f(this::addToTop, e -> new ApplyPowerAction(p, p, new SuperconductorPower(p, e), e)));
		this.addToBot(new ApplyPowerAction(p, p, new SuperconductorNoEnergyPower(p, EnergyPanel.totalCount)));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.upDesc();
        }
    }
}