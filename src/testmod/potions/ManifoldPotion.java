package testmod.potions;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen.CurScreen;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;

import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.mymod.TestMod;

public class ManifoldPotion extends AbstractTestPotion {
	public static final String POTION_ID = TestMod.makeID("ManifoldPotion");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private static AbstractPotion last;
	private boolean isInLab = false;

	public static void load() {
		String id = TestMod.getString(POTION_ID);
		last = "".equals(id) ? null : PotionHelper.getPotion(id);
	}

	private static void save() {
		TestMod.save(POTION_ID, last == null ? "" : last.ID);
	}
	
	public ManifoldPotion() {
		super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.SPHERE, PotionColor.WHITE);
		this.setEffect(PotionEffect.RAINBOW);
		this.isThrown = false;
		this.targetRequired = false;
		if (last != null) {
			copyStat(this);
		}
	}

	public String getDesc() {
		return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1]
				+ (last == null || this.isInLab ? "" : DESCRIPTIONS[2] + "#y" + last.name + " NL " + last.description);
	}
	
	private boolean isInLab() {
		return CardCrawlGame.mainMenuScreen.screen == CurScreen.POTION_VIEW;
	}
	
	public void update() {
		super.update();
		if (!this.isInLab && (this.isInLab = this.isInLab()))
			this.initializeData();
	}

	public void use(AbstractCreature target) {
		for (int i = 0; i < this.potency; i++) {
			last.use(target);
		}
	}

	public int getPotency(int ascensionLevel) {
		return 1;
	}
	
	public boolean canUse() {
		return last != null && last.canUse();
	}
	
	private static boolean isReplica(AbstractPotion p) {
		return p instanceof ManifoldPotion;
	}
	
	private static void copyStat(AbstractPotion p) {
		p.isThrown = last.isThrown;
		p.targetRequired = last.targetRequired;
		p.initializeData();
	}

	@SpirePatches(value = { @SpirePatch(clz = PotionPopUp.class, method = "updateTargetMode"),
			@SpirePatch(clz = PotionPopUp.class, method = "updateInput") })
	public static class PotionUsePatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(PotionPopUp __instance) {
			AbstractPotion p = ReflectionHacks.getPrivate(__instance, PotionPopUp.class, "potion");
			if (!isReplica(p)) {
				last = p;
				save();
				INSTANCE.p().potions.stream().filter(ManifoldPotion::isReplica).forEach(ManifoldPotion::copyStat);
			}
		}
	}
	
	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPotion.class, "use");
			int[] raw = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			return new int[] { raw[0] };
		}
	}
}
