package relics;

import com.megacrit.cardcrawl.helpers.input.InputHelper;

public abstract class AbstractClickRelic extends AbstractTestRelic {
	private boolean RclickStart;
	private boolean Rclick;

	public AbstractClickRelic(String id, RelicTier tier, LandingSound sfx) {
		super(id, tier, sfx);
		this.Rclick = false;
		this.RclickStart = false;
	}

	protected abstract void onRightClick();

	@Override
	public void update() {
		super.update();
		if (this.RclickStart && InputHelper.justReleasedClickRight) {
			if (this.hb.hovered) {
				this.Rclick = true;
			}
			this.RclickStart = false;
		}
		if ((this.isObtained) && (this.hb != null) && ((this.hb.hovered) && (InputHelper.justClickedRight))) {
			this.RclickStart = true;
		}
		if ((this.Rclick)) {
			this.Rclick = false;
			this.onRightClick();
		}
	}

}
