package testmod.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

import christmasMod.mymod.ChristmasMod;
import halloweenMod.relics.EventCelebration_Halloween;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.relics.TestBox;
import testmod.relicsup.AbstractUpgradedRelic;
import testmod.relicsup.AllUpgradeRelic;
import testmod.relicsup.TestBoxUp;
import testmod.utils.MiscMethods;

public class TestBoxRelicSelectScreen extends RelicSelectScreen implements MiscMethods {
	public static final String[] ILLEGAL = { TestMod.makeID("TestBox") };
	private Random rng;
	private boolean boxUp = false;
	private HashMap<String, AbstractRelic> original = new HashMap<String, AbstractRelic>();
	
	public TestBoxRelicSelectScreen(String bDesc, String title, String desc, Random rng, boolean up) {
		super(true, bDesc, title, desc);
		this.rng = rng;
		this.boxUp = up;
	}

	private boolean checkIllegal(AbstractRelic r) {
		return Stream.of(ILLEGAL).anyMatch(r.relicId::equals);
	}
	
	private AbstractTestRelic priority() {
		int month = this.getMonth(); 
		int date = this.getDate(); 
		//TestMod.info("月:" + month + "日:" + date);
		if ((month == 10 && date > 25) || (month == 11 && date < 6))
			return new EventCelebration_Halloween();
		// TODO 圣诞
		if (month == 12 && date > 20)
			return (AbstractTestRelic) ChristmasMod.randomRelic();
		/*Predicate<String> check = s -> TestMod.checkHash(CardCrawlGame.playerName, s);
		if (!Settings.seedSet) {
			if (Stream.of("1023dba2e158f257fba87f85d932b1df69c1989dc87c14389787a681f056cc5e",
						"c870968e2499df3ec4a1e386c21f19628af4cef6e5aaa8aa6da2071ab1fba5e4",
						"a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
						"342840f6340d15691f4be1c0e0157fb0983992c4f436c18267d41dbe6bb74a2")
						.anyMatch(check)) {
				Object o = TestMod.checkLatest(true);
				if (o != null)
					return (AbstractTestRelic) o;
			}
		} else if (Loader.isModLoaded("chronoMods")
				&& Stream.of("c870968e2499df3ec4a1e386c21f19628af4cef6e5aaa8aa6da2071ab1fba5e4",
						"a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
						"342840f6340d15691f4be1c0e0157fb0983992c4f436c18267d41dbe6bb74a2")
						.anyMatch(check)) {
			Object o = TestMod.checkLatest(true);
			if (o != null)
				return (AbstractTestRelic) o;
		}*/
		ArrayList<AbstractTestRelic> l = TestMod.RELICS.stream().map(r -> RelicLibrary.getRelic(r.relicId))
				.filter(r -> !r.isSeen).map(r -> (AbstractTestRelic) r).collect(toArrayList());
		return Loader.isModLoaded("chronoMods") || l.size() == 0 ? null : TestMod.randomItem(l, this.rng);
	}

	private boolean rollUpgrade(AbstractRelic r) {
		return this.boxUp && rng.nextDouble() < (r.tier == RelicTier.BOSS || r.tier == RelicTier.SPECIAL ? 0.05 : 0.5);
	}
	
	private boolean valid() {
		return Stream.of("c870968e2499df3ec4a1e386c21f19628af4cef6e5aaa8aa6da2071ab1fba5e4",
				"a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
				"342840f6340d15691f4be1c0e0157fb0983992c4f436c18267d41dbe6bb74a2")
				.anyMatch(s -> TestMod.checkHash(CardCrawlGame.playerName, s));
	}
	
	private void addAndMarkAsSeen(AbstractRelic r) {
		this.markAsSeen(r);
		AbstractRelic tmp;
		if (Loader.isModLoaded("RelicUpgradeLib") && this.rollUpgrade(r)) {
			tmp = AllUpgradeRelic.getUpgrade(r);
			original.put(tmp.relicId, r);
			this.relics.add(tmp);
			return;
		} else if (valid() && this.rollUpgrade(r)) {
			tmp = ((AbstractTestRelic) r).upgrade();
			original.put(tmp.relicId, r);
			this.relics.add(tmp);
			return;
		}
		this.relics.add(r);
	}
	
	@Override
	protected void addRelics() {
		AbstractTestRelic pri = this.priority();
		ArrayList<AbstractRelic> l = new ArrayList<AbstractRelic>();
		ArrayList<AbstractRelic> result = new ArrayList<AbstractRelic>();
		l.addAll(TestMod.RELICS);
		l.removeIf(this::checkIllegal);
		if (pri != null) {
			l.removeIf(pri::sameAs);
			result.add(pri);
		}
		Collections.shuffle(l, this.rng);
		l.stream().limit((this.boxUp ? 5 : 3) - (pri == null ? 0 : 1)).forEach(result::add);
		l.clear();
		result.forEach(this::addAndMarkAsSeen);
		result.clear();
	}

	private void completeSelection() {
		p().relics.remove(
				(this.boxUp ? relicStream(TestBoxUp.class) : relicStream(TestBox.class)).findFirst().orElse(null));
		p().reorganizeRelics();
		original.clear();
	}
	
	@Override
	protected void afterSelected() {
		AbstractRelic tmp = this.selectedRelic.makeCopy();
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, tmp);
		tmp.onEquip();
		TestMod.removeFromPool(tmp instanceof AbstractUpgradedRelic ? original.get(tmp.relicId) : tmp);
		this.completeSelection();
	}

	@Override
	protected void afterCanceled() {
		this.completeSelection();
	}

	@Override
	protected String categoryOf(AbstractRelic r) {
		return null;
	}

	@Override
	protected String descriptionOfCategory(String category) {
		return null;
	}
}