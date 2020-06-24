package utils;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import events.PlateOfNloth;

public class PlateOfNlothSelectScreen extends RelicSelectScreen {
	
	public PlateOfNlothSelectScreen(String bDesc) {
		super(true, 1, false);
		this.setDescription(bDesc, null, null);
	}

	@Override
	protected void addRelics() {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			this.relics.add(r.makeCopy());
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
			return "Boss";
		case COMMON:
			return "普通";
		case DEPRECATED:
			return "废弃";
		case RARE:
			return "稀有";
		case SHOP:
			return "商店";
		case SPECIAL:
			return "事件";
		case STARTER:
			return "初始";
		case UNCOMMON:
			return "罕见";
		}
		return "其他";
	}

	@Override
	protected String descriptionOfCategory(String category) {
		switch (category) {
		case "Boss":
			return "只在Boss宝箱中出现的遗物。(999)";
		case "普通":
			return "很容易找到的弱小遗物。(150)";
		case "稀有":
			return "极为少见的独特且强大的遗物。(300)";
		case "商店":
			return "只能从商人处购买到的遗物。(150)";
		case "事件":
			return "只能通过事件获得的遗物。(400)";
		case "初始":
			return "角色初始携带的遗物。(300)";
		case "罕见":
			return "比普通遗物更强大也更少见的遗物。(250)";
		}
		return "未被分类的特殊遗物。(-1)";
	}
}