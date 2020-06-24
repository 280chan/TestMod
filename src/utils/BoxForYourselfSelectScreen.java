package utils;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import events.BoxForYourself;

public class BoxForYourselfSelectScreen extends RelicSelectScreen {
	
	public BoxForYourselfSelectScreen(String bDesc, String title, String desc) {
		super(false, bDesc, title, desc);
	}

	@Override
	protected void addRelics() {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			this.relics.add(r.makeCopy());
	}

	private void sendIndexSelected(int index) {
		BoxForYourself.receiveIndexSelected(index);
	}
	
	@Override
	protected void afterSelected() {
		this.sendIndexSelected(this.relics.indexOf(this.selectedRelic));
	}

	@Override
	protected void afterCanceled() {
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