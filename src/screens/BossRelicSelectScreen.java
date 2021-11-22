package screens;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import relics.Sins;

import java.util.Collection;

public class BossRelicSelectScreen extends RelicSelectScreen {
	public static final String[] ILLEGAL = {"Pandora's Box", "Calling Bell"};
	
	public BossRelicSelectScreen() {
		super();
	}
	
	public BossRelicSelectScreen(boolean canSkip) {
		super(canSkip);
	}

	public BossRelicSelectScreen(Collection<? extends AbstractRelic> c) {
		super(c);
	}
	
	public BossRelicSelectScreen(String bDesc, String title, String desc) {
		super(bDesc, title, desc);
	}
	
	public BossRelicSelectScreen(boolean canSkip, String bDesc, String title, String desc) {
		super(canSkip, bDesc, title, desc);
	}

	public BossRelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip) {
		super(c, canSkip);
	}
	
	public BossRelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip, String bDesc, String title, String desc) {
		super(c, canSkip, bDesc, title, desc);
	}

	private boolean checkIllegal(String id) {
		for (String s : ILLEGAL)
			if (id.equals(s))
				return true;
		return false;
	}
	
	@Override
	protected void addRelics() {
		while (this.relics.size() < 3) {
			AbstractRelic r = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS);
			if (this.checkIllegal(r.relicId))
				continue;
			this.relics.add(r);
			UnlockTracker.markRelicAsSeen(r.relicId);
		}
	}

	@Override
	protected void afterSelected() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				this.selectedRelic);
		Sins.isSelect = true;
	}

	@Override
	protected void afterCanceled() {
		Sins.isSelect = true;
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