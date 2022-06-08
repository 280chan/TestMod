package testmod.cards.colorless;

import java.util.ArrayList;
import java.util.function.Function;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import testmod.cards.AbstractTestCard;

public class PowerStrike extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public void initializeDescription() {
		if (this.magicNumber > 0) {
			int mod = magicNumber % 10, whole = 1 + magicNumber / 10;
			this.rawDescription = exDesc()[0]
					+ (mod == 0 ? whole : (magicNumber < 10 ? 1 + ". !M! " : whole + "." + mod)) + exDesc()[1];
		}
    	super.initializeDescription();
    }
    
    public PowerStrike() {
        super(3, CardType.SKILL, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.exhaust = this.isEthereal = true;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.tags.add(CardTags.STRIKE);
    }

	private void modify(AbstractMonster m) {
		int tmp = (int) (Math.log(m.currentHealth) / Math.log(1 + Math.max(this.magicNumber, 1) / 10.0));
		if (tmp < m.currentHealth && tmp >= 0)
			m.currentHealth = tmp;
	}

    public void use(final AbstractPlayer p, final AbstractMonster monster) {
    	this.addTmpActionToBot(() -> {
    		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().monsters == null)
    			return;
    		if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead())
    			return;
    		ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
    		ArrayList<AbstractMonster> kill = new ArrayList<AbstractMonster>();
    		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
    			if (m.isDead || m.isDying || m.isEscaping || m.escaped)
    				continue;
    			this.modify(m);
    			if (m.currentHealth > 0) {
    				list.add(m);
    				m.healthBarUpdatedEvent();
    			} else {
    				kill.add(m);
    			}
    		}
    		if (list.size() == 0) {
    			this.addTmpActionToTop(AbstractDungeon.actionManager::clearPostCombatActions);
    		} else {
    			list.stream().map(createIntangible(list.size())).forEach(po -> this.att(apply(p, po)));
    		}
    		kill.forEach(m -> this.att(new InstantKillAction(m)));
    		list.clear();
    		kill.clear();
    	});
    }
    
    private Function<AbstractMonster, IntangiblePower> createIntangible(int amount) {
    	return m -> {
    		IntangiblePower p = new IntangiblePower(m, amount);
			p.atEndOfTurn(false);
			return p;
    	};
    }
    
    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	this.upgraded = true;
    	this.name = this.name() + "+" + ++this.timesUpgraded;
        this.initializeTitle();
        this.upgradeMagicNumber(1);
		this.initializeDescription();
    }
}