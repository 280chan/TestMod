
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.SuperconductorAction;

import com.megacrit.cardcrawl.dungeons.*;

public class Superconductor extends CustomCard {
    public static final String ID = "Superconductor";
    public static final String NAME = "超导体";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "本回合内：无法获得能量，手牌中所有牌耗能变为0。接下来抽到的前X张耗能大于0的牌的耗能在回合内降低1。 消耗 。";
    public static final String UPGRADED_DESCRIPTION = "本回合内：无法获得能量，手牌中所有牌耗能变为0。接下来抽到的前X张耗能大于0的牌的耗能在回合内降低1。";
    private static final int COST = -1;

    public Superconductor() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new SuperconductorAction(p, this.freeToPlayOnce, this.energyOnUse));//本回合内所有卡牌消耗为0
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}