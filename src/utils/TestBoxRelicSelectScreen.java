package utils;

import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import christmasMod.mymod.ChristmasMod;
import halloweenMod.relics.EventCelebration_Halloween;
import mymod.TestMod;
import relics.AbstractTestRelic;
import relics.TestBox;

public class TestBoxRelicSelectScreen extends RelicSelectScreen implements MiscMethods {
	public static final String[] ILLEGAL = {TestMod.makeID("TestBox")};
	private TestBox box;
	
	public TestBoxRelicSelectScreen(boolean canSkip, String bDesc, String title, String desc, TestBox box) {
		super(canSkip, bDesc, title, desc);
		this.box = box;
		this.box.relicSelected = false;
	}

	private boolean checkIllegal(AbstractRelic r) {
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
			return (AbstractTestRelic) ChristmasMod.randomRelic().makeCopy();
		if (!Settings.seedSet) {
			if ("BrkStarshine".equals(CardCrawlGame.playerName) || "280 chan".equals(CardCrawlGame.playerName)) {
				Object o = TestMod.checkLatest(true);
				if (o != null)
					return (AbstractTestRelic) o;
			}
		} else if (Loader.isModLoaded("chronoMods") && "BrkStarshine".equals(CardCrawlGame.playerName)) {
			Object o = TestMod.checkLatest(true);
			if (o != null)
				return (AbstractTestRelic) o;
		}
		return null;
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
		AbstractDungeon.player.relics.remove(this.box);
		AbstractDungeon.player.reorganizeRelics();
	}
	
	@Override
	protected void afterSelected() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				this.selectedRelic);
		this.selectedRelic.onEquip();
		TestMod.removeFromPool(this.selectedRelic);
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