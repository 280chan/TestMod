package testmod.potions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PotionStrings;

import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public class TimePotion extends AbstractTestPotion implements MiscMethods {
	public static final String POTION_ID = TestMod.makeID("TimePotion");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;

	public TimePotion() {
		super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.GHOST, PotionColor.SMOKE);
		this.isThrown = true;
	}

	public String getDesc() {
		return DESCRIPTIONS[0];
	}

	public void use(AbstractCreature target) {
		this.skipMonsterIntent();
	}

	public int getPotency(int ascensionLevel) {
		return 0;
	}
}
