
package testmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;

public abstract class AbstractEquivalentableCard extends AbstractTestCard {
	
	public AbstractEquivalentableCard(String id, String name, int cost, String desc, CardType type, CardRarity rarity,
			CardTarget target) {
		super(id, name, cost, desc, type, rarity, target);
	}

	private AbstractCard makeSameInstance() {
		AbstractCard c = super.makeSameInstanceOf();
		this.giveStatsTo(c);
		return c;
	}
	
	private AbstractCard makeMoreEquivalentCopy() {
		AbstractCard c = super.makeStatEquivalentCopy();
		this.giveStatsTo(c);
		return c;
	}
	
	private void giveStatsTo(AbstractCard c) {
		c.baseDiscard = this.baseDiscard;
		c.baseDraw = this.baseDraw;
		c.baseHeal = this.baseHeal;
		c.chargeCost = this.chargeCost;
		c.damage = this.damage;
		c.discard = this.discard;
		c.dontTriggerOnUseCard = this.dontTriggerOnUseCard;
		c.draw = this.draw;
		c.exhaust = this.exhaust;
		c.exhaustOnFire = this.exhaustOnFire;
		c.exhaustOnUseOnce = this.exhaustOnUseOnce;
		c.heal = this.heal;
		c.isBlockModified = this.isBlockModified;
		c.isDamageModified = this.isDamageModified;
		c.isEthereal = this.isEthereal;
		c.isFlipped = this.isFlipped;
		c.isGlowing = this.isGlowing;
		c.isInnate = this.isInnate;
		c.isMagicNumberModified = this.isMagicNumberModified;
		c.magicNumber = this.magicNumber;
		c.rawDescription = this.rawDescription;
		c.showEvokeOrbCount = this.showEvokeOrbCount;
		c.showEvokeValue = this.showEvokeValue;
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		return this.makeMoreEquivalentCopy();
	}
	
	public AbstractCard makeSameInstanceOf() {
		return this.makeSameInstance();
	}
	
}