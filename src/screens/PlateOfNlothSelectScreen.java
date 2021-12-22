package screens;

import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import events.PlateOfNloth;
import utils.MiscMethods;

public class PlateOfNlothSelectScreen extends RelicSelectScreen implements MiscMethods {
	private static final UIStrings UI = INSTANCE.uiString();
	
	public PlateOfNlothSelectScreen(String bDesc) {
		super(true, 1, false);
		this.setDescription(bDesc, null, null);
	}

	@Override
	protected void addRelics() {
		p().relics.stream().map(r -> r.makeCopy()).forEach(this.relics::add);
	}

	private void sendIndexSelected(int index) {
		PlateOfNloth.receiveIndexSelected(index);
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
		switch(r.tier) {
		case BOSS:
			return UI.TEXT[0];
		case COMMON:
			return UI.TEXT[1];
		case DEPRECATED:
			return UI.TEXT[2];
		case RARE:
			return UI.TEXT[3];
		case SHOP:
			return UI.TEXT[4];
		case SPECIAL:
			return UI.TEXT[5];
		case STARTER:
			return UI.TEXT[6];
		case UNCOMMON:
			return UI.TEXT[7];
		}
		return UI.TEXT[8];
	}

	@Override
	protected String descriptionOfCategory(String category) {
		if (category.equals(UI.TEXT[0]))
			return UI.TEXT[9];
		if (category.equals(UI.TEXT[1]))
			return UI.TEXT[10];
		if (category.equals(UI.TEXT[3]))
			return UI.TEXT[11];
		if (category.equals(UI.TEXT[4]))
			return UI.TEXT[12];
		if (category.equals(UI.TEXT[5]))
			return UI.TEXT[13];
		if (category.equals(UI.TEXT[6]))
			return UI.TEXT[14];
		if (category.equals(UI.TEXT[7]))
			return UI.TEXT[15];
		return UI.TEXT[16];
	}
}