package potions;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.WeakPower;

import mymod.TestMod;

/**
 * @deprecated
 */
public class 药水模板 extends AbstractPotion {
	public static final String POTION_ID = TestMod.makeID("药水ID");
	public static final String NAME = "药水名称";
	public static final String[] DESCRIPTIONS = {};

	public 药水模板() {
		super(NAME, POTION_ID, PotionRarity.COMMON, PotionSize.SPHERE, PotionColor.WEAK);
		this.potency = getPotency();
		this.description = this.getDesc();
		this.isThrown = true;
		this.targetRequired = true;
		this.tips.add(new PowerTip(this.name, this.description));
	}

	public String getDesc() {
		return null;
	}

	public void use(AbstractCreature target) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, AbstractDungeon.player,
				new WeakPower(target, this.potency, false), this.potency));
	}

	public AbstractPotion makeCopy() {
		return new 药水模板();
	}

	public int getPotency(int ascensionLevel) {
		return 3;
	}
}
