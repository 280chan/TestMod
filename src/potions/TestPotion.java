package potions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import actions.TestPotionAction;
import mymod.TestMod;
import utils.MiscMethods;

public class TestPotion extends AbstractTestPotion implements MiscMethods {
	public static final String POTION_ID = TestMod.makeID("TestPotion");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;

	public TestPotion() {
		super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.CARD, PotionColor.NONE);
		this.isThrown = false;
	}

	public String getDesc() {
		return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
	}

	public void use(AbstractCreature target) {
		for (int i = 0; i < this.potency; i++) {
			this.addToBot(new AbstractGameAction(){
				@Override
				public void update() {
					this.isDone = true;
					InputHelper.moveCursorToNeutralPosition();
				    this.addToTop(new TestPotionAction(TestMod.CARDS));
				}});
		}
	}

	public AbstractPotion makeCopy() {
		return new TestPotion();
	}

	public int getPotency(int ascensionLevel) {
		return 1;
	}
}
