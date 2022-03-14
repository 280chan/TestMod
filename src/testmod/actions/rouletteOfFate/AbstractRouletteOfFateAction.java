package testmod.actions.rouletteOfFate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractRouletteOfFateAction extends AbstractGameAction  {
	
	public enum Type {
		DEBUFF, ATTACK, NORMAL, SPECIAL, CONDITION, POWER;
	}
	
	public enum TargetRange {
		SINGLE, ALL, RANDOM;
	}
	
	public enum SpecialType {
		HEAVY_BLADE;
		
		public boolean attackOnly() {
			switch(this) {
			case HEAVY_BLADE:
				return true;
			default:
				return false;
			}
		}
		
		public String desc() {
			switch(this) {
			case HEAVY_BLADE:
				return " 力量 在这张牌上有 !M! 倍效果。";
			default:
				return "为什么没有分类？";
			}
		}
	}
	
	public enum Condition {
		DRAWN, DISCARDED, EXHAUSTED, ATTACKED, LOSE_HP, PLAYED, PLAYED_OTHER, DRAW_OTHER, DISCARD_OTHER, EXHAUST_OTHER; 
	
		public boolean attackOnly() {
			switch(this) {
			default:
				return false;
			}
		}
	}
	
	public Type TYPE;
	public CardType cardType;
	public TargetRange range;
	public int effectTimes;
	public SpecialType special;
	public Condition condition;
	public AbstractRouletteOfFateAction conditionAction;
	
	public void changeTarget(AbstractCreature t) {
		this.target = t;
	}
	
	protected String rangePrefix() {
		if (range == TargetRange.RANDOM)
			return "对随机敌人";
		else if (range == TargetRange.ALL)
			return "对所有敌人";
		return "";
	}
	
	protected String attackString() {
		return "造成 !D! 点伤害";
	}
	
	protected abstract String effectString();
	
	protected String timesPostfix() {
		if (effectTimes == 1)
			return "";
		return effectTimes + "次";
	}
	
	protected String condition() {
		return "";
	}
	
	public String description() {
		if (this.TYPE == Type.CONDITION)
			return this.condition() + this.conditionAction.description();
		String tmp = this.rangePrefix();
		if (this.TYPE == Type.ATTACK)
			tmp += this.attackString();
		else 
			tmp += this.effectString();
		return tmp + this.timesPostfix();
	}
	
	public String toString() {
		return this.description();
	}

}
