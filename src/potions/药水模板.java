package potions;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.WeakPower;

import mymod.TestMod;

/**
 * @deprecated
 */
public class 药水模板 extends AbstractTestPotion {
	public static final String POTION_ID = TestMod.makeID("药水ID");
	//*
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	/*/
	private static final String NAME = "Test药水";
	private static final String[] DESCRIPTIONS = {"测试描述( #b", " )"};
	//*/

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
		this.addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new WeakPower(target, this.potency, false),
				this.potency));
	}

	public int getPotency(int ascensionLevel) {
		return 3;
	}
}
