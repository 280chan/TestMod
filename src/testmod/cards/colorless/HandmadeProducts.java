package testmod.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import basemod.abstracts.CustomSavable;
import testmod.cards.AbstractTestCard;

public class HandmadeProducts extends AbstractTestCard implements CustomSavable<Integer> {
	private static final int DELTA_COST = 1;

	@Override
	public void onLoad(Integer savedCost) {
		this.upgradeBaseCost(savedCost == null ? this.cost : savedCost);
	}

	@Override
	public Integer onSave() {
		return this.cost;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.SLASH_DIAGONAL));
			m.damage(new DamageInfo(p, this.damage, this.damageTypeForTurn));
			if ((m.isDying || m.currentHealth <= 0) && !m.halfDead) {
				p.masterDeck.group.stream().filter(c -> c.uuid.equals(this.uuid)).forEach(this::modify);
				GetAllInBattleInstances.get(this.uuid).forEach(this::modify);
			}
			if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
			}
		});
	}
	
	private void modify(AbstractCard c) {
		if (c.cost > -1)
			c.updateCost(DELTA_COST);
		c.applyPowers();
		c.baseDamage = 0;
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		this.baseDamage = this.product();
		super.calculateCardDamage(m);
	}
	
	public void applyPowers() {
		this.baseDamage = this.product();
		super.applyPowers();
	}
	
	public int product() {
		return p().hand.group.stream().filter(c -> c.cost > -2)
				.map(c -> c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn).map(a -> a + this.magicNumber)
				.reduce(1, (a, b) -> a * b);
	}
	
	public boolean canUpgrade() {
		return true;
	}
	
	public void upgrade() {
		this.upgraded = true;
		this.name = this.name() + "+" + ++this.timesUpgraded;
		this.upgradeBaseCost(1);
		this.initializeTitle();
	}
	
	protected void upgradeBaseCost(int newBaseCost) {
		int diff = this.costForTurn - this.cost;
		this.cost = newBaseCost;
		if (this.costForTurn >= 0) {
			this.costForTurn = this.cost + diff;
		}
		if (this.costForTurn < 0) {
			this.costForTurn = 0;
		}
		this.upgradedCost = true;
	}
}