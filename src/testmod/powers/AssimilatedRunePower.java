package testmod.powers;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class AssimilatedRunePower extends AbstractTestPower {
	private boolean upgraded = false;
	
	public AssimilatedRunePower(AbstractCreature owner, int amount, boolean upgraded) {
		this.ID += (this.upgraded = upgraded);
		if (this.upgraded) {
			this.name += "+";
		}
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1) + desc(this.upgraded ? 3 : 2) + desc(4);
	}
	
	private boolean test(AbstractPower p) {
		return ((AssimilatedRunePower)p).upgraded;
	}
	
	private boolean isActive() {
		return upgraded || p().powers.stream().filter(p -> p instanceof AssimilatedRunePower).noneMatch(this::test);
	}
	
	private int getValue(AbstractCard c, boolean atk) {
		return atk ? c.baseDamage : c.baseBlock;
	}
	
	private int maxIn(Stream<AbstractCard> s, boolean atk) {
		return s.mapToInt(c -> this.getValue(c, atk)).max().orElse(-999);
	}
	
	private Stream<AbstractCard> getList() {
		Stream<CardGroup> s = Stream.of(p().hand);
		if (this.upgraded) {
			s = Stream.concat(s, Stream.of(p().drawPile, p().discardPile));
		}
		return s.flatMap(g -> g.group.stream());
	}
	
    public float atDamageGive(final float damage, final DamageType type) {
    	return type == DamageType.NORMAL && isActive() ? Math.max(this.maxIn(this.getList(), true), damage) : damage;
    }
    
    public void atEndOfTurn(final boolean isPlayer) {
    	if (isPlayer) {
    		this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
    	}
    }
    
    public float modifyBlock(final float blockAmount) {
    	return isActive() ? this.maxIn(this.getList(), false) : blockAmount;
    }

    
}
