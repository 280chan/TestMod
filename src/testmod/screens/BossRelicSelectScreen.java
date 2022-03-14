package testmod.screens;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import testmod.relics.Sins;

import java.util.Collection;
import java.util.stream.Stream;

public class BossRelicSelectScreen extends RelicSelectScreen {
	public static final String[] ILLEGAL = {"Pandora's Box", "Calling Bell"};
	
	private String bd, t, d;
	
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
		this.bd = bDesc;
		this.t = title;
		this.d = desc;
	}

	public BossRelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip) {
		super(c, canSkip);
	}
	
	public BossRelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip, String bDesc, String title, String desc) {
		super(c, canSkip, bDesc, title, desc);
	}

	private boolean checkIllegal(String id) {
		return Stream.of(ILLEGAL).anyMatch(id::equals);
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

	private void checkNext(boolean select) {
		if (Sins.screenQueue > 0) {
			Sins.screenQueue--;
			this.rejectSelection = select;
			new BossRelicSelectScreen(true, this.bd, this.t, this.d).open();
		} else {
			Sins.isSelect = true;
		}
	}
	
	@Override
	protected void afterSelected() {
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
				this.selectedRelic);
		this.checkNext(true);
	}

	@Override
	protected void afterCanceled() {
		this.checkNext(false);
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