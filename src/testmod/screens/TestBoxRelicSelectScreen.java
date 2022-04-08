package testmod.screens;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import christmasMod.mymod.ChristmasMod;
import halloweenMod.relics.EventCelebration_Halloween;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.relics.TestBox;
import testmod.utils.MiscMethods;

public class TestBoxRelicSelectScreen extends RelicSelectScreen implements MiscMethods {
	public static final String[] ILLEGAL = {TestMod.makeID("TestBox")};
	private TestBox box;
	
	public TestBoxRelicSelectScreen(boolean canSkip, String bDesc, String title, String desc, TestBox box) {
		super(canSkip, bDesc, title, desc);
		this.box = box;
		this.box.relicSelected = false;
	}

	private boolean checkIllegal(AbstractTestRelic r) {
		return checkIllegal(r.relicId);
	}
	
	private boolean checkIllegal(String id) {
		return Stream.of(ILLEGAL).anyMatch(id::equals);
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
		Predicate<String> check = s -> TestMod.checkHash(CardCrawlGame.playerName, s);
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
		}
		ArrayList<AbstractTestRelic> l = TestMod.RELICS.stream().map(r -> RelicLibrary.getRelic(r.relicId))
				.filter(r -> !r.isSeen).map(r -> (AbstractTestRelic)r).collect(toArrayList());
		return l.size() == 0 ? null : TestMod.randomItem(l, this.box.rng);
	}
	
	private AbstractTestRelic randomRelic(AbstractTestRelic priority) {
		return randomRelic(priority != null);
	}
	
	private AbstractTestRelic randomRelic(boolean priority) {
		return (AbstractTestRelic) TestMod.randomItem((priority ? TestMod.BAD_RELICS : TestMod.RELICS), this.box.rng);
	}
	
	private void addAndMarkAsSeen(AbstractRelic r) {
		this.relics.add(r);
		this.markAsSeen(r);
	}
	
	@Override
	protected void addRelics() {
		AbstractTestRelic priority = this.priority();
		if (priority != null)
			addAndMarkAsSeen(priority);
		for (AbstractTestRelic r = randomRelic(priority); this.relics.size() < 3; r = randomRelic(priority))
			if (!(this.checkIllegal(r) || this.relics.stream().anyMatch(r::sameAs)))
				addAndMarkAsSeen(r);
	}

	private void completeSelection() {
		this.box.relicSelected = true;
		p().relics.remove(this.box);
		p().reorganizeRelics();
	}
	
	@Override
	protected void afterSelected() {
		AbstractRelic tmp = this.selectedRelic.makeCopy();
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, tmp);
		tmp.onEquip();
		TestMod.removeFromPool(tmp);
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