package christmasMod.cards;

import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.CuriosityPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.relics.CultistMask;

public class GiftKakaa extends AbstractChristmasCard {
    public static final String ID = "GiftKakaa";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 1;
    
    public GiftKakaa() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new ApplyPowerAction(p, p, new CuriosityPower(p, this.magicNumber), this.magicNumber));
		if (this.upgraded) {
			this.addToBot(new ApplyPowerAction(p, p, new RitualPower(p, this.magicNumber, true), this.magicNumber));
		}
		this.addToBot(new SFXAction("VO_CULTIST_1A"));
		this.addToBot(new TalkAction(true, new CultistMask().DESCRIPTIONS[1], 1.0F, 2.0F));
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}