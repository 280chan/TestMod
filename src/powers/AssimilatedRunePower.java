package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class AssimilatedRunePower extends AbstractPower {
	public static final String POWER_ID = "AssimilatedRunePower";
	public static final String NAME = "同化";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = { "在 #b", " 回合内，将当前手牌中所有牌的基础伤害、基础格挡提升到", " #y当前手", " #y你所有", "牌 中的最高值。" };

	private boolean upgraded = false;
	
	public AssimilatedRunePower(AbstractCreature owner, int amount, boolean upgraded) {
		this.name = NAME;
		this.ID = POWER_ID + upgraded;
		this.upgraded = upgraded;
		if (this.upgraded) {
			this.name += "+";
		}
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		 if (this.upgraded) {
			 this.description += DESCRIPTIONS[3];
		 } else {
			 this.description += DESCRIPTIONS[2];
		 }
		 this.description += DESCRIPTIONS[4];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	private boolean isActive() {
		return this.upgraded || !AbstractDungeon.player.hasPower(POWER_ID + true);
	}
	
	private int getValue(AbstractCard c, boolean atk) {
		if (atk)
			return c.baseDamage;
		return c.baseBlock;
	}
	
	private int maxIn(ArrayList<AbstractCard> list, boolean atk) {
		int max = -999;
		for (AbstractCard c : list) {
			if (max < this.getValue(c, atk)) {
				max = this.getValue(c, atk);
			}
		}
		return max;
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
    	if (type == DamageType.NORMAL && isActive())
    		return this.maxIn(this.getList(), true);
    	return damage;
    }
    
    public void atEndOfTurn(final boolean isPlayer) {
    	if (isPlayer) {
    		AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
    	}
    }
    
    public float modifyBlock(final float blockAmount) {
    	if (isActive())
    		return this.maxIn(this.getList(), false);
    	return blockAmount;
    }
    
}
