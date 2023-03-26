package testmod.potions;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;

public class BarkSap extends AbstractTestPotion {
	public static final String POTION_ID = TestMod.makeID("BarkSap");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final ArrayList<AbstractPotion> POTIONS = new ArrayList<AbstractPotion>();
	private static int usedAmount = 0;
	private static int lastLoad = 0;

	public static void clear() {
		lastLoad = usedAmount = 0;
		save();
		POTIONS.clear();
	}
	
	public static void load() {
		if (lastLoad > 0) {
			POTIONS.removeAll(POTIONS.stream().limit(lastLoad).collect(MISC.toArrayList()));
		}
		usedAmount = TestMod.getInt(POTION_ID);
		POTIONS.forEach(AbstractPotion::initializeData);
		lastLoad = POTIONS.size();
	}

	private static void save() {
		TestMod.save(POTION_ID, usedAmount);
	}
	
	public BarkSap() {
		super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.BOTTLE, PotionColor.GREEN);
	}

	public String getDesc() {
		return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
	}
	
	public boolean canUse() {
		if (AbstractDungeon.actionManager.turnHasEnded && (AbstractDungeon.getCurrRoom()).phase == RoomPhase.COMBAT) {
			return false;
		}
		if (AbstractDungeon.getCurrRoom().event != null && AbstractDungeon.getCurrRoom().event instanceof WeMeetAgain) {
			return false;
		}
		return true;
	}

	public void use(AbstractCreature target) {
		if (this.inCombat()) {
			this.atb(apply(p(), new BarkSapPower()));
			this.addTmpActionToBot(() -> POTIONS.forEach(AbstractPotion::initializeData));
		} else {
			usedAmount += this.potency;
			POTIONS.stream().filter(p -> !(p instanceof BarkSap)).forEach(AbstractPotion::initializeData);
		}
	}

	public int getPotency(int ascensionLevel) {
		return 1;
	}
	
	private static class BarkSapPower extends AbstractTestPower {
		public BarkSapPower() {
			this.owner = p();
			this.amount = 1;
			updateDescription();
			this.type = PowerType.BUFF;
		}

		public void updateDescription() {
			this.description = desc(0) + (1 << this.amount) + desc(1);
		}
		
		public void onVictory() {
			this.amount = 0;
			POTIONS.forEach(AbstractPotion::initializeData);
		}
	}

	@SpirePatch(clz = AbstractPotion.class, method = "getPotency", paramtypez = {})
	public static class PotionUsePatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "potency" })
		public static void Insert(AbstractPotion __instance, @ByRef int[] potency) {
			if (MISC.inCombat()) {
				potency[0] <<= MISC.p().powers.stream().filter(p -> p instanceof BarkSapPower && p.amount > 0)
						.mapToInt(p -> p.amount).sum();
			}
		}
		
		public static int Postfix(int _return, AbstractPotion __instance) {
			return __instance instanceof BarkSap || MISC.p() == null ? _return : _return + usedAmount;
		}
	}
	
	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "player");
			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
		}
	}
	
	@SpirePatch(clz = SaveAndContinue.class, method = "save")
	public static class SaveAndContinuePatch {
		public static void Postfix(SaveFile save) {
			save();
		}
	}

	@SpirePatch(clz = AbstractPotion.class, method = "<ctor>", paramtypez = { String.class, String.class,
			PotionRarity.class, PotionSize.class, PotionColor.class })
	public static class NewPotionPatch {
		public static void Postfix(AbstractPotion __instance, String name, String id, PotionRarity rarity,
				PotionSize size, PotionColor color) {
			if (!POTIONS.contains(__instance))
				POTIONS.add(__instance);
		}
	}

	@SpirePatch(clz = AbstractPotion.class, method = "<ctor>", paramtypez = { String.class, String.class,
			PotionRarity.class, PotionSize.class, PotionEffect.class, Color.class, Color.class, Color.class })
	public static class NewPotionPatch1 {
		public static void Postfix(AbstractPotion __instance, String name, String id, PotionRarity rarity,
				PotionSize size, PotionEffect effect, Color liquidColor, Color hybridColor, Color spotsColor) {
			if (!POTIONS.contains(__instance))
				POTIONS.add(__instance);
		}
	}
	
}
