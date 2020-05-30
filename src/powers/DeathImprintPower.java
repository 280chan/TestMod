package powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class DeathImprintPower extends AbstractPower {
	public static final String POWER_ID = "DeathImprintPower";
	public static final String NAME = "死亡刻印";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "每当失去生命时，增加与伤害相等的层数。";
	public static final String[] DESCRIPTIONS = {"每当失去生命时，增加失去生命点数的层数。","已失去 #b"," 点生命。"};
	
	public DeathImprintPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.DEBUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.owner.name + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
	}

	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
	}

	public int onAttacked(DamageInfo info, int damage) {
		if (damage > 0) {
			if (damage < 5 && AbstractDungeon.player.hasRelic("Boot") && info.type == DamageType.NORMAL) {
				this.amount += 5;
			} else {
				this.amount += damage;
			}
			this.updateDescription();
		}
		return damage;
	}

}
