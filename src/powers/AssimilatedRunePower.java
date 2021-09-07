package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
		this.ID += upgraded;
		this.upgraded = upgraded;
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
	
	private int maxIn(ArrayList<AbstractCard> list, boolean atk) {
		return list.stream().map(c -> {return this.getValue(c, atk);}).max(Integer::max).orElse(-999);
	}
	
	private ArrayList<AbstractCard> getList() {
		ArrayList<AbstractCard> tmp = new ArrayList<AbstractCard>();
		tmp.addAll(AbstractDungeon.player.hand.group);
		if (this.upgraded) {
			tmp.addAll(AbstractDungeon.player.drawPile.group);
			tmp.addAll(AbstractDungeon.player.discardPile.group);
		}
		return tmp;
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
