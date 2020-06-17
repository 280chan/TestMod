package relics;

import com.megacrit.cardcrawl.helpers.input.InputHelper;

public abstract class AbstractDoubleClickableRelic extends AbstractTestRelic {
	private boolean RclickStart;
	private boolean Rclick;
	private boolean dCheck;
	
	private long lastClick;
	private static final int DURATION = 300;	// 双击间隔，间隔时间内没有第二次右键，判断为单击

	public AbstractDoubleClickableRelic(String id, RelicTier tier, LandingSound sfx) {
		super(id, tier, sfx);
		this.Rclick = false;
		this.RclickStart = false;
		this.dCheck = false;
	}

	protected abstract void onRightClick();

	protected void onDoubleRightClick() {
	}
	
	private long deltaTime() {
		return System.currentTimeMillis() - this.lastClick;
	}
	
	private boolean doubleClick() {
		boolean b = this.deltaTime() < DURATION;
		this.lastClick = System.currentTimeMillis();
		return b;
	}
	
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
		if (this.deltaTime() >= DURATION && this.dCheck) {
			this.dCheck = false;
			this.onRightClick();
		}
		if ((this.Rclick)) {
			this.Rclick = false;
			this.dCheck = true;
			if (this.doubleClick()) {
				this.onDoubleRightClick();
			}
		}
	}

}
