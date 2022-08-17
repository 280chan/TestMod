package testmod.cards.colorless;

import java.util.ArrayList;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import testmod.cards.AbstractTestCard;

public class RabbitOfFibonacci extends AbstractTestCard {
    private static final int BASE_DMG = 1;
    private static final int BASE_BLK = 1;
    private static final double ROOT5 = Math.sqrt(5);
    private static final double LOG_PHI = Math.log((ROOT5 + 1) / 2);
    private static final ArrayList<RabbitOfFibonacci> PREVIEW = new ArrayList<RabbitOfFibonacci>();
    
    public RabbitOfFibonacci() {
        this(false);
		MultiCardPreview.add(this, getPreview(1), getPreview(2), getPreview(3));
    }
	
	private RabbitOfFibonacci getPreview(int n) {
		int old = PREVIEW.size();
		for (int i = old; i < n; i++) {
			RabbitOfFibonacci tmp = new RabbitOfFibonacci(true);
			for (int j = 0; j < i + 1; j++) {
				tmp.upgrade();
			}
			PREVIEW.add(tmp);
		}
		return PREVIEW.get(n - 1);
	}
    
    private RabbitOfFibonacci(boolean preview) {
        super(2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL);
        this.baseDamage = BASE_DMG;
        this.baseBlock = BASE_BLK;
        this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(new GainBlockAction(p, p, this.block));
		this.atb(new DamageAllEnemiesAction(p, this.multiDamage, damageTypeForTurn, AttackEffect.SLASH_HORIZONTAL));
		this.addTmpActionToBot(() -> GetAllInBattleInstances.get(this.uuid).forEach(c -> c.upgrade()));
    }

    public boolean canUpgrade() {
    	return true;
    }
    
    private boolean overflow() {
    	return this.timesUpgraded > 44;
    }
    
    private static int f(int n) {
    	int sign = (n % 2 == 0) ? -1 : 1;
    	double tmp = Math.exp(LOG_PHI * n);
    	return (int) ((tmp + sign / tmp) / ROOT5 + 0.5);
    }
    
    public void upgrade() {
    	this.name = this.name() + "+" + ++this.timesUpgraded;
    	this.upgraded = this.upgradedDamage = this.upgradedBlock = true;
        this.initializeTitle();
        this.baseDamage = this.baseBlock = this.overflow() ? 2147450000 : f(this.timesUpgraded + 2);
		ArrayList<AbstractCard> l = MultiCardPreview.multiCardPreview.get(this);
		if (!PREVIEW.contains(this) && l != null && !l.isEmpty())
			l.add(getPreview(l.remove(0).timesUpgraded + 3));
    }
}