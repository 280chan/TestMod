
package deprecated.cards;

import basemod.abstracts.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

/**
 * @deprecated
 */
public class FakeCard extends CustomCard {
	private AbstractCard c;
	
    public FakeCard(AbstractCard c) {
        super(c.cardID, c.name, c.assetUrl, c.cost, c.rawDescription, c.type, c.color, c.rarity, c.target);
        this.baseBlock = c.baseBlock;//基础格挡值. this.block为有敏捷等加成的格挡值.
        this.baseDamage = c.baseDamage;//基础伤害值. this.damage为有力量、钢笔尖等加成的伤害值.
        this.baseMagicNumber = c.baseMagicNumber;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = c.magicNumber;
        
        this.isEthereal = c.isEthereal;//虚无属性，false不虚无，true虚无。可在该类里调用改变。不虚无就可以赋值为false或者删掉这一行
        this.exhaust = c.exhaust;//消耗属性，false不消耗，true消耗。可在该类里调用改变。不消耗就可以赋值为false或者删掉这一行
        this.isInnate = c.isInnate;//固有属性，false不固有，true固有。可在该类里调用改变。不固有就可以赋值为false或者删掉这一行

		this.upgraded = c.upgraded;
		this.timesUpgraded = c.timesUpgraded;
		this.costForTurn = c.costForTurn;
		this.isCostModified = c.isCostModified;
		this.isCostModifiedForTurn = c.isCostModifiedForTurn;
		this.misc = c.misc;
		this.freeToPlayOnce = c.freeToPlayOnce;
		
		this.purgeOnUse = true;
		this.c = c;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.c.use(p, m);
    }
    
    public void upgrade() {
    }
}