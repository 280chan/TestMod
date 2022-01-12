package powers;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import mymod.TestMod;

public class AssimilatedRunePower extends AbstractTestPower {
	public static final String POWER_ID = "AssimilatedRunePower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private boolean upgraded = false;
	
	public AssimilatedRunePower(AbstractCreature owner, int amount, boolean upgraded) {
		super(POWER_ID);
		this.name = NAME;
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
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		 this.description += this.upgraded ? DESCRIPTIONS[3] : DESCRIPTIONS[2];
		 this.description += DESCRIPTIONS[4];
	}
	
	private boolean isActive() {
		return this.upgraded || !AbstractDungeon.player.hasPower(TestMod.makeID(POWER_ID) + true);
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
    	return type == DamageType.NORMAL && isActive() ? this.maxIn(this.getList(), true) : damage;
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
