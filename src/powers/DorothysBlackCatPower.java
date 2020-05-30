package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class DorothysBlackCatPower extends AbstractPower {
	public static final String POWER_ID = "DorothysBlackCatPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "桃乐丝的黑猫";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每当你对敌人造成 #r失去生命  #y以外类型 的伤害时，使所有敌人均摊失去总共 #b","% 其数值的生命。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	
	public DorothysBlackCatPower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (m.hasPower(DorothysBlackCatEnemyPower.POWER_ID)) {
				this.updateAmount(m);
			} else {
				this.addEnemyPower(m);
			}
		}
	}
	
	private void updateAmount(AbstractMonster m) {
		AbstractPower p = m.getPower(DorothysBlackCatEnemyPower.POWER_ID);
		p.stackPower(this.amount - p.amount);
		p.updateDescription();
	}
	
	private void addEnemyPower(AbstractMonster m) {
		m.powers.add(new DorothysBlackCatEnemyPower(m, this.amount));
	}
	
	private void removeEnemyPower(AbstractMonster m) {
		AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(m, this.owner, DorothysBlackCatEnemyPower.POWER_ID));
	}
	
	public void onInitialApplication() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			this.addEnemyPower(m);
		}
	}
	
	public void atStartOfTurn() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.hasPower(DorothysBlackCatEnemyPower.POWER_ID)) {
				this.addEnemyPower(m);
			}
		}
	}
	
	public void onRemove() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			this.removeEnemyPower(m);
		}
	}
    
}
