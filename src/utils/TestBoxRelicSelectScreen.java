package utils;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import christmasMod.mymod.ChristmasMod;
import halloweenMod.relics.EventCelebration_Halloween;
import mymod.TestMod;
import relics.TestBox;

public class TestBoxRelicSelectScreen extends RelicSelectScreen implements MiscMethods {
	public static final String[] ILLEGAL = {TestMod.makeID("TestBox")};
	private TestBox box;
	
	public TestBoxRelicSelectScreen(boolean canSkip, String bDesc, String title, String desc, TestBox box) {
		super(canSkip, bDesc, title, desc);
		this.box = box;
		this.box.relicSelected = false;
	}

	private boolean checkIllegal(String id) {
		for (String s : ILLEGAL)
			if (id.equals(s))
				return true;
		return false;
	}
	
	private AbstractRelic priority() {
		int month = this.getMonth(); 
		int date = this.getDate(); 
		//TestMod.info("月:" + month + "日:" + date);
		if ((month == 10 && date > 25) || (month == 11 && date < 6))
			return new EventCelebration_Halloween();
		// TODO 圣诞
		if (month == 12 && date > 20)
			return ChristmasMod.randomRelic();
		if (!Settings.seedSet) {
			if ("BrkStarshine".equals(CardCrawlGame.playerName) || "280 chan".equals(CardCrawlGame.playerName)) {
				Object o = TestMod.checkLatest(true);
				if (o != null)
					return (AbstractRelic) o;
			}
		}
		return null;
	}
	
	@Override
	protected void addRelics() {
		AbstractRelic priority = this.priority();
		if (priority != null)
			this.relics.add(priority);
		while (this.relics.size() < 3) {
			boolean repeat = false;
			AbstractRelic r = TestMod.randomItem(TestMod.RELICS, this.box.rng);
			if (this.checkIllegal(r.relicId))
				continue;
			for (AbstractRelic re : this.relics) {
				if (re.relicId.equals(r.relicId))
					repeat = true;
			}
			if (repeat)
				continue;
			this.relics.add(r);
			UnlockTracker.markRelicAsSeen(r.relicId);
		}
	}

	@Override
	protected void afterSelected() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				this.selectedRelic);
		TestMod.removeFromPool(this.selectedRelic);
		this.box.relicSelected = true;
	}

	@Override
	protected void afterCanceled() {
		this.box.relicSelected = true;
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