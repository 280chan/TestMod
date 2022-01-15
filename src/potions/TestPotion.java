package potions;

import java.util.ArrayList;
import java.util.Collections;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import basemod.BaseMod;
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

	public void initializeData() {
		this.potency = getPotency();
		this.description = getDesc();
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.description));
	}
	
	public String getDesc() {
		return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
	}
	
	private ArrayList<AbstractCard> cards() {
		ArrayList<AbstractCard> list = TestMod.CARDS.stream().collect(this.toArrayList());
		Collections.shuffle(list, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));
		return list.stream().limit(3).collect(this.toArrayList());
	}

	public void use(AbstractCreature target) {
		for (int i = 0; i < this.potency; i++) {
			this.addTmpActionToBot(() -> {
				InputHelper.moveCursorToNeutralPosition();
				AbstractDungeon.cardRewardScreen.customCombatOpen(cards(), CardRewardScreen.TEXT[1], true);
				this.addTmpActionToTop(() -> {
					if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
						AbstractCard card = AbstractDungeon.cardRewardScreen.discoveryCard.makeCopy();
						card.setCostForTurn(0);
						card.current_x = (-1000.0F * Settings.scale);
						AbstractDungeon.effectList.add(AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE
								? new ShowCardAndAddToHandEffect(card) : new ShowCardAndAddToDiscardEffect(card));
						AbstractDungeon.cardRewardScreen.discoveryCard = null;
					}
				});
			});
		}
	}

	public AbstractPotion makeCopy() {
		return new TestPotion();
	}

	public int getPotency(int ascensionLevel) {
		return 1;
	}
}
