
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.IllusoryAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class Illusory extends CustomCard {
    public static final String ID = "Illusory";
    public static final String NAME = "虚无缥缈";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String[] DESCRIPTIONS = { "抽 !M! 张牌，然后将抽牌堆中所有牌附加 虚无 。其中每有一张牌已有 虚无 ，获得", "。 消耗 。 虚无 。" };
	private static final String[] E = { " [R] ", " [G] ", " [B] ", " [W] " };
    private static final int COST = 1;
    private static final int BASE_ENG = 1;
    private static final int BASE_MGC = 1;

    public Illusory() {
        super(TestMod.makeID(ID), NAME, IMG, COST, getDescription(BASE_ENG), CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
    }

	public static String getDescription(int mgc) {
		String e = E[0];
		if (AbstractDungeon.player != null) {
			switch (AbstractDungeon.player.chosenClass) {
			case WATCHER:
				e = E[3];
				break;
			case DEFECT:
				e = E[2];
				break;
			case THE_SILENT:
				e = E[1];
			default:
			}
		}
		return DESCRIPTIONS[0] + e + DESCRIPTIONS[1];
	}
    
	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
			if (c.isEthereal) {
				this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
				break;
			}
		}
	}
	
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
    	AbstractDungeon.actionManager.addToBottom(new IllusoryAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}